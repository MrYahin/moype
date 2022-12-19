package ru.moype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBStage;
import ru.moype.model.NomLinks;
import ru.moype.model.RowStageSchemeResgroup;
import ru.moype.model.Stage;

import java.util.List;

@Service
public class StageService {

    @Autowired
    DBStage dbStage;

    public List<NomLinks> getInputs(String idStage) {
       return dbStage.getInputs(idStage);
    }

    public List<NomLinks> getOutputs(String idStage) {
        return dbStage.getOutputs(idStage);
    }

    public List<RowStageSchemeResgroup> getSchemeResGroups(String idStage){
        return dbStage.getSchemeResGroups(idStage);
    }

    public Stage save(Stage stage) {
        return dbStage.save(stage);
    }

    public NomLinks saveLink(NomLinks nomL) {
        return dbStage.saveLink(nomL);
    }

    public Stage register(Stage stage) {
        return dbStage.register(stage);
    }

    public NomLinks registerRowNomLink(NomLinks rowNomLink) {
        return dbStage.registerNomLink(rowNomLink);
    }

    public RowStageSchemeResgroup registerSchemeResGroup(RowStageSchemeResgroup rowScheme) {
        return dbStage.registerScheme(rowScheme);
    }
}
