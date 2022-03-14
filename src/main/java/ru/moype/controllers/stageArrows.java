package ru.moype.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.moype.model.Nomenclature;
import ru.moype.service.NomenclatureListService;
import ru.moype.service.OrderProductionService;

import java.util.List;

@RestController
public class stageArrows {

	@Autowired
	OrderProductionService orderProductionService;
	
	@RequestMapping(path="app/plAssistant/stageArrows", method=RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getArrowsByStage(@RequestBody String idStage) throws JsonProcessingException {
		idStage = idStage.replaceAll("=", "");
		ObjectWriter ow = new ObjectMapper().writer().without(SerializationFeature.INDENT_OUTPUT);
		return ow.writeValueAsString(orderProductionService.getArrowsByStage(idStage));
	}

}
