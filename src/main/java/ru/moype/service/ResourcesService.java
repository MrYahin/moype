package ru.moype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moype.dbService.DBResources;
import ru.moype.model.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class ResourcesService {

    @Autowired
    DBResources dbResources;

    public List<Division> getAll(){
        return dbResources.getAll("1");
    }

    public List<ResGroup> getAllResGroup(){
        return dbResources.getResGroups();
    }

    public List<RowCalendar> getCalendar(){
        return dbResources.getCalendar();
    }

    public List<ResGroup> getResGroupsByDivision(String division){
        return dbResources.getResGroupsByDivision(division);
    }

    public RowCapacityResgroup getCapacityByResGroup(String idResgroup, Date date) throws ParseException {
        return dbResources.getResgroupByDate(idResgroup, date);
    }

    public RowCapacityResgroup getAvailableResgroupByDate(String idResgroup, Date date) throws ParseException {
        return dbResources.getAvailableResgroupByDate(idResgroup, date);
    }

    public List<RowCapacityResgroup> getAllAvailableResgroup() throws ParseException {
        return dbResources.getAllAvailableResgroup();
    }

    public List<RowCapacityResgroup> getAllCapacity() throws ParseException {
        return dbResources.getAllCapacity();
    }

    public Division registerDivision(Division division) {
        return dbResources.registerDivision(division);
    }

    public RowCapacityResgroup updateRowCapacityResGroup(RowCapacityResgroup rowResGroup) throws ParseException {
        return dbResources.updateRowCapacityResGroup(rowResGroup);
    }

    public RowCapacityResgroup registerRowCapacityResGroup(RowCapacityResgroup rowResGroup){
        return dbResources.registerRowCapacityResGroup(rowResGroup);
    }

    public RowCalendar registerRowCalendar(RowCalendar rowCalendar) {
        return dbResources.registerRowCalendar(rowCalendar);
    }

    public ResGroup registerResGroup(ResGroup resGroup) {
        return dbResources.registerResGroup(resGroup);
    }

}
