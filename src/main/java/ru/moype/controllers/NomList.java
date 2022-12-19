package ru.moype.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.moype.model.Nomenclature;
import ru.moype.service.NomenclatureListService;

@RestController
public class NomList {

	@Autowired
	NomenclatureListService nomenclatureService;
	
	@RequestMapping(path="app/pdm/getNomList", method=RequestMethod.GET)
	@ResponseBody
	public List<Nomenclature> getCategory(){
		return nomenclatureService.getAll();
	}

}
