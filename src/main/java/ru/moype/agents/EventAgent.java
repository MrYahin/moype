package ru.moype.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ru.moype.config.SpringContext;
import ru.moype.model.Event;
import ru.moype.model.OrderProduction;
import ru.moype.service.EventsService;
import ru.moype.service.OrderProductionService;

import java.text.SimpleDateFormat;
import java.util.Vector;

public class EventAgent extends Agent{

	private Vector orderProdAgents = new Vector();
	Event ev;
	String typeOfEvent;
	OrderProductionService orderProdService = SpringContext.getBean(OrderProductionService.class);
	EventsService evService = SpringContext.getBean(EventsService.class);
	String state = "";

	private Vector orderAgents = new Vector();

	//Initialization
	public void setup() {

		Object[] args = getArguments();
		ev 	= (Event) args[0];
		typeOfEvent = ev.getType();
		DFAgentDescription templateToRequest = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("orderProduction" + ev.getOrderId());
		templateToRequest.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, templateToRequest);
			for (int i = 0; i < result.length; ++i) {
				System.out.println("Found:" + result[i].getName());
				orderProdAgents.addElement(result[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new EventAgent.EventBehav(this));

		addBehaviour(new TickerBehaviour(this, 1000) {

			protected void onTick() {
				// Update the list of orderProd agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("orderProduction" + ev.getOrderId());
				sd.setName("orderProduction" + ev.getOrderId());
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent,	template);
					orderAgents.clear();
					for (int i = 0; i < result.length; ++i) {
						orderAgents.addElement(result[i].getName());
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});

		state = "start";

	}

	//dying
	//protected void takeDown()  {
	//	try { DFService.deregister(this); }
	//	catch (Exception e) {}
	//}

	private class EventBehav extends TickerBehaviour {

		private String state = "";
		//private DBStage dbStage;
		private EventBehav(Agent a) {
			//this.dbStage = dbStage;
			super(a, 1000); // tick every 1 sec
		}

		public void onTick() {
			myAgent.addBehaviour(new EventAgent.EventNegotiator(this));
		}
	}

	private class EventNegotiator extends Behaviour {

		private String msgContent = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public EventNegotiator(EventAgent.EventBehav m) {
			super(null);
			//manager = m;
		}

		private int step = 0;

		public void action() {
			switch (step) {
				case 0:
					ACLMessage msgP = new ACLMessage(ACLMessage.REQUEST);
					for (int i = 0; i < orderAgents.size(); ++i) {
						msgP.addReceiver((AID) orderAgents.elementAt(i));
					}
					if (typeOfEvent.equals("stopOrder")) {
						event_request_stopOrder(msgP, myAgent);
					}
					if (typeOfEvent.equals("startOrder")) {
						event_request_startOrder(msgP, myAgent);
					}
					if (typeOfEvent.equals("replanOrder")) {
						event_request_replanOrder(msgP, myAgent);
					}
					step = 1;
					break;
				case 1:
					//Если получили подтверждение, то уничтожаем агента.
					//предварительно записав статус
					ACLMessage msgS = myAgent.receive();
					if (msgS != null) {
						if (msgS.getPerformative() == ACLMessage.INFORM) {
							if (msgS.getConversationId() == "startOrder" ||
									msgS.getConversationId() == "stopAllAgents" ||
									msgS.getConversationId() == "replanOrder"){
								String stateO = msgS.getContent();
								if (!ev.getState().equals(stateO)){
									ev.setState(stateO);
									evService.update(ev);
									state = "finish";
								}
							}
							if (state.equals("finish")){
								doDelete();
							}
						}
					}
					step = 2;
					break;
			}
		}

		public boolean done() {
			return step == 2;
		}

	}

	public void event_request_stopOrder(ACLMessage msgP, Agent myAgent){
		if (orderAgents.size() == 0){
			ev.setState("plan");
			evService.update(ev);
			doDelete();
		} else {
			msgP.setConversationId("stopAllAgents");
			msgP.setContent(ev.getType());
			myAgent.send(msgP);
		}
		state = "processing";
	}

	public void event_request_startOrder(ACLMessage msgP, Agent myAgent){
		if (state.equals("processing")){
			msgP.setConversationId("startOrder");
			msgP.setContent(ev.getType());
			myAgent.send(msgP);
		} else {
			//Проверка на зависимость заказа
			OrderProduction order = orderProdService.getOrderById(ev.getOrderId());
			if (!order.getNeedOrder().equals("none")) {
				OrderProduction needOrder = orderProdService.getOrderById(order.getNeedOrder());
				if (needOrder == null || needOrder.getState() != "plan"){
					ev.setState("error");
					evService.update(ev);
					doDelete();
				}
			} else {
				if (orderProdAgents.size() == 0) {
					try {
						orderProdService.createStageAgent(ev.getOrderId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			state = "processing";
		}
	}

	public void event_request_replanOrder(ACLMessage msgP, Agent myAgent){

		if (orderProdAgents.size() == 0) { //Не возможно перепланирование при отсутствии агентов
			ev.setState("error");
			evService.update(ev);
			doDelete();
			return;
		}

		msgP.setConversationId("replanOrder");
		msgP.setContent(String.valueOf(ev.getId()));
		myAgent.send(msgP);
		state = "processing";
	}

}
