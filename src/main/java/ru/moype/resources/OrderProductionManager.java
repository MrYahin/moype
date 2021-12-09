package ru.moype.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ru.moype.dbService.DBOrderProduction;
import ru.moype.model.OrderProduction;
import ru.moype.service.OrderProductionService;
//import ru.moype.service.OrderProductionService;

public class OrderProductionManager {

//	private AgentContainer ac;

	public OrderProductionManager() {
  //      this.ac = ac;
    }

	@Autowired
	static
	OrderProductionService orderProductionService;
	
	public static boolean start() throws Exception{
		// Get a hold on JADE runtime
        Runtime rt = Runtime.instance();	
		
		Profile profile = new ProfileImpl();
	    profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
	    profile.setParameter(Profile.MAIN_PORT, "1099");
	    // now set the default Profile to start a container
	    AgentContainer ac = rt.createMainContainer(profile);
		//DBOrderProduction dborderProduction = new DBOrderProduction();
		int i = 1;
		
		//OrderProductionService orderProductionService = new OrderProductionService();
		
		List<OrderProduction> orderProductionList = orderProductionService.getAll();
		
		OrderProduction order = new OrderProduction();
		
		//long interval = 0;	//������
		//CalendarResAv avTime;

		//units = unit.getUnits();
		//Calendar cal = Calendar.getInstance();
		
		//CalendarResAv calendarRes = new CalendarResAv(); 
		if (orderProductionList != null) {
			Iterator<OrderProduction> itOP = orderProductionList.iterator();
			while (itOP.hasNext()){
				//������� �������

		    	order = itOP.next();				
		    	Object argsJ[] = new Object[1];
		    	argsJ[0]= order; 
 
				AgentController agentOrderProduction = ac.createNewAgent("orderProduction:" + order.getNumber(), "ru.moype.resources.OrderProductionAgent", argsJ); 
				agentOrderProduction.start();

				//i = i + 1;
				//����� �����������
				//for (int i = 1; i != 32; i++){		
				//	cal.set(2017, Calendar.JANUARY, i, 0, 0, 0);
				//	Date day = cal.getTime();
				//	avTime = calendarRes.getAvTimeRes(day, unit.getNumber());
				//}
			}
			return true;
		}
		else{
			return false;
		}
	}


}
