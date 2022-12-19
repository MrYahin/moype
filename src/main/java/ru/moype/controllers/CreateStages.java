package ru.moype.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.moype.model.Stage;
import ru.moype.service.CategoryService;


@RestController
public class CreateStages {

	@Autowired
	CategoryService categoryService;
	
	@RequestMapping(path="app/pdm/addStage", method=RequestMethod.POST)
	@ResponseBody
	public String createStages(@RequestBody String order) throws JsonParseException, JsonMappingException, IOException {
				
		ObjectMapper objectMapper = new ObjectMapper();
		Map<?, ?> map = objectMapper.readValue(order, Map.class);
		String batch = "";
		
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			
			if ("batch" == (String)entry.getKey()) {
				batch = (String)entry.getValue();
			}
			
			if ("stages" == (String) entry.getKey()) { 

				List<?> stages = (ArrayList<?>) entry.getValue();
				for (int i = 0; i < stages.size(); i++) {
					Map<?, ?> stage = (Map) stages.get(i);
					Stage newStage = objectMapper.convertValue(stage, Stage.class);
					newStage.setBatch(batch);
					categoryService.registerStage(newStage);
				}
			}
			
		}
		
		return order;
	}

}
