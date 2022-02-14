package ru.moype.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.jade4spring.JadeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ru.moype.config.SpringContext;
import ru.moype.dbService.DBOrderProduction;
import ru.moype.dbService.DBStage;
import ru.moype.model.NomLinks;
import ru.moype.model.Stage;
import ru.moype.service.StageService;

public class StageAgent extends Agent{

	//private PlanMechanics planMechanics;
	//private Vector resourceAgents = new Vector();
	private Vector stageAgents = new Vector();
	private String state;
	private Stage stage;
	private Date startDate, deadLine, bestDay, planStart, planFinish;
	private String idStage, input, name, orderId, typeOfResource, nom, mode;
	private long needTime, point, number;
	//private AID bestResource; 	// The resource agent who provides the best offer
	private boolean precondition;
	Map<Long, String> precStages = new HashMap(); //Precondition stages
	private String inputs[];
	String stageTopic;

	//@Autowired private ApplicationContext applicationContext;	

//	@Autowired
//	DBStage dbStage;
	StageService stageService = SpringContext.getBean(StageService.class);

	//public DBStage getDbStage()
	//{
	//	return dbStage;
	//}

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
		name    		= stage.getName();
		orderId 		= stage.getOrderId();
		typeOfResource 	= stage.getTypeOfResource();
		needTime		= stage.getNeedTime();
		point			= stage.getPoint();
		number			= stage.getNumber();
		state			= stage.getState();
		planStart		= stage.getPlanStartDate();
		planFinish      = stage.getPlanFinishDate();
		mode = stage.getMode();

		nom = stage.getCodeNom();
		precondition    = true;
		stageTopic = "stages_" + nom; //Топик в котором общаются только агенты по номенклатуре

	  	//Register point as service
		DFAgentDescription dfd = new DFAgentDescription();
	  	dfd.setName(getAID());
	  	ServiceDescription sd = new ServiceDescription();
	  	sd.setType("precondition" + orderId);
	  	sd.setName("" + number + "_" + nom);

	  	dfd.addServices(sd);
	  	try {
	  		DFService.register(this, dfd);
	  	}
	  	catch (FIPAException fe) {
	  		fe.printStackTrace();
	  	}
		
		//inputs = parseInput(input);
		//precondition = Long.parseLong(inputs[0]) == 0;
		
		//if (!precondition) {
		//	for (int i = 0; i < inputs.length; ++i) {
		//		precStages.put(Long.parseLong(inputs[i].trim()), "new");
		//	}
		//}
		
		// Update the list of stages agents every 30 sec for planing
		//addBehaviour(new TickerBehaviour(this, 30000) {
		//	protected void onTick() {

		//		if (!precondition) {
					// Update the list of operation agents
		//			DFAgentDescription template = new DFAgentDescription();
		//			ServiceDescription sd = new ServiceDescription();
		//			sd.setType("precondition" + orderId);
		//			template.addServices(sd);
		//			try {
		//				DFAgentDescription[] result = DFService.search(myAgent,	template);
		//				stageAgents.clear();
		//				for (int i = 0; i < result.length; ++i) {
		//					stageAgents.addElement(result[i].getName());
		//				}
		//			}
		//			catch (FIPAException fe) {
		//				fe.printStackTrace();
		//			}
		//		}
		//	}
		//});

		//addBehaviour(new PreconditionManager(this));

		//Reply to other operation agents about status
		//addBehaviour(new PreconditionReply());		
		
		// Update the list of resources agents every minute
//		addBehaviour(new TickerBehaviour(this, 60000) {
//				protected void onTick() {
//					if (precondition) {
//						// Update the list of resources agents
//						DFAgentDescription template = new DFAgentDescription();
//						ServiceDescription sd = new ServiceDescription();
//						sd.setType(typeOfResource);
//						template.addServices(sd);
//						try {
//							DFAgentDescription[] result = DFService.search(myAgent,	template);
//							resourceAgents.clear();
//							for (int i = 0; i < result.length; ++i) {
//								resourceAgents.addElement(result[i].getName());
//							}
//						}
//						catch (FIPAException fe) {
//							fe.printStackTrace();
//						}
//					}	
//				}
//		});
		
		addBehaviour(new PlaningManager(this)); //calculate plan
		//addBehaviour(new PlaningReply());		

		System.out.println("Agent: "+ getAID().getLocalName() + " is active.");
	}
	
	//private class PreconditionManager extends TickerBehaviour {
		
	//	private PreconditionManager(Agent a) {
	//		super(a, 60000); // tick every minute
	//	}
		
	//	public void onTick() {
	//		if (!precondition){
				// Update states of precondition orders
	//			myAgent.addBehaviour(new PreconditionNegotiator(this));
	//		}
	//	}
	//}

	private class PlaningManager extends TickerBehaviour {
		
		private String state = "";
		//private DBStage dbStage;
		private PlaningManager(Agent a) {
			//this.dbStage = dbStage;
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
		private MessageData messageData;
		private String msgContent = "";
		private Long pointOper;
		private String strPointOper;
		
		private int repliesCnt = 0; // The counter of replies from resource agents

		
		public PlaningNegotiator(PlaningManager m) {
			super(null);
			manager = m;
		}		
		
		private int step = 0;		
		
		public void action() {
			// Receive all message from stage agents and orderP
			switch (step) {
				case 0:
					ACLMessage msgS = myAgent.receive();
					if (msgS != null) {
						if (msgS.getPerformative() == ACLMessage.REQUEST) {
							if (msgS.getConversationId() == "status_update") {
								ACLMessage reply = msgS.createReply();
								reply.setPerformative(ACLMessage.INFORM);
								reply.setContent(state);
								myAgent.send(reply);
							}
							if (msgS.getConversationId() == "StartPlanning") { //Начало планирования
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								msgContent = msgS.getContent();
								//try {
								//	messageData = parseMessage(msgContent);
								//} catch (ParseException e) {
								//	// TODO Auto-generated catch block
								//	e.printStackTrace();
								//}
								//if (messageData.senderNom.equals(stage.getCodeNom())) { //Проверка на номенклатуру
								//	if (messageData.senderNumber == (number - 1)) { //этап является следующим по порядку
										try {
											startCalc(idStage, formatter.parse(msgContent));
										} catch (ParseException e) {
											e.printStackTrace();
										}
										if (state.equals("plan")) {
											//отправить предыдущему
											ACLMessage reply = msgS.createReply();
											reply.setPerformative(ACLMessage.INFORM);
											reply.setContent("planningDone");
											myAgent.send(reply);
										}
								//	}
								//}
							}
						}
					}
					block();
			}
		}
		
		public boolean done() {
			return step == 3;
		}
	}
	
	/**
	Inner class PlanNegotiator.
	This is the behaviour used by Operation agents to actually
	negotiate with resource agents to plan.
	*/
//	private class PlaningNegotiator extends Behaviour {
		
//		private PlanManager manager;
		

//		private Date offerDay; 		// The best offered day
//		private int repliesCnt = 0; // The counter of replies from resource agents
//		
//		private MessageTemplate mt; // The template to receive replies
//		
//		private int step = 0;
//		
//		public PlanNegotiator(PlanManager m) {
//			super(null);
//			manager = m;
//		}
//		
//		public void action() {
//			switch (step) {
//			case 0:
				// Send the CFP to all resources
//				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
//				for (int i = 0; i < resourceAgents.size(); ++i) {
//					cfp.addReceiver((AID)resourceAgents.elementAt(i));
//				}
//				try {
//					cfp.setContentObject(operation);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				cfp.setContent(operation.getIdOperation());
//				try {
//					cfp.setContentObject(operation);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				cfp.setConversationId("search"); 
//				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
//				myAgent.send(cfp);
				// Prepare the template to get proposals
//				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("search"), MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
//				step = 1;
//				break;
//			case 1:
				// Receive all proposals/refusals from resource agents
//				ACLMessage reply = myAgent.receive(mt);
//				if (reply != null) {
					// Reply received
//					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer
//						state = reply.getContent();  //?
//						try {
//							offerDay = (Date) reply.getContentObject();
//						} catch (UnreadableException e) {
//							e.printStackTrace();
//						}
//						if (bestDay != null){
//							if (offerDay.before(bestDay)){
//								bestDay = offerDay;
//								bestResource = reply.getSender();
//								operation.setPlanDate(bestDay);
//							}
//						} else {
//							bestDay = offerDay;
//							bestResource = reply.getSender();
//							operation.setPlanDate(bestDay);
//						}
//					}
//					repliesCnt++;
//					if (repliesCnt >= resourceAgents.size()) {
//						// We received all replies
//						step = 2;
//					}
//				}	
//				else {
//					block();
//				}
//				break;
//			case 2:
//				if (bestResource != null){ 
					// Send the order to the resource that provided the best offer
//					ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//					order.addReceiver(bestResource);
//					order.setContent(operation.getIdOperation());
//					try {
//						order.setContentObject(operation);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					order.setConversationId("search");
//					order.setReplyWith("order"+System.currentTimeMillis());
//					myAgent.send(order);
					// Prepare the template to get the purchase order reply
//					mt = MessageTemplate.and(MessageTemplate.MatchConversationId("search"),	MessageTemplate.MatchInReplyTo(order.getReplyWith()));
//					step = 3;
//				}
//				else {
				// If we received no acceptable proposals, terminate
					
//				}
//				break;
//			case 3:
				// Receive the purchase order reply
//				reply = myAgent.receive(mt);
//				if (reply != null) {
					// Purchase order reply received
//					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Plan successful. We can terminate
//						myGui.notifyUser("Book " + title + " successfully purchased. Price = " + bestPrice);
//						stage.setState("plan");
//						stage.setPlanDate(bestDay);
//						stage.update();
//						System.out.println("An agent: "+ getAID().getLocalName() + " " + number + " plan succecfull");
//						manager.stop();
//					}
//					step = 4;
//				}
//				else {
//					block();
//				}
//				break;
//			}
//		}
		
//		public boolean done() {
//			return step == 4;
//		}
//	}
	
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
	
	private void startCalc(String idStage, Date time) {
		
		boolean startCalc = true;

//		DBStage dbstage = (DBStage) applicationContext.getBean("ru.moype.DBStage");		
		
		//dbStage = new DBStage();
		//Надо перенести в orderProductionAgent
		//List<NomLinks> inputs = stageService.getInputs(idStage);
		//Iterator<NomLinks> itStageInputs = inputs.iterator();
		//startCalc = true;
		//while (itStageInputs.hasNext()){
		//	NomLinks input = itStageInputs.next();
		//	input.getStageIdInput()
		//		if (! getState().equals("new")) {
		//			startCalc = false;
		//		}
		//}
		if (startCalc) {
			if (mode.equals("0")) {
				stage.setPlanStartDate(time);
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				c.add(Calendar.DATE, (int) stage.getNeedTime());  // number of days to add
				stage.setPlanFinishDate(c.getTime());
			} else {
				stage.setPlanFinishDate(time);
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				c.add(Calendar.DATE, (int) stage.getNeedTime()*-1);  // number of days to -
				stage.setPlanStartDate(c.getTime());
			}
			state = "plan";
			stage.setState(state);
			stageService.save(stage);

			//Обновляем связи номенклатуры
			//List<NomLinks> inputLinks = dbStage.getOutputs(idStage);
			//Iterator<NomLinks> itStageOutputs = inputs.iterator();
			//while (itStageOutputs.hasNext()) {
			//	NomLinks output = itStageOutputs.next();
			//	if (output.getState() != "done") {
			//		output.setState("done");
			//		dbStage.saveLink(output);
			//	}
				
			//}
			
		}
	}
}
