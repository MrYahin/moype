package ru.moype.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.jade4spring.JadeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.ContainerID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import ru.moype.dbService.DBCategory;
import ru.moype.dbService.DBOrderProduction;
import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.resources.OrderProductionAgent;
import ru.moype.resources.OrderProductionManager;

@Service
public class OrderProductionService {

	private static DBOrderProduction dbOrderProduction;
	private OrderProduction order;
	
	@Autowired
	DBOrderProduction dbOrderProduction0;

	@Resource(name="testBean")
	private JadeBean jadeBean;
	
	//DBOrderProduction dbOrderProduction = new DBOrderProduction();
	//Это нужно для @Autowired, т.к. не должен быть статичным метод
	@PostConstruct     
	private void initStaticDBOrderProduction () {
	  dbOrderProduction = this.dbOrderProduction0;
	}	
	
	public void start(AgentContainer ac) throws Exception{
		// Get a hold on JADE runtime
        //Runtime rt = Runtime.instance();	
		
		//Profile profile = new ProfileImpl();
	    //profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
	    //profile.setParameter(Profile.MAIN_PORT, "1099");
	    // now set the default Profile to start a container
	    //AgentContainer ac = rt.createMainContainer(profile);
		//DBOrderProduction dborderProduction = new DBOrderProduction();
		int i = 1;
		
		//OrderProductionService orderProductionService = new OrderProductionService();
		
		List<OrderProduction> orderProductionList = getOrderAgents();
		
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
 
				//AgentController agentOrderProduction = ac.createNewAgent("orderProduction:" + order.getNumber(), "ru.moype.resources.OrderProductionAgent", argsJ);
				jadeBean.startAgent("orderProduction:" + order.getNumber(), "ru.moype.resources.OrderProductionAgent", argsJ);
				//agentOrderProduction.start();

				//i = i + 1;
				//����� �����������
				//for (int i = 1; i != 32; i++){		
				//	cal.set(2017, Calendar.JANUARY, i, 0, 0, 0);
				//	Date day = cal.getTime();
				//	avTime = calendarRes.getAvTimeRes(day, unit.getNumber());
				//}
			}
		}
		else{
		}
	}
	
	public List<OrderProduction> getAll(){
		return dbOrderProduction.getAll();
	}

	public List<OrderProduction> getOrderAgents(){
		String state = "planning";
		return dbOrderProduction.getAllState(state);
	}	

	public String createStageAgent(String orderId) throws Exception{

		OrderProduction order;
		List<Stage> stageList = new ArrayList<Stage>();	
		
		// Get a hold on JADE runtime
        Runtime rt = Runtime.instance();	
		
		Profile profile = new ProfileImpl();
	    //profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
	    //profile.setParameter(Profile.MAIN_PORT, "1098");
	    //profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
		profile.setParameter(Profile.MAIN_HOST, "192.168.31.27");
		profile.setParameter(Profile.MAIN_PORT, "1099");
		profile.setParameter(Profile.CONTAINER_NAME, "OrderContainer");
	    // now set the default Profile to start a container
	    //AgentContainer ac = rt.createMainContainer(profile);
	 
	    
		//Boolean result;
	    //OrderProductionManager orderProductionManager = new OrderProductionManager();
		//result = createStage(orderId);
	    
		order = dbOrderProduction.getOrder(orderId);

//		Iterator<Stage> itStage = stageList.iterator();
//		if  
//		while (itStage.hasNext()){
//			stage = itStage.next();
//			stateStage = stage.getState();  
//			if (!stateStage.equals("stop")){
//				try {
//			    	Object argsJ[] = new Object[3];
//			    	argsJ[0] = stage; 
			    	//argsJ[1] = order.getStartDate();
			    	//argsJ[2] = order.getCompleteDate();			
//					AgentController agentStage = ac.createNewAgent("stage:" + stage.getNumber(),  "ru.moype.resources.StageAgent", argsJ); 
//					agentOperation.start();
//					operationsStatus.put(agentOperation.getName(), "start");
//				} catch (Exception e) {
//					System.out.println(""); 
//				}
//			}
//		}		
		
		
		if (order != null) {
			Object argsJ[] = new Object[1];
			argsJ[0]= order;
				//OrderProductionAgent myAgent = new OrderProductionAgent();
			//myAgent.createAgent(order.getNumber());
			// Get a hold on JADE runtime
			//ContainerController cc = rt.createAgentContainer(profile);
		    // now set the default Profile to start a container
		    //AgentContainer ac = rt.createMainContainer(profile);
			//AgentController ac = cc.createNewAgent(order.getNumber(), "ru.moype.resources.OrderProductionAgent", argsJ);
			//ac.start();

			jadeBean.startAgent("orderProduction:" + order.getOrderId(), "ru.moype.resources.OrderProductionAgent", argsJ);

			//dbOrderProduction.updateOrder(orderId);
			
			return orderId;}
		else 
			return "read order problem";
		
	}
	
	public List<Stage> getStageList(String orderId){

    	List<Stage> stageList = new ArrayList<Stage>();
    	
    	//DBOrderProduction dbService = new DBOrderProduction();
        stageList = dbOrderProduction.readStageList(orderId);
        
        if (stageList != null) { return stageList; }
        else { return null; }
	}

	public List<Stage> getStageById(String stageId){

		List<Stage> stageList = new ArrayList<Stage>();

		//DBOrderProduction dbService = new DBOrderProduction();
		stageList = dbOrderProduction.readStageById(stageId);

		if (stageList != null) { return stageList; }
		else { return null; }
	}

	public List<Stage> getStageToPlanList(String orderId, String mode){

		List<Stage> stageList = new ArrayList<Stage>();

		//DBOrderProduction dbService = new DBOrderProduction();
		stageList = dbOrderProduction.readStageToPlanList(orderId, mode);

		if (stageList != null) { return stageList; }
		else { return null; }
	}

	public List<Stage> getStage(String orderId, long number, String codeNom){

		List<Stage> stageList = new ArrayList<Stage>();

		//DBOrderProduction dbService = new DBOrderProduction();
		stageList = dbOrderProduction.readStage(orderId, number, codeNom);

		if (stageList != null) { return stageList; }
		else { return null; }
	}



//	public int delete(Long[] ids) {
//		return dbCategory.delete(ids);
//	}

	public OrderProduction register(OrderProduction order) {
		return dbOrderProduction.register(order);
	}
}
