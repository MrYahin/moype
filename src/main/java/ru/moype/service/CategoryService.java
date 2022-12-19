package ru.moype.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.moype.dbService.DBCategory;
import ru.moype.dbService.DBStage;
import ru.moype.model.pdm.Category;
import ru.moype.model.Stage;

@Service
public class CategoryService {
	@Autowired
	DBCategory dbCategory;
	
	public List<Category> getAll(){
		return dbCategory.getAll();
	}

	public int delete(Long[] ids) {
		return dbCategory.delete(ids);
	}

	public Category register(Category category) {
		return dbCategory.register(category);
	}
	
	@Autowired
	DBStage dbStage;	
	
	public List<Stage> listOfStage(String batch){
		return dbStage.getAll(batch);
	}
	
	public Stage registerStage(Stage stage){
		return dbStage.register(stage);
	}
	
}
