package ru.moype.resources;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.BeanUtils;
import ru.moype.config.SpringContext;
import ru.moype.model.AgentOffer;
import ru.moype.model.ResGroup;
import ru.moype.model.RowCapacityResgroup;
import ru.moype.service.ResourcesService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResGroupAgent extends Agent{

	private ResGroup rg;
	ResourcesService resourceService = SpringContext.getBean(ResourcesService.class);
	Map<String, AgentOffer> offerList = new HashMap<>();

	//Initialization
	public void setup() {

		Object[] args = getArguments();
		rg = (ResGroup) args[0];
		String topic = "resGroup_" + rg.getId(); //Топик в котором общаются агенты

		//Register point as service
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("offer_" + rg.getId());
		sd.setName(topic);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new OfferManager(this));

		//List<ResGroup> resGroups = resService.getResGroupsByDivision(div.getId());
		//Object argsJ[] = new Object[0];

		//for (ResGroup rsg: resGroups) {
			//jadeBean.startAgent("stage:" + rsg.getId() + "_" + rsg.getName(),  "ru.moype.resources.resGroupAgent", argsJ);
		//}

		System.out.println("Agent: "+ getAID().getLocalName() + " is active.");
	}

	private class OfferManager extends TickerBehaviour {

		private String state = "";
		//private DBStage dbStage;
		private OfferManager(Agent a) {
			//this.dbStage = dbStage;
			super(a, 1000); // tick every 1 sec
		}

		public void onTick() {
			// Update states of precondition orders
			myAgent.addBehaviour(new OfferNegotiator(this));
		}
	}

	private class OfferNegotiator extends Behaviour {

		Date start, finish;
		private String msgContent = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public OfferNegotiator(ResGroupAgent.OfferManager m) {
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
							if (msgS.getConversationId() == "resGroupOrder") {
								msgContent = msgS.getContent();
								try {
									Map<String, String> responce = parseResponce(msgContent);
									long load = Long.parseLong(responce.get("load"));
									Date date = formatter.parse(responce.get("date"));
									start = date;
									finish = date;
									if (load != 0) {
										long toNext = load;
										//ПолучитьДоступностьВДняхВЗаданномИнтервале, возможно пока не надо интервал, просто на каждую последующую дату
										while (toNext > 0) {
											//System.out.println("" + date) ;
											RowCapacityResgroup capacity = resourceService.getCapacityByResGroup(rg.getId(), date);
											if (capacity.getAvailable() > 0) { //Есть доступность
												//зафиксировать даты
												if (toNext == load) {
													if (responce.get("mode").equals("0")) {
														start = date; 
													} else {
														finish = date;
													}
												}
												toNext = toNext - capacity.getAvailable();
												if (toNext <= 0) {
													ArrayList<String> mStringArray = new ArrayList<String>();
													if (responce.get("mode").equals("0")) {
														date = addHoursToJavaUtilDate(date, 24); //Чтобы при сравнении можно было понять влево или вправо, надо переделать
														mStringArray.add("start:" + formatter.format(start));
														finish = date;
														mStringArray.add("finish:" + formatter.format(finish));
													} else {
														date = addHoursToJavaUtilDate(date, -24); //Чтобы при сравнении
														start = date;
														mStringArray.add("start:" + formatter.format(start));
														mStringArray.add("finish:" + formatter.format(finish));
													}
													JSONArray mJSONArray = new JSONArray(mStringArray);

													ACLMessage reply = msgS.createReply();
													reply.setPerformative(ACLMessage.PROPOSE);
													reply.setContent(String.valueOf(mJSONArray));
													myAgent.send(reply);

													AgentOffer newOffer = new AgentOffer(load, responce.get("mode"), start, finish, responce.get("idStage"), capacity);
													offerList.put(responce.get("idStage"), newOffer);
												} else {


												}
											}
											if (responce.get("mode").equals("0")) {
												date = addHoursToJavaUtilDate(date, 24);
											} else {
												date = addHoursToJavaUtilDate(date, -24);
											}
										}
									}
								} catch (ParseException e) {
									e.printStackTrace();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
						if (msgS.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
							AgentOffer offer = offerList.get(msgS.getContent());
							if (offerList != null) {
								RowCapacityResgroup newLoad = new RowCapacityResgroup();
								BeanUtils.copyProperties(offer.getResGroup(), newLoad);
								newLoad.setAvailable(-1*offer.getLoad());
								newLoad.setIdStage(offer.getStage());
								resourceService.registerRowCapacityResGroup(newLoad);
								//Уведомление о загрузке
								ACLMessage reply = msgS.createReply();
								reply.setPerformative(ACLMessage.CONFIRM);
								ArrayList<String> mStringArray = new ArrayList<String>();
								mStringArray.add("start:" + formatter.format(offer.getStart()));
								mStringArray.add("finish:" + formatter.format(offer.getFinish()));
								JSONArray mJSONArray = new JSONArray(mStringArray);
								reply.setContent(String.valueOf(mJSONArray));
								myAgent.send(reply);
							}

						}

					}
			}
		}

		public boolean done() {
			return step == 2;
		}
	}

	Map<String, String> parseResponce(String msgContent) throws JSONException {

		Map<String, String> result = new HashMap<String, String>();

		JSONArray the_json_array = new JSONArray(msgContent);
		for (int i = 0; i < the_json_array.length(); i++) {
			if (the_json_array.getString(i).contains("load")) {
				result.put("load", the_json_array.getString(i).substring(5));
			}
			if (the_json_array.getString(i).contains("mode")) {
				result.put("mode", the_json_array.getString(i).substring(5));
			}
			if (the_json_array.getString(i).contains("date")) {
				result.put("date", the_json_array.getString(i).substring(5));
			}
			if (the_json_array.getString(i).contains("stage")) {
				result.put("idStage", the_json_array.getString(i).substring(6));
			}
		}
		return result;
	}

	public Date addHoursToJavaUtilDate(Date date, int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}
}

