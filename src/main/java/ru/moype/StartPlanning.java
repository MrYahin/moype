package ru.moype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.moype.service.CompanyService;

@Component
public class StartPlanning {

    @Autowired
    CompanyService companyService;

    public void startPlanning() throws Exception {
        companyService.start();
    }
}
