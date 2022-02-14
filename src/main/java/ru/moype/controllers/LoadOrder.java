package ru.moype.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.moype.model.NomLinks;
import ru.moype.model.OrderProduction;
import ru.moype.model.RowStageSchemeResgroup;
import ru.moype.model.Stage;
import ru.moype.service.CategoryService;
import ru.moype.service.OrderProductionService;
import ru.moype.service.StageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.springframework.stereotype.Controller;


@RestController
public class LoadOrder {

	@Autowired
	StageService stageService;

	@Autowired
	OrderProductionService orderProductionServicе;

	@RequestMapping(path="app/loadOrder", method=RequestMethod.POST)
	@ResponseBody
	public String loadOrder(@RequestBody String order) throws JsonParseException, JsonMappingException, IOException {
				
		ObjectMapper objectMapper = new ObjectMapper();
		Map<?, ?> map = objectMapper.readValue(order, Map.class);
		String codeNom = "";
		
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			
			if ("orderProduction" == (String)entry.getKey()) {
				List<?> orders = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < orders.size(); i++) {
					Map<?, ?> orderProduction = (Map) orders.get(i);
					OrderProduction newOrderProduction = objectMapper.convertValue(orderProduction, OrderProduction.class);
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

			//if ("stages" == (String) entry.getKey()) {

			//	List<?> stages = (ArrayList<?>) entry.getValue();
			//	for (int i = 0; i < stages.size(); i++) {
			//		Map<?, ?> stage = (Map) stages.get(i);
			//		Stage newStage = objectMapper.convertValue(stage, Stage.class);
			//		newStage.setCodeNom(codeNom);
			//		categoryService.registerStage(newStage);
			//	}
			//}
			
		}
		
		return order;
	}

}
