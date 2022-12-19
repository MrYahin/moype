package ru.moype.agents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
import ru.moype.model.RowStageSchemeResgroup;
import ru.moype.model.Stage;
import ru.moype.service.StageService;

public class StageAgent extends Agent{

	Vector resourceResGroupAgents = new Vector();
	String state;
	Stage stage;
	String idStage;
	String orderId;
	String batch;
	String mode;
	long number;
	AID orderProductionAgent;
	boolean precondition;
	Date planDate;

	String stageTopic;

	StageService stageService = SpringContext.getBean(StageService.class);

	public class MessageData {
		public Long point;
	    public String state = "";
	    public String senderId = "";
	    public String senderNom = "";
	    public Long senderNumber;
	    public Date senderEndTime;
	}
	
	//Initialization
	public void setup() {

		Object[] args = getArguments();
		stage 	= (Stage)args[0];
		//startDate 	= (Date)args[1];
		//deadLine 	= (Date)args[2];
		idStage 	= stage.getIdStage();
		//input   		= (stage.getInput() == null)?"0":stage.getInput(); //String of precondition
		//name    		= stage.getName();
		orderId 		= stage.getOrderId();
		//typeOfResource 	= stage.getTypeOfResource();
		//needTime		= stage.getNeedTime();
		//point			= stage.getPoint();
		number			= stage.getNumber();
		state			= stage.getState();
		//planStart		= stage.getPlanStartDate();
		//planFinish      = stage.getPlanFinishDate();
		mode = stage.getMode();

		batch = stage.getBatch();
		precondition    = true;
		stageTopic = "stages_" + batch; //Топик в котором общаются только агенты по номенклатуре

		registerServices(); //Register point as service

		addBehaviour(new PlaningManager(this)); //calculate plan

		System.out.println("Agent: "+ getAID().getLocalName() + " is active.");
	}

	//dying
	protected void takeDown()  {
		try { DFService.deregister(this); }
		catch (Exception e) {}
	}

	private class PlaningManager extends TickerBehaviour {
		
		private String state = "";
		private PlaningManager(Agent a) {
			super(a, 1000); // tick every 1 sec
		}
		
		public void onTick() {
			if (precondition) {
			// Update states of precondition orders
				if (state.equals("done")) {
					stop();
				}
				else {
					myAgent.addBehaviour(new PlaningNegotiator(this));
				}
			}	
		}
	}		
	
	private class PlaningNegotiator extends Behaviour {
	
		private PlaningManager manager;
		private MessageTemplate mt; // The template to receive replies
		//Date planDate; //Дата выданная заказом
		
		private int repliesCnt = 0; // The counter of replies from resource agents

		public PlaningNegotiator(PlaningManager m) {
			super(null);
			manager = m;
		}		
		
		int step = 0;

		public void action() {
			// Receive all message from stage agents and orderP
			switch (step) {
				case 0:
					ACLMessage msgS = myAgent.receive();
					if (msgS != null) {
						//Запросы от заказа
						if (msgS.getPerformative() == ACLMessage.REQUEST) {
							if (msgS.getConversationId() == "status_update") {
								ACLMessage reply = msgS.createReply();
								reply.setPerformative(ACLMessage.INFORM);
								reply.setContent(state);
								myAgent.send(reply);
								System.out.println(reply.toString());
							}
							if (msgS.getConversationId() == "stop") {
								doDelete();
							}
							if (msgS.getConversationId() == "StartPlanning") { //Начало планирования
								startPlanning(msgS, myAgent);
							}
						}
						//Офферы от ресурсов
						if (msgS.getPerformative() == ACLMessage.PROPOSE) {
							propose_resAgent(msgS, myAgent);
						}
						if (msgS.getPerformative() == ACLMessage.CONFIRM) {
							confirm_resAgent(msgS, myAgent);
						}
					}
					if (msgS == null) {
						step = 1;
						break;
					}
			}
		}
		
		public boolean done() {
			return step == 1;
		}
	}

	private String[] parseInput(String input){
		return input.split(",");
	}
	
	private MessageData parseMessage(String messageText) throws ParseException{
	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		MessageData msgData = new MessageData();
		String[] msgStruc = messageText.split(";");
		msgData.senderId = msgStruc[0];
		msgData.senderNom = msgStruc[1];
		msgData.senderNumber = Long.parseLong(msgStruc[2]);
		msgData.senderEndTime = formatter.parse(msgStruc[3]);
		
		return msgData;
	}
	
	private void startCalc(String idStage, Date time, long needTime) {

		boolean startCalc = true;


		if (startCalc) {
			if (mode.equals("0")) {
				stage.setPlanStartDate(time);
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				c.add(Calendar.SECOND, (int) needTime);  // number of days to add
				stage.setPlanFinishDate(c.getTime());
			} else {
				stage.setPlanFinishDate(time);
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				c.add(Calendar.SECOND, (int) needTime*-1);  // number of days to -
				stage.setPlanStartDate(c.getTime());
			}
			state = "plan";
			stage.setState(state);
			stageService.save(stage);
			//Обновить даты обеспечения
			List<NomLinks> inputs = stageService.getInputs(stage.getIdStage());
			for (NomLinks rowNomLink: inputs){
				rowNomLink.setNeedDate(stage.getPlanStartDate());
				stageService.saveLink(rowNomLink);
			}
			//НУжно обновить связанные строки по ВРЦ

		}
	}

	private void setCalc(String idStage, Date start, Date finish) {

		boolean startCalc = true;

		if (startCalc) {
			stage.setPlanStartDate(start);
			stage.setPlanFinishDate(finish);
			state = "plan";
			stage.setState(state);
			stageService.save(stage);
			//Обновить даты обеспечения
			List<NomLinks> inputs = stageService.getInputs(stage.getIdStage());
			for (NomLinks rowNomLink: inputs){
				rowNomLink.setNeedDate(stage.getPlanStartDate());
				stageService.saveLink(rowNomLink);
			}
			//НУжно обновить связанные строки по ВРЦ

		}
	}

	Map<String, String> parseResponce(String msgContent) throws JSONException {

		Map<String, String> result = new HashMap<String, String>();

		JSONArray the_json_array = new JSONArray(msgContent);
		for (int i = 0; i < the_json_array.length(); i++) {
			if (the_json_array.getString(i).contains("start")) {
				result.put("start", the_json_array.getString(i).substring(6));
			}
			if (the_json_array.getString(i).contains("finish")) {
				result.put("finish", the_json_array.getString(i).substring(7));
			}
		}
		return result;
	}

	Map<String, String> parseResponceStartPlanning(String msgContent) throws JSONException {

		Map<String, String> result = new HashMap<String, String>();

		System.out.println(msgContent);

		JSONArray the_json_array = new JSONArray(msgContent);
		for (int i = 0; i < the_json_array.length(); i++) {
			if (the_json_array.getString(i).contains("mode")) {
				result.put("mode", the_json_array.getString(i).substring(5));

			}
			if (the_json_array.getString(i).contains("date")) {
				result.put("date", the_json_array.getString(i).substring(5));
			}
		}
		return result;
	}

	public void registerServices(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("precondition" + orderId);
		sd.setName("" + number + "_" + batch);

		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	public void startPlanning(ACLMessage msgS, Agent myAgent){

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String msgContent = msgS.getContent();
		orderProductionAgent = msgS.getSender();
		try {
			Map<String, String> responce = parseResponceStartPlanning(msgContent);
			mode = responce.get("mode");
			if (mode != stage.getMode()) {
				stage.setMode(mode);
			}
			planDate = formatter.parse(responce.get("date"));

			if (stage.getModelPlanning() == 0) { //По общей доступности
				startCalc(idStage, planDate, stage.getNeedTime());
				ACLMessage reply = msgS.createReply();
				reply.setConversationId("CompletePlanning");
				reply.setPerformative(ACLMessage.INFORM);
				String mStringArray[] = {"idStage:" + idStage, "start:" + formatter.format(stage.getPlanStartDate()), "finish:" + formatter.format(stage.getPlanFinishDate())};
				JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
				reply.setContent(String.valueOf(mJSONArray));
				myAgent.send(reply);
				System.out.println(reply.toString());
			} else { //по дступности РЦ
				List<RowStageSchemeResgroup> rssrg_list = stageService.getSchemeResGroups(idStage);
				for (RowStageSchemeResgroup rssrg : rssrg_list) {
					if (rssrg.getState().equals("new")) {
						String topic = "resGroup_" + rssrg.getIdResGroup();
						DFAgentDescription templateToRequest = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType("offer_" + rssrg.getIdResGroup());
						sd.setName(topic);
						templateToRequest.addServices(sd);
						try {
							DFAgentDescription[] result = DFService.search(myAgent, templateToRequest);
							resourceResGroupAgents.clear();
							for (int i = 0; i < result.length; ++i) {
								resourceResGroupAgents.addElement(result[i].getName());
							}
							if (result.length == 0) {
								System.out.println("Не найдены агенты ресурсов: " + topic);
							}
						} catch (FIPAException fe) {
							fe.printStackTrace();
						}
						ACLMessage msgP = new ACLMessage(ACLMessage.REQUEST);
						for (int i = 0; i < resourceResGroupAgents.size(); ++i) {
							msgP.addReceiver((AID) resourceResGroupAgents.elementAt(i));
							String mStringArray[] = {"load:" + String.valueOf(rssrg.getLoadTime()), "mode:" + mode, "date:" + formatter.format(planDate), "stage:" + idStage};
							JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
							msgP.setContent(String.valueOf(mJSONArray));
							msgP.setConversationId("resGroupOrder");
							myAgent.send(msgP);
							System.out.println(msgP.toString());
						}
					}
				}
			}
		} catch (ParseException | JSONException e) {
			e.printStackTrace();
		}
	}

	public void propose_resAgent(ACLMessage msgS, Agent myAgent){

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (msgS.getConversationId() == "resGroupOrder") {
			Date startRes = null;
			Date finishRes = null;
			try {
				Map<String, String> responceMsg = parseResponce(msgS.getContent());
				startRes = formatter.parse(responceMsg.get("start"));
				finishRes = formatter.parse(responceMsg.get("finish"));
			} catch (ParseException | JSONException e) {
				e.printStackTrace();
			}
			if (mode.equals("0")) { //ASAP
				//if (dateRes.after(planDate)){
				//accept
				ACLMessage reply = msgS.createReply();
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent(idStage);
				myAgent.send(reply);
				System.out.println(reply.toString());
			} else { //JIT
				//if (dateRes.before(planDate)){
				//accept
				ACLMessage reply = msgS.createReply();
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent(idStage);
				myAgent.send(reply);
				System.out.println(reply.toString());
			}
		}
	}

	public void confirm_resAgent(ACLMessage msgS, Agent myAgent){

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (msgS.getConversationId() == "resGroupOrder") {
			Date startRes = null;
			Date finishRes = null;
			try {
				Map<String, String> responce = parseResponce(msgS.getContent());
				startRes = formatter.parse(responce.get("start"));
				finishRes = formatter.parse(responce.get("finish"));
			} catch (ParseException | JSONException e) {
				e.printStackTrace();
			}
			//startCalc(idStage, dateRes, 1); //Надо подумать
			setCalc(idStage, startRes, finishRes);

			//Отправить ответ заказу
			ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
			inform.addReceiver(orderProductionAgent);
			inform.setConversationId("CompletePlanning");
			inform.setPerformative(ACLMessage.INFORM);
			String mStringArray[] = {"idStage:" + idStage, "start:" + formatter.format(stage.getPlanStartDate()), "finish:" + formatter.format(stage.getPlanFinishDate())};
			JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
			inform.setContent(String.valueOf(mJSONArray));
			myAgent.send(inform);
			System.out.println(inform.toString());
		}
	}
}
