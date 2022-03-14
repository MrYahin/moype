package ru.moype.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import ru.moype.model.*;
import ru.moype.resources.*;
import ru.moype.service.OrderProductionService;
import ru.moype.service.ResourcesService;
@Service
public class DispatchResponseBody {

	private List<Group> gr;
	private List<Item> items;

	@Autowired
	ResourcesService resourceService;
	@Autowired
	OrderProductionService orderService;
	@Autowired
	StageService stageService;

	public void setDispatchResponseBody() throws ParseException, JsonProcessingException {

		Calendar c = Calendar.getInstance();

		gr = new ArrayList<Group>();
		items = new ArrayList<Item>();

		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		List<Division> divisionList = resourceService.getAll();

		Iterator<Division> itDivision = divisionList.iterator();
		while (itDivision.hasNext()){
			Division division = itDivision.next();

			List<ResGroup> resGroupsList = resourceService.getResGroupsByDivision(division.getId());
			Iterator<ResGroup> itResGroup = resGroupsList.iterator();
			ArrayList<String> nestedGroups = new ArrayList<String>();
			while (itResGroup.hasNext()){
				nestedGroups.add(itResGroup.next().getId());
			}
			Group group = new Group(division.getId(),division.getName(), 1, "", nestedGroups);
			gr.add(group);

		}

		List<ResGroup>  resGroups = resourceService.getAllResGroup();
		for (ResGroup resGroup : resGroups) {
			Group ngroup = new Group(resGroup.getId(), resGroup.getName(), 2,"");
			gr.add(ngroup);
		}

		List<RowCalendar> rowCalendarList = resourceService.getCalendar();
		//Надо переделать
		//Вывод не рабочих дней
		for (RowCalendar day : rowCalendarList) {
			c.setTime(day.getDate()); //устанавливаем дату, с которой будет производить операции
			c.add(Calendar.DATE, 1);
			Date end = c.getTime();
			c.setTime(day.getDate());
			Date start = c.getTime();
			Item item = new Item(day.getDate().toString(), start, end, "", "", "positive", "background");
			items.add(item);
		}

		List<OrderProduction> orderList = orderService.getAll();
		Iterator<OrderProduction> itOrder = orderList.iterator();
		while (itOrder.hasNext()){
			Date defaultPlanStartDate;
			Date defaultPlanFinishDate;

			OrderProduction order = itOrder.next();

			List<Stage> stages = orderService.getStageList(order.getOrderId());
			if (order.getMode().equals("0")) { //ASAP
				defaultPlanStartDate = order.getStartDate();
				c.setTime(defaultPlanStartDate);
				c.add(Calendar.DATE, 1);
				defaultPlanFinishDate = c.getTime();
			}
			else{ //JIT
				defaultPlanFinishDate = order.getCompleteDate();
				c.setTime(defaultPlanFinishDate);
				c.add(Calendar.DATE, -1);
				defaultPlanStartDate = c.getTime();
			}
			for (Stage stage: stages) {
				String className = "new";

				if (stage.getState().equals("plan")) {
					if (stage.getIsCritical() == 1) {
						className = "critical_new";
					}
					if (stage.getBuffer()) {
						className = "buffer_new";
					}
				} else {
					if (stage.getState().equals("start")) {
						className = "start";
						if (stage.getIsCritical() == 1) {
							className = "critical";
						}
						if (stage.getBuffer()) {
							className = "buffer";
						}
					}
					if (stage.getState().equals("finish")) {
						className = "finish";
					}
				}

				String itemGroup = stage.getDivision();

				if (stage.getModelPlanning() == 1) {
					List<RowStageSchemeResgroup> schemeResgroups = stageService.getSchemeResGroups(stage.getIdStage());
					for (RowStageSchemeResgroup rsgrp: schemeResgroups) {
						itemGroup = rsgrp.getIdResGroup();
					}
				}

				Date start;
				Date end;

				if (stage.getPlanStartDate() == null) {
					start = defaultPlanStartDate;
				} else {
					start = stage.getPlanStartDate();
				}

				if (stage.getPlanFinishDate() == null) {
					end = defaultPlanFinishDate;
				} else {
					end = stage.getPlanFinishDate();
				}

				c.setTime(start);
				start = c.getTime();
				c.setTime(end);
				end = c.getTime();

				Item item = new Item(stage.getIdStage(), start, end, "" + stage.getNumber() + ": " + stage.getName() + " поз.:" + stage.getCodeNom() + " заказ: " + stage.getOrderId() + " " + stage.getState() ,itemGroup, className,  "");
				items.add(item);
			}	
		}	
	}
	
	public String DispatchResponseBodyToJSON() throws JsonProcessingException, ParseException {

		setDispatchResponseBody();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(df);
//		ObjectWriter ow = new ObjectMapper().writer().without(SerializationFeature.INDENT_OUTPUT);
//		ow.


		Map<String, List> m = new HashMap();
		 m.put("groups", gr);
		 m.put("items", items);
		 return  mapper.writeValueAsString(m);
	}

	public String resourceResponseBodyToJSON() throws JsonProcessingException, ParseException {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		List<GroupResource> grRes = new ArrayList<GroupResource>();
		List<ItemResource> itemsRes = new ArrayList<ItemResource>();
		Map<String, Long> avMap = new HashMap<>();
		List<RowCapacityResgroup> avList = resourceService.getAllAvailableResgroup();

		for(RowCapacityResgroup rc: avList){
			avMap.put(formatter.format(rc.getDate()) + rc.getIdResGroup(), rc.getAvailable());
		}

		Date tempDate = c.getTime();

		List<RowCapacityResgroup> capacityResgroups =  resourceService.getAllCapacity();
		for (RowCapacityResgroup c_resgrp: capacityResgroups){

			if (!tempDate.equals(c_resgrp.getDate())) {
				tempDate = c_resgrp.getDate();
				c.setTime(tempDate);
			}

			Date start = c.getTime();
			c.add(Calendar.HOUR_OF_DAY, 2);
			Date end = c.getTime();

			long av = avMap.get(formatter.format(c_resgrp.getDate()) + c_resgrp.getIdResGroup());
			if (c_resgrp.getAvailable() != av) {
				double per = Math.round((double)(av - c_resgrp.getAvailable())/av * 100);
				LabelResource label = new LabelResource("" + per + "%");
				ItemResource item = new ItemResource(start, av - c_resgrp.getAvailable(), c_resgrp.getIdResGroup(), end, label);
				itemsRes.add(item);
			} else {
				LabelResource label = new LabelResource("0%");
				ItemResource item = new ItemResource(start, c_resgrp.getAvailable(), c_resgrp.getIdResGroup(), end, label);
				itemsRes.add(item);
			}

			if (c_resgrp.getAvailable() != av) {
			//RowCapacityResgroup av = resourceService.getAvailableResgroupByDate(c_resgrp.getIdResGroup(), c_resgrp.getDate());
				ItemResource item3 = new ItemResource(start, av, c_resgrp.getIdResGroup(), end);
				itemsRes.add(item3);
			}
		}

		List<ResGroup> resGroups =  resourceService.getAllResGroup();
		for (ResGroup resgrp: resGroups){
			GroupResource gr = new GroupResource(resgrp.getId(), resgrp.getName());
			grRes.add(gr);
		}

		ObjectMapper mapper = new ObjectMapper();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mapper.setDateFormat(df);

		Map<String, List> m = new HashMap();
		m.put("groups", grRes);
		m.put("items", itemsRes);
		return  mapper.writeValueAsString(m);
	}

	//public List<ItemResource> getItemResourceResponseBody() throws ParseException, JsonProcessingException {

	//}
}
