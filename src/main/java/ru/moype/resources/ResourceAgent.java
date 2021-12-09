//package ru.moype.resources;
//
//import jade.core.Agent;
//import jade.core.AID;
//
//import base.BookSellerGui;
//
//import java.io.IOException;
//import java.util.*;
//import jade.core.behaviours.*;
//import jade.lang.acl.MessageTemplate;
//import jade.lang.acl.UnreadableException;
//import resources.CalendarResAv;
//import resources.CalendarResPlan;
//import resources.Operation;
//import jade.lang.acl.ACLMessage;
//import jade.domain.FIPAAgentManagement.*;
//import jade.domain.*;
//
//import resources.Unit;
//
//public class ResourceAgent extends Agent {
//
//	  //���� ����������� �������!!!
//	  // The GUI to interact with the user
//	  //private ResourceGui myGui;
//
//	  // The catalogue of books available for sale
//	  //private Map catalogue = new HashMap();
//	  private Unit res;		//res object
//	  private CalendarResAv avDay = new CalendarResAv();
//	  private CalendarResPlan plDay = new CalendarResPlan();
//	  private String name, number, typeOfResource;
//
//	  /** The following parts, where the SLCodec and BookTradingOntology are
//	    * registered, are explained in section 5.1.3.4 page 88 of the book.
//	   **/
//	  //private Codec codec = new SLCodec();
//	  //private Ontology ontology = BookTradingOntology.getInstance();
//
//
//	  /**
//	   * Agent initializations
//	  **/
//	  protected void setup() {
//
//			Object[] args = getArguments();
//			res = (Unit)args[0];
//			name   			= res.getName();
//			number 			= res.getNumber();
//			typeOfResource 	= res.getTypeOfResource();
//
//		  // Register the resource service in the yellow pages
//		  	DFAgentDescription dfd = new DFAgentDescription();
//		  	dfd.setName(getAID());
//		  	ServiceDescription sd = new ServiceDescription();
//		  	sd.setType(typeOfResource);
//		  	sd.setName(getLocalName() + name + "-" + number);
//		  	dfd.addServices(sd);
//		  	try {
//		  		DFService.register(this, dfd);
//		  	}
//		  	catch (FIPAException fe) {
//		  		fe.printStackTrace();
//		  	}
//
//		  	// Printout a welcome message
//		    System.out.println("Resource-agent " + name + " is ready.");
//		    //getContentManager().registerLanguage(codec);
//		    //getContentManager().registerOntology(ontology);
//
//		    // Create and show the GUI
//		    //myGui = new BookSellerGuiImpl();
//		    //myGui.setAgent(this);
//		    //myGui.show();
//
//		    // Add the behaviour serving calls for operation from operation agents
//		    addBehaviour(new CallForOfferServer());
//
//		    // Add the behaviour serving resource requests from operation agents
//		    addBehaviour(new ResourceOrderServer());
//
//		    addBehaviour(new ResManager(this));
//	  }
//
//	  /**
//
//	  */
//	  private class ResourceOrderServer extends CyclicBehaviour {
//
//		  private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
//		  private Stage oper = new Stage();
//		  private CalendarResPlan newResPlan;
//
//		  public void action() {
//			  ACLMessage msg = myAgent.receive(mt);
//			  if (msg != null) {
//				  //Check
//
//
//				//reply.setPerformative(ACLMessage.REFUSE);
//
//				  // Message received. Process it
//				ACLMessage reply = msg.createReply();
//
//				try {
//					oper = (Stage) msg.getContentObject();
//					newResPlan = new CalendarResPlan(oper.getPlanDate(), number, oper.getNeedTime(), oper.getIdOperation());
//					newResPlan.save();
//					reply.setPerformative(ACLMessage.INFORM);
//					// The resource is NOT available for operation.
//					myAgent.send(reply);
//				} catch (UnreadableException e) {
//					e.printStackTrace();
//				}
//			  } else {
//				  block();
//			  }
//		  }
//	  }
//
//
//	  /**
//	  Inner class CallForOfferServer.
//	  This is the behaviour used by Resource agents to serve
//	  incoming call for offer from operation agents.
//	  If the res available, the resource agent
//	  replies with a PROPOSE message specifying the status. Otherwise
//	  a REFUSE message is sent back.
//	  */
//	  private class CallForOfferServer extends CyclicBehaviour {
//
//		  private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
//		  private Stage oper = new Stage();
//		  private Date startDate, completeDate, tempDate, offerDate;
//		  private long avTimeAtDay;
//		  private long busyTimeAtDay;
//		  private long opNeedTime;
//
//		  Calendar cal = Calendar.getInstance(); //Gregorian calendar
//
//		  public void action() {
//			  ACLMessage msg = myAgent.receive(mt);
//			  if (msg != null) {
//				// Message received. Process it
//				//				  String operation = msg.getContent();
//				  try {
//					oper = (Stage) msg.getContentObject();
//					startDate = oper.getStartDate(oper.getOrderId());
//					completeDate = oper.getCompleteDate(oper.getOrderId());
//					opNeedTime	= oper.getNeedTime();
//				  } catch (UnreadableException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				  }
//				// The requested resource is available for operation. Reply with the day
//				  tempDate = startDate;
//				  cal.setTime(tempDate);
//
//				  while(tempDate.before(completeDate)){
//					  //read avTime
//					  avTimeAtDay = avDay.getAvTime(tempDate, number);
//					  if (avTimeAtDay != 0){
//						  //read res loading at day
//						  plDay.getPlanTimeRes(tempDate, number);
//						  if ((plDay.getNeedTime() + opNeedTime) <= avTimeAtDay){ //is available
//							  offerDate = plDay.getDay();
//							  break;
//						  }
//					  }
//					  cal.add(Calendar.DAY_OF_MONTH, 1); //add one day
//					  tempDate = cal.getTime();
//				  }
//
//				  ACLMessage reply = msg.createReply();
//				  if (offerDate != null){
//					  String status = "ready";
//					  reply.setPerformative(ACLMessage.PROPOSE);
//					  reply.setContent(status);
//					  try {
//						reply.setContentObject(offerDate);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					  // The resource is NOT available for operation.
//					  //reply.setPerformative(ACLMessage.REFUSE);
//				  } else {
//					  reply.setPerformative(ACLMessage.REFUSE);
//				  }
//				  myAgent.send(reply);
//
//			  }
//			  else {
//				  block();
//			  }
//		  }
//	  }
//
//	  private class ResManager extends TickerBehaviour {
//
//		  //private int minPrice, currentPrice, initPrice, deltaP;
//		  //private long initTime, deadline, deltaT;
//
//		  private ResManager(Agent a) {
//			  super(a, 30000); // tick every minute
//
//			  //currentPrice = initPrice;
//			  //deltaP = initPrice - mp;
//			  //deadline = d.getTime();
//			  //initTime = System.currentTimeMillis();
//		  }
//
//		  public void onStart() {
//
//			 // catalogue.put(title, this);
//			  super.onStart();
//		  }
//
//		  public void onTick() {
//			  //�������� ������ ��������
//
//
//			  //long currentTime = System.currentTimeMillis();
//			  //for (i)
//			  //if (currentTime > deadline) {
//				  // Deadline expired
//			//	  myGui.notifyUser("Cannot sell book " + title);
//			//	  catalogue.remove(title);
//			//	  stop();
//			  //} else {
//				  // Compute the current price
//			//	  long elapsedTime = currentTime - initTime;
//			//	  currentPrice = (int) (initPrice - deltaP * (elapsedTime / deltaT));
//			  //}
//		  }
//
//		 // public int getCurrentPrice() {
//		//	  return currentPrice;
//		  //}
//	  }
//}
