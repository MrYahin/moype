package ru.moype.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.service.OrderProductionService;

public class DispatchResponseBody {

	private List<Group> gr;
	private List<Item> items;

	public DispatchResponseBody() throws ParseException {

		gr = new ArrayList<Group>();
		items = new ArrayList<Item>();
		OrderProductionService orderService = new OrderProductionService();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Group group = new Group("Конструкторский отдел", "Конструкторский отдел", 1, "Конструкторский отдел");
		gr.add(group);
		//Нужно добавлять подчиненные группы по номенклатуре
		Group group1 = new Group("участок коммплектации", "участок коммплектации", 1, "участок коммплектации");
		gr.add(group1);
		Group group2 = new Group("механический цех", "механический цех", 1, "механический цех");
		gr.add(group2);
		Group group3 = new Group("сборочный цех", "сборочный цех", 1, "сборочный цех");
		gr.add(group3);
		Group group4 = new Group("НТО", "НТО", 1, "НТО");
		gr.add(group4);
		Group group5 = new Group("цех защитных покрытий", "цех защитных покрытий", 1, "цех защитных покрытий");
		gr.add(group5);
		Group group6 = new Group("испытательная станция", "испытательная станция", 1, "испытательная станция");
		gr.add(group6);
		Group group7 = new Group("заготовительный участок", "заготовительный участок", 1, "заготовительный участок");
		gr.add(group7);
		Group group8 = new Group("механический участок", "механический участок", 1, "механический участок");
		gr.add(group8);		
		Group group9 = new Group("ЦЗП", "ЦЗП", 1, "ЦЗП");
		gr.add(group9);	
		Group group10 = new Group("ОТК", "ОТК", 1, "ОТК");
		gr.add(group10);		
		Group group11 = new Group("электро-монтажный цех", "электро-монтажный цех", 1, "электро-монтажный цех");
		gr.add(group11);	
		
		List<OrderProduction> orderList = orderService.getAll();
		Iterator<OrderProduction> itOrder = orderList.iterator();
		while (itOrder.hasNext()){
			OrderProduction order = itOrder.next();

			List<Stage> stages = orderService.getStageList(order.getOrderId());
			Iterator<Stage> itStage = stages.iterator();
			while (itStage.hasNext()){
				Stage stage = itStage.next();
				Item item = new Item((stage.getPlanStartDate() == null) ? formatter.parse("2015-1-10"):stage.getPlanStartDate() /*formatter.parse("2015-1-10")*/, (stage.getPlanFinishDate() == null) ? formatter.parse("2015-1-11"):stage.getPlanFinishDate()/*formatter.parse("2015-1-11")*/, stage.getDivision() , order.getOrderId(), "" + stage.getNumber() + ": " + stage.getName() + " поз.:" + stage.getCodeNom() + " заказ: " + stage.getOrderId() + " " + stage.getState() , stage.getIdStage());
				items.add(item);
			}	
		}	
	}
	
	public String DispatchResponseBodyToJSON() throws JsonProcessingException {
		
		ObjectWriter ow = new ObjectMapper().writer().without(SerializationFeature.INDENT_OUTPUT);
		
		Map<String, List> m = new HashMap();
		 m.put("groups", gr);
		 m.put("items", items);
		 return  ow.writeValueAsString(m);
	}
}
