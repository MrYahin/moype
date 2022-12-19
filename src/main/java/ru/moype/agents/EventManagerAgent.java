package ru.moype.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import net.sf.jade4spring.JadeBean;
import ru.moype.config.SpringContext;
import ru.moype.model.Event;
import ru.moype.service.EventsService;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Vector;

public class EventManagerAgent extends Agent{

	EventsService evService = SpringContext.getBean(EventsService.class);
	private Vector eventAgents = new Vector();
	JadeBean jadeBean = SpringContext.getBean(JadeBean.class);
	String curAgent = "";

	//Initialization
	public void setup() {
		addBehaviour(new EventManagerAgent.EventManager(this));
		System.out.println("Agent: "+ getAID().getLocalName() + " is active.");
	}

	private class EventManager extends TickerBehaviour {

		private String state = "";
		//private DBStage dbStage;
		private EventManager(Agent a) {
			//this.dbStage = dbStage;
			super(a, 5000); // tick every 10 sec
		}

		public void onTick() {
			myAgent.addBehaviour(new EventManagerAgent.EventNegotiator(this));
		}
	}

	private class EventNegotiator extends Behaviour {

		private String msgContent = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public EventNegotiator(EventManagerAgent.EventManager m) {
			super(null);
			//manager = m;
		}

		private int step = 0;

		public void action() {
			switch (step) {
				case 0:
					//Получить первое событие
					Event ev = evService.getFirstEvent();

					if (ev == null){
						step = 1;
						break;
					}
					if (!curAgent.equals(ev.getId() + ev.getType() + ev.getOrderId() + ev.getTime())) {
						try {
							Object argsJ[] = new Object[1];
							argsJ[0] = ev;
							jadeBean.startAgent("Event_" + ev.getType() + ":" + ev.getOrderId() + "_" + ev.getStageId(), "ru.moype.agents.EventAgent", argsJ);
						} catch (Exception e) {
							System.out.println("не получилось создать события.");
						}
						curAgent = ev.getId() + ev.getType() + ev.getOrderId() + ev.getTime();
					}
					step = 1;
					break;
			}
		}

		public boolean done() {
			return step == 1;
		}

	}

}
