package ru.moype.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.jade4spring.JadeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.moype.dbService.DBOrderProduction;
import ru.moype.dbService.DBStage;
import ru.moype.model.Event;
import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.model.vis.Arrow;
import ru.moype.model.Task;

@Service
public class OrderProductionService {

	private static DBOrderProduction dbOrderProduction;

	@Autowired
	DBOrderProduction dbOrderProduction0;

	@Resource(name="testBean")
	private JadeBean jadeBean;

	@Autowired
	DBStage dbStage;

	//Это нужно для @Autowired, т.к. не должен быть статичным метод
	@PostConstruct     
	private void initStaticDBOrderProduction () {
	  dbOrderProduction = this.dbOrderProduction0;
	}	

	public List<OrderProduction> getAll(){
		return dbOrderProduction.getAll();
	}

	public OrderProduction getOrderById(String orderId){
		return dbOrderProduction.getOrderProduction(orderId);
	}

	public String createStageAgent(String orderId) throws Exception{

		//Зарегистрировать событие запуска заказа

		OrderProduction order = dbOrderProduction.getOrder(orderId);

		if (order != null) {
			Object argsJ[] = new Object[1];
			argsJ[0]= order;
			jadeBean.startAgent("orderProduction:" + order.getOrderId(), "ru.moype.agents.OrderProductionAgent", argsJ);
			return orderId;
		}
		else
			return "read order problem";

	}

	public List<Stage> getStageList(String orderId){

    	List<Stage> stageList = new ArrayList<Stage>();
        stageList = dbOrderProduction.getStageList(orderId);
        
        if (stageList != null) {
				return stageList; }
        else { return null; }
	}

	public List<Stage> getAllStageList(String orderId){

		List<Stage> stageList = new ArrayList<Stage>();

		stageList = dbOrderProduction.getAllStageList(orderId);

		if (stageList != null) {
			return stageList; }
		else { return null; }
	}

	public List<Stage> getStageById(String stageId){

		List<Stage> stageList = new ArrayList<Stage>();

		//DBOrderProduction dbService = new DBOrderProduction();
		stageList = dbOrderProduction.getStageById(stageId);

		if (stageList != null) { return stageList; }
		else { return null; }
	}

	public List<Stage> getStageToPlanList(String orderId, String mode){

		List<Stage> stageList = new ArrayList<Stage>();

		stageList = dbOrderProduction.getStageToPlanList(orderId, mode);

		if (stageList != null) { return stageList; }
		else { return null; }
	}

	public List<Stage> getStage(String orderId, long number, String batch){

		List<Stage> stageList = new ArrayList<Stage>();

		stageList = dbOrderProduction.getStage(orderId, number, batch);

		if (stageList != null) { return stageList; }
		else { return null; }
	}

	public HashSet<Task> getStageForCalculateCritical(String orderId){
		HashSet<Task> tasks = new HashSet<Task>();
		List<Task> taskL = dbOrderProduction.getBatchList(orderId);
		for (Task t : taskL) {
			HashSet<Task> dependencies = new HashSet<Task>();
			List<String> dependenciesL = dbOrderProduction.getDependencies(orderId, t.getName());
			for (String dep: dependenciesL) {
				for (Task ts : taskL) {
					if (ts.getName().equals(dep)) {
						dependencies.add(ts);
						break;
					}
				}
			}
			t.dependencies = dependencies;
			tasks.add(t);
		}
		return tasks;
	}

	public List<Arrow> getArrowsByStage(String stageId){

		List<Arrow> arrowList = new ArrayList<Arrow>();

		arrowList = dbOrderProduction.getArrowsByStage(stageId);

		if (arrowList != null) {
			return arrowList; }
		else { return null; }
	}

	public int updateCritical(Task t, String orderId) {

		List<Stage> stages = dbOrderProduction.getStagesByBatch(t.getName(), orderId);
		for (Stage st: stages){
			if (st.getIsCritical() != t.getOnCritical()){
				st.setIsCritical(t.getOnCritical());
				dbOrderProduction.updateStageCritical(st.getIdStage(), t.getOnCritical());
			}
		}

		return 0;
	}

	public int updateStagesToPlan(String orderId) {

		List<Stage> stages = dbOrderProduction.getStageList(orderId);
		for (Stage st: stages){
			st.setState("new");
			st.setMode("0");
			dbStage.register(st);
			deleteResGroupLoad(st.getIdStage());
		}

		return 0;
	}

	public int deleteResGroupLoad(String id) {
		return dbOrderProduction.deleteResGroupLoad(id);
	}

	public OrderProduction register(OrderProduction order) {
		return dbOrderProduction.register(order);
	}

	public int updateOrderStatus(OrderProduction order) {
		dbOrderProduction.updateOrderStatus(order);
		return 0;
	}

	public void setReplanStage(String idStage, String orderProd, Date notEarlier) throws Exception {
		//Меняем статус заказа
		OrderProduction order = dbOrderProduction.getOrderProduction(orderProd);
		order.setState("replan");
		dbOrderProduction.register(order);
		//меняем этап
		dbOrderProduction.setReplanStage(idStage, notEarlier);
	}

}
