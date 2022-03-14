package ru.moype.resources;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import ru.moype.config.SpringContext;
import ru.moype.model.NomLinks;
import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.service.OrderProductionService;
import ru.moype.service.StageService;

public class OrderProductionAgent extends Agent{

	//Содержит актуальный список агентов с их статусами
	private Map<String, String> stageStatus = new HashMap();
	private Map<String, Stage> stages = new HashMap();
	private Date startDate;
	private Date completeDate;
//	private boolean processingRequest;
//	private boolean processingRequest;
	private int step = 0;
	private int requestCnt = 0; // The counter of replies from ordersP agents
	private int repliesCnt = 0; // The counter of replies from ordersP agents

	private Vector stageAgents = new Vector();
	private Vector criticalPathAgents = new Vector();
	private OrderProduction order;
	private String state;
	String mode;

	JadeBean jadeBean = SpringContext.getBean(JadeBean.class);
	OrderProductionService orderProdService = SpringContext.getBean(OrderProductionService.class);
	StageService stageService = SpringContext.getBean(StageService.class);

	//Initialization
	public void setup() {
		
//		Stage stage;

		Object[] args = getArguments();
		order  = (OrderProduction)args[0];
		mode = order.getMode();
		state = order.getState();
		startDate = order.getStartDate();
		completeDate = order.getCompleteDate();

		System.out.println("Create agent: "+ getAID().getLocalName() + " " + order.getNumber());
		System.out.println("My GUID is "+ getAID().getName());
		System.out.println("My addresses are:");
		Iterator it = getAID().getAllAddresses();
		while (it.hasNext()) {
			System.out.println("- "+it.next());
		}

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
					jadeBean.startAgent("stage:" + stage.getNumber() + "_" + stage.getId(),  "ru.moype.resources.StageAgent", argsJ);
				} catch (Exception e) {
					System.out.println("не получилось создать агента этапа.");
				}
			}
		}


		//создание агента критического пути
		if (stageList.size() > 0){
			try {
				Object argsJ[] = new Object[1];
				argsJ[0]= order;
				jadeBean.startAgent("orderCriticalPath:" + order.getOrderId(),  "ru.moype.resources.OrderCriticalPathAgent", argsJ);
			} catch (Exception e) {
				System.out.println("ошибка создания агента критического пути");
			}
		}
	  	
		System.out.println("Agent production: "+ getAID().getLocalName() + " is active.");
	  	
		// Update the list of active stage agents every minute
		addBehaviour(new TickerBehaviour(this, 60000) {

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

	private class PlaningManager extends TickerBehaviour {
		
		private PlaningManager(Agent a) {
			super(a, 1000); // tick 1 sek
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
		
		private String number;
		//private String wayPoint;

		private PlaningManager manager;
//		private AID bestSeller; 	// The seller agent who provides the best offer
//		private int bestPrice; 		// The best offered price
		private MessageTemplate mt; // The template to receive replies
		List<Stage> stageList;



		//Создаем время жизни этого цикла, т.к. могут быть зависания
		long t= System.currentTimeMillis();
		long end = t+15000; //15 сек

		public PlaningNegotiator(PlaningManager m) {
			super(null); //?
			this.manager 		= m;
		}
	
		public void action() {
			switch (step) {
			case 0: //режим планирования
				//if (processing) {break;}

				//processing = true;
				if (state.equals("plan")){
					step = 2;
					break;
				}
				//	Stage nextStage;
				requestCnt = 0;
				repliesCnt = 0;
				step = 1;

				stageList = orderProdService.getStageToPlanList(order.getOrderId(), order.getMode());
				if (stages.size() == 0){
					state = "plan";
					step = 2;
					break;
				}

				String codeNom = "";
				for (Stage stage: stageList) {
					boolean startCalc = true;
					if (!codeNom.equals(stage.getCodeNom())) {
						if (!stage.getState().equals("plan")) {
							requestCnt = requestCnt + (int)searchAndRequestStage(myAgent, stage, startCalc, startDate, completeDate);
							//if (requestCnt == 1){step = 1; break;} //temp
							codeNom = stage.getCodeNom();
						}
					}
				}
				break;
			case 1: //Этап ожидания изменений
				// Receive all status from stage agents
				//mt = MessageTemplate.MatchConversationId("completePlanning");
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.INFORM) {
						if (reply.getConversationId() == "CompletePlanning") {
							// Reply received
							repliesCnt++;

							String idStage = reply.getContent();
							stages.remove(idStage);
							stageStatus.put(idStage, "done");
							//Удалить из массива

							//Если в массиве есть позиции, то шаг 0 иначе завершаем
							if (stages.size() == 0) {
								state = "plan";
								step = 2;
								break;
							}
						}
						if (reply.getConversationId() == "refusePlanning") {
							repliesCnt++;
						}
						if (repliesCnt == requestCnt) {
							step = 0;
							//processing = false;
						}
					}
				}

				//step = 3;
				//block();
				break;
				case 2:
					if (state.equals("plan")){
						order.setState(state);
						orderProdService.register(order);

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
						step = 3;
						break;
					}
			}
		}
		public boolean done() {
			return step == 3;  //Если присвоить верное значение, агент перестанет работать
		}
	}

	public long searchAndRequestStage(Agent myAgent, Stage stage, boolean startCalc, Date startDate, Date completeDate){

		long requestCnt = 0;

		//Найти этап
		String topic = "" + stage.getNumber() + "_" + stage.getCodeNom();
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
					List<Stage> inputStages = orderProdService.getStageById(input.getStageIdInput());
					for (Stage inStage: inputStages) {
						if (inStage.getState().equals("new")) {
							startCalc = false;
						} else if (startDate.before(inStage.getPlanFinishDate())) {
							startDate = inStage.getPlanFinishDate();
						}
					}
				}
				msgP.setContent(formatter.format(startDate));
			} else {
				List<Stage> prevStageList = new ArrayList<Stage>();
				prevStageList = orderProdService.getStage(order.getOrderId(), stage.getNumber() - 1, stage.getCodeNom());
				for (Stage pStage: prevStageList) {
					if (pStage.getState().equals("plan")) {
						msgP.setContent(formatter.format(pStage.getPlanFinishDate()));
					}
				}
			}
		}
		else { //JIT
			if (stage.getNextNumber() == 0) {
				//Вхождения
				List<NomLinks> outputs = stageService.getOutputs(stage.getIdStage());
				Iterator<NomLinks> itStageOutputs = outputs.iterator();
				while (itStageOutputs.hasNext()) {
					NomLinks output = itStageOutputs.next();
					List<Stage> outputStages = orderProdService.getStageById(output.getStageId());
					Iterator<Stage> itOutputStages = outputStages.iterator();
					while (itOutputStages.hasNext()) {
						Stage outStage = itOutputStages.next();
						if (outStage.getState().equals("new")) {
							startCalc = false;
						} else if (completeDate.after(outStage.getPlanStartDate())) {
							completeDate = outStage.getPlanStartDate();
						}
					}
				}
				msgP.setContent(formatter.format(completeDate));
			} else {
				List<Stage> nextStageList = new ArrayList<Stage>();
				nextStageList = orderProdService.getStage(order.getOrderId(), stage.getNextNumber(), stage.getCodeNom());
				Iterator<Stage> itNextStage = nextStageList.iterator();
				while (itNextStage.hasNext()) {
					Stage nextStage = itNextStage.next();
					if (nextStage.getState().equals("plan")) {
						msgP.setContent(formatter.format(nextStage.getPlanStartDate()));
					}
				}
			}
		}
		msgP.setConversationId("StartPlanning");
		//cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
		if (startCalc) {
			myAgent.send(msgP);
			requestCnt++;
		}
		// Prepare the template to get proposals

		return requestCnt;
	}

}




