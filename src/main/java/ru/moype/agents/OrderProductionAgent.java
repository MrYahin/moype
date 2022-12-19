package ru.moype.agents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sf.jade4spring.JadeBean;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import ru.moype.config.SpringContext;
import ru.moype.model.NomLinks;
import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.service.OrderProductionService;
import ru.moype.service.StageService;

public class OrderProductionAgent extends Agent{

	//Содержит актуальный список агентов с их статусами
	Map<String, String> stageStatus = new HashMap();
	Map<String, Stage> stages = new HashMap();
	Date orderStartDate;
	Date orderCompleteDate;
	int requestCnt = 0; // The counter of replies from ordersP agents
	int repliesCnt = 0; // The counter of replies from ordersP agents
	String state;

	Vector stageAgents = new Vector();
	Vector criticalPathAgents = new Vector();
	OrderProduction order;
	private String mode;
	boolean processing = false;
	String evId;

	JadeBean jadeBean = SpringContext.getBean(JadeBean.class);
	OrderProductionService orderProdService = SpringContext.getBean(OrderProductionService.class);
	StageService stageService = SpringContext.getBean(StageService.class);

	//Initialization
	public void setup() {
		
		Object[] args = getArguments();
		order  = (OrderProduction)args[0];
		mode = order.getMode();
		state = order.getState();
		orderStartDate = order.getStartDate();
		orderCompleteDate = order.getCompleteDate();

		System.out.println("Create agent: "+ getAID().getLocalName() + " " + order.getNumber());

		registerServices();//Register point as service-------------------------------------------------------------------

		initStages();

		initCritPathAgent();

		System.out.println("Agent production: "+ getAID().getLocalName() + " is active.");
	  	
		// Update the list of active stage agents every minute
		addBehaviour(new TickerBehaviour(this, 1000) {

				protected void onTick() {
					// Update the list of orderProd agents
					DFAgentDescription template = new DFAgentDescription();		
					ServiceDescription sd = new ServiceDescription();
					sd.setType("precondition" + order.getOrderId());								
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent,	template);
						stageAgents.clear();
						for (int i = 0; i < result.length; ++i) {
							stageAgents.addElement(result[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}
				}
			});

		addBehaviour(new PlaningManager(this)); //order action
		
	}

	//dying
	protected void takeDown()  {
		try { DFService.deregister(this); }
		catch (Exception e) {}
	}

	private class PlaningManager extends TickerBehaviour {
		
		private PlaningManager(Agent a) {
			super(a, 500); // tick 0.5 sek
		}
		
		public void onTick() {
			if (state.equals("finish")) {
				stop();
			}
			else {
				myAgent.addBehaviour(new PlaningNegotiator(this));
			}
		}
	}

	private class PlaningNegotiator extends Behaviour {
		
		private PlaningManager manager;

		private MessageTemplate mt; // The template to receive replies
		List<Stage> stageList;

		//Создаем время жизни этого цикла, т.к. могут быть зависания
		long t = System.currentTimeMillis();

		public PlaningNegotiator(PlaningManager m) {
			super(null); //?
			this.manager 		= m;
		}
		int step = 0;

		public void action() {
			switch (step) {
				case 0: //режим планирования

					if (requestCnt != repliesCnt){
						step = 1;
						break;
					}

					if (state.equals("plan") && processing == false){
						step = 1;
						break;
					}

					stageList = orderProdService.getStageToPlanList(order.getOrderId(), mode);
					if (stageList.size() == 0){
						state = "plan";
						step = 1;
						break;
					}

					processing = true;

					requestCnt = 0;
					repliesCnt = 0;

					String batch = "";
					for (Stage stage: stageList) {
						boolean startCalc = true;
						if (!batch.equals(stage.getBatch())) {
							if (!stage.getState().equals("plan")) {
								requestCnt = requestCnt + (int)searchAndRequestStage(myAgent, stage, startCalc);
									//if (requestCnt == 1){step = 1; break;} //temp
									batch = stage.getBatch();
									//if (needChangeMode){
							}
						}
					}
					step = 1;
					break;
				case 1: //Этап ожидания изменений
					// Receive all status from stage agents
					ACLMessage reply = myAgent.receive();
					if (reply != null) {
						if (reply.getPerformative() == ACLMessage.INFORM) {
							inform_waiting_process(reply);
							repliesCnt++;
						}
						if (reply.getPerformative() == ACLMessage.REQUEST) {
							request_waiting_process(reply, myAgent);
						}
					}

					//if (repliesCnt == requestCnt) {
					//	state = "plan";
					//}

					if (!order.getState().equals(state)){
						order.setState(state);
						order.setCompleteDate(orderCompleteDate);
						orderProdService.register(order);
						processing = false;

						startCriticalPathAgent(myAgent);
					}

					if (reply == null) {
						step = 2;
						break;
					}
			}
		}
		public boolean done() {
			return step == 2;  //Если присвоить верное значение, агент перестанет работать
		}
	}

	public long searchAndRequestStage(Agent myAgent, Stage stage, boolean startCalc){

		Date startDate = orderStartDate;
		Date completeDate = orderCompleteDate;

		long requestCnt = 0;

		//Найти этап
		String topic = "" + stage.getNumber() + "_" + stage.getBatch();
		DFAgentDescription templateToRequest = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("precondition" + order.getOrderId());
		sd.setName(topic);
		templateToRequest.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, templateToRequest);
			stageAgents.clear();
			for (int i = 0; i < result.length; ++i) {
				stageAgents.addElement(result[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		ACLMessage msgP = new ACLMessage(ACLMessage.REQUEST);
		for (int i = 0; i < stageAgents.size(); ++i) {
			msgP.addReceiver((AID) stageAgents.elementAt(i));
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (mode.equals("0")) { //ASAP
			if (stage.getNumber() == 1) {
				//Вхождения
				List<NomLinks> inputs = stageService.getInputs(stage.getIdStage());
				for (NomLinks input: inputs) {
					if (input.getProduced()) {
						List<Stage> inputStages = orderProdService.getStageById(input.getStageIdInput());
						for (Stage inStage : inputStages) {
							if (inStage.getState().equals("new")) {
								startCalc = false;
							} else if (startDate.before(inStage.getPlanFinishDate())) {
								startDate = inStage.getPlanFinishDate();
							}
						}
					}
				}
				//начало не ранее заданной даты
				if (stage.getNotEarlier() != null && startDate.before(stage.getNotEarlier())){
					startDate = stage.getNotEarlier();
				}

				String mStringArray[] = {"mode:" + mode, "date:" + formatter.format(startDate)};
				JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
				msgP.setContent(String.valueOf(mJSONArray));
			} else {
				List<Stage> prevStageList = new ArrayList<Stage>();
				prevStageList = orderProdService.getStage(order.getOrderId(), stage.getNumber() - 1, stage.getBatch());
				for (Stage pStage: prevStageList) {
					if (!pStage.getState().equals("new")) {
						if (startDate.before(pStage.getPlanFinishDate())) {
							startDate = pStage.getPlanFinishDate();
						}
						String mStringArray[] = {"mode:" + mode, "date:" + formatter.format(startDate)};
						JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
						msgP.setContent(String.valueOf(mJSONArray));
					}
				}
			}
		}
		else { //JIT
			if (stage.getNextNumber() == 0) {
				//Вхождения
				List<NomLinks> outputs = stageService.getOutputs(stage.getIdStage());
				for (NomLinks output: outputs){
					List<Stage> outputStages = orderProdService.getStageById(output.getStageId());
					for (Stage outStage: outputStages) {
						if (outStage.getState().equals("new")) {
							startCalc = false;
						} else if (completeDate.after(outStage.getPlanStartDate())) {
							completeDate = outStage.getPlanStartDate();
						}
					}
				}
				String mStringArray[] = {"mode:" + mode, "date:" + formatter.format(completeDate)};
				JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
				msgP.setContent(String.valueOf(mJSONArray));
			} else {
				List<Stage> nextStageList = orderProdService.getStage(order.getOrderId(), stage.getNextNumber(), stage.getBatch());
				for (Stage nextStage: nextStageList){
					if (nextStage.getState().equals("plan")) {
						String mStringArray[] = {"mode:" + mode, "date:" + formatter.format(nextStage.getPlanStartDate())};
						JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
						msgP.setContent(String.valueOf(mJSONArray));
					}
				}
			}
		}
		msgP.setConversationId("StartPlanning");
		//cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
		if (startCalc) {
			myAgent.send(msgP);
			System.out.println(msgP.toString());
			requestCnt++;
		}
		// Prepare the template to get proposals

		return requestCnt;
	}

	public long searchAndStopStage(Agent myAgent, Stage stage){

		long requestCnt = 0;

		//Найти этап
		String topic = "" + stage.getNumber() + "_" + stage.getBatch();
		DFAgentDescription templateToRequest = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("precondition" + order.getOrderId());
		sd.setName(topic);
		templateToRequest.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, templateToRequest);
			stageAgents.clear();
			for (int i = 0; i < result.length; ++i) {
				stageAgents.addElement(result[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		ACLMessage msgP = new ACLMessage(ACLMessage.REQUEST);
		for (int i = 0; i < stageAgents.size(); ++i) {
			msgP.addReceiver((AID) stageAgents.elementAt(i));
		}
		msgP.setConversationId("stop");
		myAgent.send(msgP);
		System.out.println(msgP.toString());
		requestCnt++;

		return requestCnt;
	}

	public void inform_waiting_process(ACLMessage reply){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (reply.getConversationId() == "CompletePlanning") {
			Date start = null;
			Date finish = null;
			try {
				Map<String, String> responce = parseResponceStage(reply.getContent());
				String idStage = responce.get("idStage");
				start = formatter.parse(responce.get("start"));
				finish = formatter.parse(responce.get("finish"));
				if (orderCompleteDate.before(finish)){
					orderCompleteDate = finish;
				}

				stages.remove(idStage);
				stageStatus.put(idStage, "done");
			} catch (ParseException | JSONException e) {
				e.printStackTrace();
			}

			if (mode.equals("1") && start.before(new Date())) {
				//needChangeMode = true;
				mode = "0";
				order.setMode(mode); //if JIT not possible
				orderStartDate = new Date();
				order.setStartDate(orderStartDate);
				orderProdService.register(order);
				orderProdService.updateStagesToPlan(order.getOrderId());
				//needChangeMode = false;
				List<Stage> stageList = orderProdService.getStageList(order.getOrderId());
				for(Stage stage: stageList) {
					String stateStage = stage.getState();
					stages.put(stage.getIdStage(), stage);
					//Очистка загрузки ресурсов
					//ВРЦ
					if (stateStage.equals("new")) {
						orderProdService.deleteResGroupLoad(stage.getIdStage());
					}
				}
				return;
			}
		}
		if (reply.getConversationId() == "refusePlanning") {}
	}

	public void request_waiting_process(ACLMessage reply, Agent myAgent){
		if (reply.getConversationId() == "startOrder") {
			if (!processing) {
				ACLMessage replyEv = reply.createReply();
				replyEv.setPerformative(ACLMessage.INFORM);
				replyEv.setContent(state);
				myAgent.send(replyEv);
				System.out.println(replyEv.toString());
			}
		}
		if (reply.getConversationId() == "stopAllAgents") {
			if (stageAgents.size() == 0) {
				ACLMessage replyEv = reply.createReply();
				replyEv.setPerformative(ACLMessage.INFORM);
				replyEv.setContent(state);
				myAgent.send(replyEv);
				System.out.println(replyEv.toString());
				doDelete();
			}
			else {
				List<Stage> stageList = orderProdService.getStageList(order.getOrderId());
				if (!processing) {
					for(Stage stage: stageList){
						orderProdService.deleteResGroupLoad(stage.getIdStage());
					}
					processing = true;
				}
				for(Stage stage: stageList) {
					searchAndStopStage(myAgent, stage);
				}
				ACLMessage replyEv = reply.createReply();
				replyEv.setPerformative(ACLMessage.INFORM);
				replyEv.setContent("new");//Отдаем новый статус, что ничего не поменялось
				myAgent.send(replyEv);
				System.out.println(replyEv.toString());
			}
		}
		if (reply.getConversationId() == "replanOrder") {
			String replyEvId = reply.getContent();
			if (!replyEvId.equals(evId)){ //Событие обрабатывается в первый раз
				state = "replan";
				order.setState(state);
				orderProdService.updateOrderStatus(order);
				orderProdService.updateStagesToPlan(order.getOrderId());
				evId = replyEvId;
			}
			if (!processing){
				ACLMessage replyEv = reply.createReply();
				replyEv.setPerformative(ACLMessage.INFORM);
				if (state.equals("replan")){replyEv.setContent("new");}
				else {replyEv.setContent(state);};
				myAgent.send(replyEv);
				System.out.println(replyEv.toString());
			}
		}

	}

	public void startCriticalPathAgent(Agent myAgent){
		//Найти агента критического пути
		String topic = "criticalPath" + order.getOrderId();
		DFAgentDescription templateToRequest = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("criticalPath");
		sd.setName(topic);
		templateToRequest.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, templateToRequest);
			criticalPathAgents.clear();
			for (int i = 0; i < result.length; ++i) {
				criticalPathAgents.addElement(result[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		ACLMessage msgP = new ACLMessage(ACLMessage.REQUEST);
		for (int i = 0; i < criticalPathAgents.size(); ++i) {
			msgP.addReceiver((AID) criticalPathAgents.elementAt(i));
		}
		msgP.setConversationId("calcCriticalPath");
		myAgent.send(msgP);
		System.out.println(msgP.toString());
	}

	public void registerServices(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("orderProduction" + order.getOrderId());
		sd.setName("orderProduction" + order.getOrderId());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	public void initStages(){

		List<Stage> stageList = orderProdService.getStageList(order.getOrderId());

		for(Stage stage: stageList){
			String stateStage = stage.getState();
			stages.put(stage.getIdStage(), stage);
			//Очистка загрузки ресурсов
			//ВРЦ
			if (stateStage.equals("new")){
				orderProdService.deleteResGroupLoad(stage.getIdStage());
			}

			if ((stateStage == null) || !stateStage.equals("finish")){

				try {
					Object argsJ[] = new Object[1];
					argsJ[0]= stage;
					jadeBean.startAgent("stage:" + stage.getNumber() + "_" + stage.getId(),  "ru.moype.agents.StageAgent", argsJ);
				} catch (Exception e) {
					System.out.println("не получилось создать агента этапа.");
				}
			}
		}
	}

	public void initCritPathAgent(){
		try {
			Object argsJ[] = new Object[1];
			argsJ[0]= order;
			jadeBean.startAgent("orderCriticalPath:" + order.getOrderId(),  "ru.moype.agents.OrderCriticalPathAgent", argsJ);
		} catch (Exception e) {
			System.out.println("ошибка создания агента критического пути");
		}
	}

	Map<String, String> parseResponceStage(String msgContent) throws JSONException {

		Map<String, String> result = new HashMap<String, String>();

		JSONArray the_json_array = new JSONArray(msgContent);
		for (int i = 0; i < the_json_array.length(); i++) {
			if (the_json_array.getString(i).contains("idStage")) {
				result.put("idStage", the_json_array.getString(i).substring(8));
			}
			if (the_json_array.getString(i).contains("start")) {
				result.put("start", the_json_array.getString(i).substring(6));
			}
			if (the_json_array.getString(i).contains("finish")) {
				result.put("finish", the_json_array.getString(i).substring(7));
			}
		}
		return result;
	}

}




