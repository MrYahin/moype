package ru.moype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBStage;
import ru.moype.model.NomLinks;
import ru.moype.model.Stage;

import java.util.List;

@Service
public class NomLinksService {

    @Autowired
    DBStage dbStage;

    public List<NomLinks> getInputs(String idStage) {
       return dbStage.getInputs(idStage);
    }

    public Stage save(Stage stage) {
        return dbStage.save(stage);
    }

}
