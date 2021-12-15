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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.moype.config.SpringContext;
import ru.moype.config.WebConfig;
import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.service.OrderProductionService;
//import resources.Operation;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import javax.annotation.Resource;
//import base.PlanMechanics;
//import mechanics.PlanMechanicsImpl;
public class OrderProductionAgent extends Agent{

	//private PlanMechanics planMechanics;
	//Содержит актуальный список агентов с их статусами
	private Map<String, String> stageStatus = new HashMap();
	private Vector stageAgents = new Vector();
	private OrderProduction order;
	boolean updateStatus = true;

	JadeBean jadeBean = SpringContext.getBean(JadeBean.class);

	//@Autowired
	//private ApplicationContext appContext;

	//WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
	//JadeBean jadeBean = (JadeBean) context.getBean("testBean");

	//@Autowired
	//JadeBean jadeBean;

	//@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection");
	//@Autowired(required = false)
	//private JadeBean jadeBean;

	//Initialization
	public void setup() {
		
		String stateStage = "";
		
		Object[] args = getArguments();
		order  = (OrderProduction)args[0]; 

		List<Stage> stageList = new ArrayList<Stage>();		
		
		System.out.println("Create agent: "+ getAID().getLocalName() + " " + order.getNumber());
		System.out.println("My GUID is "+ getAID().getName());
		System.out.println("My addresses are:");
		Iterator it = getAID().getAllAddresses();
		while (it.hasNext()) {
		System.out.println("- "+it.next());
		}
		
		//Create nested agents
		AgentContainer ac = getContainerController();
		
		Stage stage;
		OrderProductionService service = new OrderProductionService();
		stageList = service.getStageList(order.getNumber()); 
		
		Iterator<Stage> itStage = stageList.iterator();
		while (itStage.hasNext()){
			stage = itStage.next();
			stateStage = stage.getState();  
			if ((stateStage == null) || !stateStage.equals("stop")){
				try {
			    	//Object argsJ[] = new Object[3];
			    	//argsJ[0] = stage;
					Object argsJ[] = new Object[1];
					argsJ[0]= stage;

					//argsJ[1] = order.getStartDate();
			    	//argsJ[2] = order.getCompleteDate();			
					//AgentController agentStage = ac.createNewAgent("stage:" + stage.getNumber() + "_" + stage.getCodeNom() + "_" + stage.getName(),  "ru.moype.resources.StageAgent", argsJ);
					//agentStage.start();
					jadeBean.startAgent("stage:" + stage.getNumber() + "_" + stage.getCodeNom() + "_" + stage.getName(),  "ru.moype.resources.StageAgent", argsJ);
//					operationsStatus.put(agentOperation.getName(), "start");
				} catch (Exception e) {
					System.out.println("не получилось создать агентов.");
				}
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
		addBehaviour(new PlaningManager(this, order.getNumber(), order.getState(), order.getWayPoint(), order.getStartDate(), order.getCompleteDate())); //calculate plan
		
	}

//	public void createAgent(String orderNumber) {        
//        CreateAgent ca = new CreateAgent();
//        ca.setAgentName("orderProductionAgent:" + orderNumber);
//        ca.setClassName("ru.moype.resources.OrderProductionAgent");
//        ca.setContainer(new ContainerID("Main-Conteiner", null));
//        Action actExpr = new Action(getAMS(), ca);
//        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
//        AID aid = getAMS();
//        request.addReceiver(aid);
//        request.setOntology(JADEManagementOntology.getInstance().getName());

//        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL);
//        getContentManager().registerOntology(JADEManagementOntology.getInstance());
        
//        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
//        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
//        try {
//            getContentManager().fillContent(request, actExpr);
//            addBehaviour(new AchieveREInitiator(this, request) {
//                protected void handleInform(ACLMessage inform) {
//                    System.out.println("Agent successfully created");
//                }
       
//                protected void handleFailure(ACLMessage failure) {
//                    System.out.println("Error creating agent.");
//                }
//            });
//        }
//        catch (Exception e) {
 //           e.printStackTrace();
 //       }
		
		
		
		
//    }	
	
	private class PlaningManager extends TickerBehaviour {
		
		private String number, state, wayPoint;
		private Date startDate, completeDate;
		
		private PlaningManager(Agent a, String number, String state, String wayPoint, Date startDate, Date completeDate) {
			super(a, 60000); // tick every minute
			this.number 		= number;
			this.state 			= state;
			this.wayPoint 		= wayPoint;
			this.startDate 		= startDate;
			this.completeDate 	= completeDate;
		}
		
		public void onTick() {
			if (state.equals("ready")) {
				stop();
			}
			else {
				myAgent.addBehaviour(new PlaningNegotiator(number, state, wayPoint, startDate, completeDate, this));
			}
		}
	}

	private class PlaningNegotiator extends Behaviour {
		
		private String number;
		private String state;
		private String wayPoint;
		private Date startDate;
		private Date completeDate;
		
		private PlaningManager manager;
//		private AID bestSeller; 	// The seller agent who provides the best offer
//		private int bestPrice; 		// The best offered price
		private int repliesCnt = 0; // The counter of replies from ordersP agents
		private MessageTemplate mt; // The template to receive replies
		private String curStatus; 	// The seller agent who provides the best offer
		
		private int step = 0;

		//Создаем время жизни этого цикла, т.к. могут быть зависания
		long t= System.currentTimeMillis();
		long end = t+15000; //15 сек

		public PlaningNegotiator(String number, String state, String wayPoint, Date startDate, Date completeDate, PlaningManager m) {
			super(null); //?
			this.number 		= number;
			this.state 			= state;
			this.wayPoint 		= wayPoint;
			this.startDate 		= startDate;
			this.completeDate 	= completeDate;
			this.manager 		= m;
		}
	
		public void action() {
			curStatus = order.getState();

			switch (step) {
			case 0: //отправить всем этапам запрос на получение статусов
				if (updateStatus) {
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					for (int i = 0; i < stageAgents.size(); ++i) {
						msg.addReceiver((AID) stageAgents.elementAt(i));
					}
					//cfp.setContent(number);
					msg.setConversationId("status_update");
					//cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
					myAgent.send(msg);
					// Prepare the template to get proposals
					step = 1;
					updateStatus = false; //отправляем запрос на получение статусов только один раз
					break;
				}
			case 1: //start planning
				if (curStatus.equals("planning")) {
					// Send the INFORM to all stages
					ACLMessage msgP = new ACLMessage(ACLMessage.REQUEST);
					for (int i = 0; i < stageAgents.size(); ++i) {
						msgP.addReceiver((AID)stageAgents.elementAt(i));
					}
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					msgP.setContent(formatter.format(startDate));
					msgP.setConversationId("StartPlanning");
					//cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
					myAgent.send(msgP);
					// Prepare the template to get proposals
				}
				step = 2;
				break;					
			case 2:
				// Receive all status from stage agents

				mt = MessageTemplate.MatchConversationId("status_update");
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						String status = reply.getContent();
						curStatus = stageStatus.get((AID)reply.getSender());
						if (curStatus == null || (! curStatus.equals(status) )) {
							stageStatus.put(reply.getSender().getName(), status);
						}
					}
					repliesCnt++;
					//Выход из цикла получения ответов
					if (repliesCnt >= stageAgents.size()) {
						// We received all replies
						step = 3;
					}
				}
				else {
					//block();
				}
				//Если выполняется более 15 сек
				if (System.currentTimeMillis() > end) {
					step = 3;
				}
				break;

			case 3:
					block();
			}
		}
		
		public boolean done() {
			return step == 4;  //Если присвоить верное значение, агент перестанет работать
		}
	}

}


