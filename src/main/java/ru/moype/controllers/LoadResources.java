package ru.moype.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.moype.model.*;
import ru.moype.service.ResourcesService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//import org.springframework.stereotype.Controller;


@RestController
public class LoadResources {

	@Autowired
	ResourcesService resourcesServicе;

	@RequestMapping(path="app/loadResources", method=RequestMethod.POST)
	@ResponseBody
	public String loadOrder(@RequestBody String resources) throws JsonParseException, JsonMappingException, IOException, ParseException {
				
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

			if ("rowsResGroup" == (String)entry.getKey()) {
				List<?> resGroups = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < resGroups.size(); i++) {
					Map<?, ?> resGroup = (Map) resGroups.get(i);
					RowCapacityResgroup newResGroup = objectMapper.convertValue(resGroup, RowCapacityResgroup.class);
					resourcesServicе.updateRowCapacityResGroup(newResGroup);
				}
			}

			if ("resGroups" == (String)entry.getKey()) {
				List<?> resGroups = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < resGroups.size(); i++) {
					Map<?, ?> resGroup = (Map) resGroups.get(i);
					ResGroup newResGroup = objectMapper.convertValue(resGroup, ResGroup.class);
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

			if ("calendar" == (String)entry.getKey()) {
				List<?> calendar = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < calendar.size(); i++) {
					Map<?, ?> rowDate = (Map) calendar.get(i);
					RowCalendar rowCal = objectMapper.convertValue(rowDate, RowCalendar.class);
					resourcesServicе.registerRowCalendar(rowCal);
				}
			}
		}
		
		return "ok";
	}

}
