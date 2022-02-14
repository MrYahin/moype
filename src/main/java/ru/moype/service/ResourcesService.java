package ru.moype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBResources;
import ru.moype.model.*;

import java.util.List;

@Service
public class ResourcesService {

    @Autowired
    DBResources dbResources;

    public List<Division> getAll(){
        return dbResources.getAll();
    }

    public Division registerDivision(Division division) {
        return dbResources.registerDivision(division);
    }

    public RowCapacityResgroup registerResGroup(RowCapacityResgroup resGroup) {
        return dbResources.registerResGroup(resGroup);
    }
}
