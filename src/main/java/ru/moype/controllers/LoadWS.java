package ru.moype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.apache.log4j.Logger;

import ru.moype.WS1CClient;
import ru.moype.model.integration.GetSpecResponse;
import ru.moype.model.pdm.Category;
import ru.moype.model.integration.SpecStr;
import ru.moype.dbService.DBCategory;

@RestController
public class LoadWS {

	static Logger logger = Logger.getLogger(LoadWS.class);	
	
	@Autowired
	private WS1CClient ws1CClient;
	@Autowired
	DBCategory dbCategory;
	
	@RequestMapping(path="/loadWS", method=RequestMethod.POST)
	public void loadSpec() {
		GetSpecResponse wsResponce = ws1CClient.getResponse();
		
		if (wsResponce == null) {
			logger.error("Web service responce is NULL.");
			return;
		}

		if (wsResponce.getReturn() == null) {
			logger.warn("Web service cannot return any data.");
			return;
		}
		
		List<SpecStr> categoryList = wsResponce.getReturn().getSpecStr();
		
		if (!categoryList.isEmpty()) {
			logger.debug("Have " + categoryList.size() + " division records. Start load.");
			for (SpecStr category : categoryList) {
				Category cat = new Category();
				
				cat.setIdCategory(Long.parseLong(category.getId()));
				cat.setIdParentCategory(Integer.parseInt(category.getParentId()));
				cat.setCodeCategory(category.getArticle());
				cat.setDescriptionCategory(category.getName());
				
				dbCategory.register(cat);

				}
		} else
			logger.warn("SpecListt is EMPTY");		
	}
	
}
