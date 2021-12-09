package ru.moype.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.moype.dbService.DBNomenclatureList;
import ru.moype.model.Nomenclature;

@Service
public class NomenclatureListService {
	@Autowired
	DBNomenclatureList dbNomenclature;
	
	public List<Nomenclature> getAll(){
		return dbNomenclature.getAll();
	}

}
