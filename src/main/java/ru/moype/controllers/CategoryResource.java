package ru.moype.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.moype.model.pdm.Category;
import ru.moype.model.Stage;
import ru.moype.service.CategoryService;

@RestController
public class CategoryResource {

	@Autowired
	CategoryService categoryService;
	
	@RequestMapping(path="app/pdm/category", method=RequestMethod.GET)
	@ResponseBody
	public List<Category> getCategory(){
		return categoryService.getAll();
	}

	@RequestMapping(path="app/pdm/stageList", method=RequestMethod.POST)
	@ResponseBody
	public List<Stage> listOfStage(@RequestBody String batch){
		return categoryService.listOfStage(batch);
	}	
	
	@RequestMapping(value="app/pdm/category", method = RequestMethod.DELETE)
	@ResponseBody
	public int delete(@RequestBody Long[] array) {
		return categoryService.delete(array);
	}
	
	@RequestMapping(value="app/pdm/category", method = RequestMethod.POST)
	@ResponseBody
	public Category register(@RequestBody Category category) {
		return categoryService.register(category);
	}	
	
}
