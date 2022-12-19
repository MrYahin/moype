package ru.moype.agents;

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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class ResGroupAgent extends Agent{

	ResGroup rg;
	ResourcesService resourceService = SpringContext.getBean(ResourcesService.class);
	Map<String, AgentOffer> offerList = new HashMap<>();
	Map<String, ArrayList<RowCapacityResgroup>> reserveList = new HashMap<>();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatterDay = new SimpleDateFormat("yyyy-MM-dd");
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
						//Запрос ресурса
						if (msgS.getPerformative() == ACLMessage.REQUEST) {
							request_from_stage(msgS, myAgent);
						}
						if (msgS.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
							accept_from_stage(msgS, myAgent);
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

	public Date addSecondsToJavaUtilDate(Date date, int seconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, seconds);
		return calendar.getTime();
	}

	public static Date atStartOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
		return localDateTimeToDate(startOfDay);
	}

	private static Date localDateTimeToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	private static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public Date addDayToJavaUtilDate(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	public void Mix_capacity_reserve(List<RowCapacityResgroup> capacityIntervals, ArrayList<RowCapacityResgroup> reserve_intervals){

		ArrayList<RowCapacityResgroup> removeIntervals = new ArrayList<RowCapacityResgroup>();
		RowCapacityResgroup newResGRP = new RowCapacityResgroup();
		//Что зарезервировано по переговорам с другими агентами
		//Накладываем интервалы
		if (reserve_intervals != null){
			for (RowCapacityResgroup res_interval : reserve_intervals) {
				boolean addInterval = false;
				for (RowCapacityResgroup cap_interval : capacityIntervals){
					// |||||||||            |
					if (res_interval.getStart().equals(cap_interval.getStart()) && res_interval.getFinish().before(cap_interval.getFinish())) {
						cap_interval.setStart(res_interval.getFinish());
					}
					// |       ||||||||||
					if (res_interval.getStart().after(cap_interval.getStart()) && res_interval.getFinish().equals(cap_interval.getFinish())) {
						cap_interval.setFinish(res_interval.getStart());
					}
					// |       ||||||||||           |
					if (res_interval.getStart().after(cap_interval.getStart()) && res_interval.getFinish().before(cap_interval.getFinish())){
						Date finish_t = cap_interval.getFinish();
						cap_interval.setFinish(res_interval.getStart());
						//Добавить интервал
						BeanUtils.copyProperties(cap_interval, newResGRP);
						newResGRP.setStart(res_interval.getFinish());
						newResGRP.setFinish(finish_t);
						addInterval = true;
					}
					//Весь интервал занят
					if (res_interval.getStart().equals(cap_interval.getStart()) && res_interval.getFinish().equals(cap_interval.getFinish())) {
						removeIntervals.add(cap_interval);
					}
				}
				if (addInterval) {
					RowCapacityResgroup newResGRP_ = new RowCapacityResgroup();
					BeanUtils.copyProperties(newResGRP, newResGRP_);
					capacityIntervals.add(newResGRP_);
				}
			}
			//capacityIntervals.add()
		} else {
			//reserve = 0;
		}

		capacityIntervals.removeAll(removeIntervals);

		//System.out.println("" + date) ;
	}

	public void request_from_stage(ACLMessage msgS,Agent myAgent){
		Date start, finish;
		//long reserve;
		String msgContent = "";

		if (msgS.getConversationId() == "resGroupOrder") {
			msgContent = msgS.getContent();
			try {
				Map<String, String> responce = parseResponce(msgContent);
				long load = Long.parseLong(responce.get("load"));
				Date date = formatter.parse(responce.get("date"));
				//start = date;
				//finish = date;
				long dayCapacity;
				long toNext = 1;
				while (toNext != 0) {
					//ПолучитьДоступностьВДняхВЗаданномИнтервале, возможно пока не надо интервал, просто на каждую последующую дату
					//Планируем
					List<RowCapacityResgroup> capacityIntervals = resourceService.getCapacityByResGroup(rg.getId(), date, rg.getSimultaneous());
					ArrayList<RowCapacityResgroup> reserve_intervals = reserveList.get(formatterDay.format(date));
					if (rg.getSimultaneous() == 0) {//Не надо уменьшать период при параллельной загрузке
						Mix_capacity_reserve(capacityIntervals, reserve_intervals);
					}
					Collections.sort(capacityIntervals, Collections.reverseOrder(RowCapacityResgroup.COMPARE_BY_DATE));
					//Сможем ли сделать
					for (RowCapacityResgroup c_interval : capacityIntervals) {
						//Если интервал находится правее границы
						//date - граница
						if (date.before(c_interval.getFinish())) {
							long milliseconds = c_interval.getFinish().getTime() - c_interval.getStart().getTime();
							int seconds = (int) (milliseconds / (1000));
							if (seconds >= load) {
								//интервал подходит
								if (date.before(c_interval.getStart())) {
									start = c_interval.getStart();
								} else {
									start = date;
								}
								finish = addSecondsToJavaUtilDate(start, (int) load);

								ArrayList<String> mStringArray = new ArrayList<String>();


								RowCapacityResgroup newResGRP = new RowCapacityResgroup();
								BeanUtils.copyProperties(c_interval, newResGRP);
								newResGRP.setStart(start);
								newResGRP.setFinish(finish);

								newResGRP.setIdStage(responce.get("idStage"));

								//Добавить резерв
								if (reserve_intervals == null) {
									reserve_intervals = new ArrayList<RowCapacityResgroup>();
								}

								reserve_intervals.add(newResGRP);
								reserveList.put(formatterDay.format(date), reserve_intervals);

								ACLMessage reply = msgS.createReply();
								reply.setPerformative(ACLMessage.PROPOSE);
								AgentOffer newOffer = new AgentOffer(load, responce.get("mode"), start, finish, responce.get("idStage"), newResGRP);
								offerList.put(responce.get("idStage"), newOffer);
								mStringArray.add("start:" + formatter.format(newOffer.getStart()));
								mStringArray.add("finish:" + formatter.format(newOffer.getFinish()));

								JSONArray mJSONArray = new JSONArray(mStringArray);
								reply.setContent(String.valueOf(mJSONArray));
								myAgent.send(reply);

								toNext = 0;
								break;
							}
						}
					}
					if (toNext == 1) {
						//Если оказались тут, то планирование не выполнено, пробуем другой день
						if (responce.get("mode").equals("0")) {
							date = atStartOfDay(addDayToJavaUtilDate(date, 1));
						} else {
							date = addDayToJavaUtilDate(date, -1);
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

	public void accept_from_stage(ACLMessage msgS, Agent myAgent){
		AgentOffer offer = offerList.get(msgS.getContent());
		if (offerList != null) {
			//очистить резерв
			ArrayList<RowCapacityResgroup> reserveL = reserveList.get(formatterDay.format(offer.getStart()));
			if (reserveL!= null){
				for (RowCapacityResgroup reserve_int: reserveL){
					//Находим ранее зарезервированный интервал
					if (reserve_int.getIdStage().equals(offer.getStage())){
						resourceService.registerRowCapacityResGroup(reserve_int);
						//Очистка резерва
						reserveL.remove(reserve_int);
						//Уведомление о загрузке
						ACLMessage reply = msgS.createReply();
						reply.setPerformative(ACLMessage.CONFIRM);
						ArrayList<String> mStringArray = new ArrayList<String>();
						mStringArray.add("start:" + formatter.format(offer.getStart()));
						mStringArray.add("finish:" + formatter.format(offer.getFinish()));
						JSONArray mJSONArray = new JSONArray(mStringArray);
						reply.setContent(String.valueOf(mJSONArray));
						myAgent.send(reply);
						break;
					}
				}
				//Очистка резерва
				reserveList.put(formatterDay.format(offer.getStart()), reserveL);
			}
			offerList.remove(msgS.getContent());
		}
	}
}

