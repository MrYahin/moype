package ru.moype.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.moype.model.*;
import ru.moype.service.ResourcesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.springframework.stereotype.Controller;


@RestController
public class LoadResources {

	@Autowired
	ResourcesService resourcesServicе;

	@RequestMapping(path="app/loadResources", method=RequestMethod.POST)
	@ResponseBody
	public String loadOrder(@RequestBody String resources) throws JsonParseException, JsonMappingException, IOException {
				
		ObjectMapper objectMapper = new ObjectMapper();
		Map<?, ?> map = objectMapper.readValue(resources, Map.class);

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			
			if ("divisions" == (String)entry.getKey()) {
				List<?> divisions = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < divisions.size(); i++) {
					Map<?, ?> division = (Map) divisions.get(i);
					Division newDivision = objectMapper.convertValue(division, Division.class);
					resourcesServicе.registerDivision(newDivision);
				}
			}

			if ("resGroup" == (String)entry.getKey()) {
				List<?> resGroups = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < resGroups.size(); i++) {
					Map<?, ?> resGroup = (Map) resGroups.get(i);
					RowCapacityResgroup newResGroup = objectMapper.convertValue(resGroup, RowCapacityResgroup.class);
					resourcesServicе.registerResGroup(newResGroup);
				}
			}

			if ("resources" == (String)entry.getKey()) {
				//List<?> resources = (ArrayList<?>) entry.getValue();
				//for (int i = 0; i < nomLinks.size(); i++) {
				//	Map<?, ?> nomLink = (Map) nomLinks.get(i);
				//	NomLinks newRowNomLink = objectMapper.convertValue(nomLink, NomLinks.class);
				//	stageService.saveRowNomLink(newRowNomLink);
				//}
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
		
		return "ok";
	}

}
