package ru.moype.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ru.moype.model.OrderProduction;
import ru.moype.model.Task;
import ru.moype.service.CriticalPath;
import ru.moype.service.OrderProductionService;

import java.text.SimpleDateFormat;
import java.util.HashSet;

public class OrderCriticalPathAgent extends Agent{

	OrderProductionService orderProdService = new OrderProductionService();
	OrderProduction order;
	//Initialization
	public void setup() {

		Object[] args = getArguments();
		order 	= (OrderProduction)args[0];

		//Register point as service
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("criticalPath");
		sd.setName("criticalPath" + order.getOrderId());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new OrderCriticalPathAgent.CriticalPathManager(this));
		System.out.println("Agent: "+ getAID().getLocalName() + " is active.");
	}

	private class CriticalPathManager extends TickerBehaviour {

		private String state = "";
		//private DBStage dbStage;
		private CriticalPathManager(Agent a) {
			//this.dbStage = dbStage;
			super(a, 60000); // tick every 1 min
		}

		public void onTick() {
			// Update states of precondition orders
			myAgent.addBehaviour(new OrderCriticalPathAgent.CriticalPathNegotiator(this));
		}
	}

	private class CriticalPathNegotiator extends Behaviour {

		private String msgContent = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public CriticalPathNegotiator(OrderCriticalPathAgent.CriticalPathManager m) {
			super(null);
			//manager = m;
		}

		private int step = 0;

		public void action() {
			// Receive all message from stage agents
			switch (step) {
				case 0:
					ACLMessage msgS = myAgent.receive();
					if (msgS != null) {
						if (msgS.getPerformative() == ACLMessage.REQUEST) {
							if (msgS.getConversationId() == "calcCriticalPath") {
								HashSet<Task> allTasks = orderProdService.getStageForCalculateCritical(order.getOrderId());
								CriticalPath critical = new CriticalPath(allTasks);

								for (Task t : critical.result) {
									orderProdService.updateCritical(t, order.getOrderId());
								}
							}
						}
					}
			}
		}

		public boolean done() {
			return step == 2;
		}

	}


}
