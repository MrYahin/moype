//Тестирование получения данных из 1C. Тестовый пример

package ru.moype.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.moype.model.Category;
import ru.moype.model.Stage;
import ru.moype.service.CategoryService;

@RestController
public class scheduler {

	@Autowired
	CategoryService categoryService;	
	
	@RequestMapping(path="/scheduler", method=RequestMethod.GET)
	public List<Stage> getOperation() throws ParseException{
		List<Stage> opList = new ArrayList<Stage>();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Stage ob1 = new Stage();
		ob1.setId(1);
		ob1.setName("item 1");
		ob1.setPlanStartDate(formatter.parse("2020-05-24"));
		ob1.setPlanFinishDate(formatter.parse("2020-05-24"));

		Stage ob2 = new Stage();
		ob2.setId(2);
		ob2.setName("item 2");
		ob2.setPlanStartDate(formatter.parse("2020-05-24"));
		ob2.setPlanFinishDate(formatter.parse("2020-05-24"));

		Stage ob3 = new Stage();
		ob3.setId(3);
		ob3.setName("item 3");
		ob3.setPlanStartDate(formatter.parse("2020-05-24"));
		ob3.setPlanFinishDate(formatter.parse("2020-05-24"));
		
		Stage ob4 = new Stage();
		ob4.setId(4);
		ob4.setName("item 4");
		ob4.setPlanStartDate(formatter.parse("2013-05-24"));
		ob4.setPlanFinishDate(formatter.parse("2013-05-24"));

		Stage ob5 = new Stage();
		ob5.setId(5);
		ob5.setName("item 5");
		ob5.setPlanStartDate(formatter.parse("2020-05-24"));
		ob5.setPlanFinishDate(formatter.parse("2020-05-25"));

		Stage ob6 = new Stage();
		ob6.setId(6);
		ob6.setName("item 6");
		ob6.setPlanStartDate(formatter.parse("2020-05-25"));
		ob6.setPlanFinishDate(formatter.parse("2020-05-27"));
		
		opList.add(ob1);
		opList.add(ob2);
		opList.add(ob3);
		opList.add(ob4);
		opList.add(ob5);
		opList.add(ob6);
		
		return opList;
	}	
	
}
