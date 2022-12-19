package ru.moype.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.moype.model.*;
import ru.moype.service.CategoryService;
import ru.moype.service.EventsService;
import ru.moype.service.OrderProductionService;
import ru.moype.service.StageService;

import java.io.IOException;
import java.util.*;

@RestController
public class LoadOrder {

	@Autowired
	StageService stageService;

	@Autowired
	OrderProductionService orderProductionServicе;

	@Autowired
	EventsService evServicе;

	@RequestMapping(path="app/loadOrder", method=RequestMethod.POST)
	@ResponseBody
	public String loadOrder(@RequestBody String order) throws Exception {
				
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
		Map<?, ?> map = objectMapper.readValue(order, Map.class);

		OrderProduction newOrderProduction = null;

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			
			if ("orderProduction" == (String)entry.getKey()) {
				List<?> orders = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < orders.size(); i++) {
					Map<?, ?> orderProduction = (Map) orders.get(i);
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					newOrderProduction = objectMapper.convertValue(orderProduction, OrderProduction.class);
					orderProductionServicе.register(newOrderProduction);				}
			}

			if ("stage" == (String)entry.getKey()) {
				List<?> stages = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < stages.size(); i++) {
					Map<?, ?> stage = (Map) stages.get(i);
					Stage newStage = objectMapper.convertValue(stage, Stage.class);
					stageService.register(newStage);				}
			}

			if ("nomlinks" == (String)entry.getKey()) {
				List<?> nomLinks = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < nomLinks.size(); i++) {
					Map<?, ?> nomLink = (Map) nomLinks.get(i);
					NomLinks newRowNomLink = objectMapper.convertValue(nomLink, NomLinks.class);
					stageService.registerRowNomLink(newRowNomLink);
				}
			}

			if ("schemeResgroup" == (String)entry.getKey()) {
				List<?> scResGroups = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < scResGroups.size(); i++) {
					Map<?, ?> scResGroup = (Map) scResGroups.get(i);
					RowStageSchemeResgroup newScResGroup = objectMapper.convertValue(scResGroup, RowStageSchemeResgroup.class);
					stageService.registerSchemeResGroup(newScResGroup);
				}
			}
		}

		if (newOrderProduction != null) {
			//Зарегистрировать событие старта планирования заказа
			Event ev = new Event();
			ev.setType("stopOrder");
			ev.setStageId("");
			ev.setOrderId(newOrderProduction.getNumber());
			ev.setState("new");
			Date date = new Date();
			ev.setTimeOfEvent(date);

			evServicе.register(ev);

			Event ev_start = new Event();
			ev_start.setType("startOrder");
			ev_start.setStageId("");
			ev_start.setOrderId(newOrderProduction.getNumber());
			ev_start.setState("new");
			date = new Date();
			ev_start.setTimeOfEvent(date);

			evServicе.register(ev_start);
		}
		return order;
	}

}
