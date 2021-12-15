package ru.moype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBStage;
import ru.moype.model.NomLinks;

import java.util.List;

@Service
public class StageService {

    @Autowired
    DBStage dbStage;

    public List<NomLinks> getInputs(String idStage) {
       return dbStage.getInputs(idStage);
    }
}
