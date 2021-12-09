package ru.moype.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.resource.*;
import ru.moype.dbService.DBOrderProduction;
import ru.moype.resources.*;
import ru.moype.service.NomenclatureListService;
import ru.moype.service.OrderProductionService;

@RestController
public class StartJADE{

	@Autowired
	OrderProductionService orderProductionService;  	
	
	@Autowired
	ApplicationContext context;
	
	@RequestMapping(path="start", method=RequestMethod.POST)
	public void startJADE() throws Exception{
	//	AgentContainer ac;
        //run JADE
        //ac = start();
        //ContainerController container;
	//	Runtime rt = Runtime.instance();
		Object test = context.getBean("test");
        //Order agents
       // orderProductionService.start();
        
        //Resource agents
	//	OrderProductionManager resManager = new OrderProductionManager(ac);
	//	resManager.start();
	}
	
	public AgentContainer start() throws Exception{

        // Get a hold on JADE runtime
        Runtime rt = Runtime.instance();
        // Exit the JVM when there are no more containers around
        rt.setCloseVM(true);
        // Create a default profile
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
        profile.setParameter(Profile.MAIN_PORT, "1099");
        // now set the default Profile to start a container
        AgentContainer ac = rt.createMainContainer(profile);
        AgentController controller = ac.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);//массив объектов из одной строки
        controller.start();
       
        
        
        return ac;
//        AgentController agentOrderClient = ac.createNewAgent("orderClient", "resources.agents.HelloWorldAgent", new Object[0]); 
//        agentOrderClient.start();
//        Object argsJ[] = new Object[1];
//        argsJ[0]= "MySeller"; 
//        AgentController bookBuyerAgent = ac.createNewAgent("bookBuyer", "resources.agents.BookBuyerAgent", argsJ); 
//        bookBuyerAgent.start();
//        AgentController bookSellerAgent = ac.createNewAgent("bookSeller", "resources.agents.BookSellerAgent", new Object[0]); 
//        bookSellerAgent.start();        
        
        //Agent agent = new HelloWorldAgent();
        //System.out.println("My local-name is "+ agent.getAID().getLocalName());		
		
	}

	public static String createStageAgent(String orderId) throws Exception{
	
		// Get a hold on JADE runtime
        Runtime rt = Runtime.instance();	
		
		Profile profile = new ProfileImpl();
	    profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
	    profile.setParameter(Profile.MAIN_PORT, "1099");
	    // now set the default Profile to start a container
	    AgentContainer ac = rt.createMainContainer(profile);
	 
		Boolean result;
	    OrderProductionManager orderProductionManager = new OrderProductionManager();
		//result = orderProductionManager.createStage(orderId);
	
		return orderId;
	}
}
