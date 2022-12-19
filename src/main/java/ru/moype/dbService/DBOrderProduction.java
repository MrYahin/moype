package ru.moype.dbService;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import ru.moype.model.NomLinks;
import ru.moype.model.OrderProduction;
import ru.moype.model.OrderProductionActualState;
import ru.moype.model.Stage;
import ru.moype.model.vis.Arrow;
import ru.moype.model.Task;

@Repository
public class DBOrderProduction{

	@PersistenceContext
	private EntityManager em;

	//OrderProduction
	private static final String READ_ORDERS = "Select c From OrderProduction c";
	private static final String READ_ORDER = "Select c From OrderProduction c where c.orderId = :orderid";

	private static final String READ_ORDER_BY_STATE = "Select c From OrderProductionActualState c where c.state = :state";
	private static final String READ_ORDER_STATE = "Select c From OrderProductionActualState c where c.orderId = :orderid order by c.changeDate DESC";
	//private static final String UPDATE_ORDER_STATUS = "Update OrderProductionActualState c Set c.state= :state, c.changeDate = :change where c.orderId = :orderid";
	//Stage
	private static final String READ_STAGES = "Select c From Stage c where c.orderId = :orderid and c.state <> 'finish'";
	private static final String READ_ALL_STAGES = "Select c From Stage c where c.orderId = :orderid";
	private static final String READ_STAGE_BY_ID = "Select c From Stage c where c.idStage = :stageid";
	private static final String READ_STAGE_BY_BATCH = "Select c From Stage c where c.batch = :batch and c.orderId =:orderid";
	private static final String READ_STAGE_TO_PLAN_ASAP = "Select c From Stage c where c.orderId = :orderid and c.state = 'new' order by c.batch, c.number";
	private static final String READ_STAGE_TO_PLAN_JIT = "Select c From Stage c where c.orderId = :orderid and c.state = 'new' order by c.batch, c.number DESC";
	private static final String READ_STAGE = "Select c From Stage c where c.orderId = :orderid and c.number = :number and c.batch = :batch";
	private static final String UPDATE_STAGE_CRITICAL = "Update Stage c Set c.isCritical = :isCritical where c.idStage = :idStage";
	private static final String UPDATE_STAGE_NOT_EARLIER = "Update Stage c Set c.notEarlier = :notEarlier, c.state = 'replan' where c.idStage = :idStage";
	private static final String READ_BATCH = "Select c From Stage c where c.orderId = :orderid order by c.batch";
	private static final String READ_STAGE_BY_ID_ORDER = "Select c From Stage c where c.batch = :batch and c.orderId =:orderid order by c.number";
	//NomLinks
	private static final String READ_DEPENDENCIES = "Select c.stageIdInput From NomLinks c where c.orderId = :orderId and c.batch = :batch order by c.batch";
	private static final String READ_DEPENDENCIES_BY_STAGE = "Select c From NomLinks c where c.stageId = :idStage";
	private static final String READ_ENTRIES_BY_STAGE = "Select c From NomLinks c where c.stageIdInput = :idStage";
	//ResGroup
	private static final String DELETE_CAPACITY_BY_STAGE ="DELETE FROM RowCapacityResgroup c where c.idStage =:id";

	public DBOrderProduction() {
    }	

    //OrderProduction

	@SuppressWarnings("unchecked")
	public List<OrderProduction> getAll() {
		System.out.println("DBOrderProduction - getALL");
		Query query = em.createQuery(READ_ORDERS);
		return query.getResultList();
	}    

	@SuppressWarnings("unchecked")
	public List<OrderProduction> getAllByState(String state) {
		System.out.println("DBOrderProduction - getAllState");
		Query query = em.createQuery(READ_ORDER_BY_STATE);
		query.setParameter("state", state);		
		return query.getResultList();
	}    

	//@Transactional
	//public void updateOrder(String orderId, Date change) {
	//	System.out.println("DBOrderProduction - updateOrder");
	//	Query query = em.createQuery(UPDATE_ORDER_STATUS);
	//	query.setParameter("orderid", orderId);
	//	query.setParameter("change", change);
	//	query.executeUpdate();
	//}

	@Transactional
	public OrderProduction getOrderProduction(String orderId) {
		System.out.println("DBOrderProduction - getOrderProduction");
		Query query = em.createQuery(READ_ORDER);
		query.setParameter("orderid", orderId);
		List<OrderProduction> entityList = query.getResultList();
		if (entityList.size() != 0) {
			return entityList.get(0);
		}
		else
			return null;
	}

	//STAGES

	@SuppressWarnings("unchecked")
	public List<Stage> getStageList(String idOrder) {
		System.out.println("DBOrderProduction - readStageList");
		Query query = em.createQuery(READ_STAGES);
		query.setParameter("orderid", idOrder);
		return query.getResultList();
	}

	public List<Stage> getAllStageList(String idOrder) {
		System.out.println("DBOrderProduction - readAllStageList");
		Query query = em.createQuery(READ_ALL_STAGES);
		query.setParameter("orderid", idOrder);
		return query.getResultList();
	}

	public List<Arrow> getArrowsByStage(String stageId) {
		System.out.println("DBOrderProduction - readArrowsByStage");
		//цепочка этапов
		List<Stage> stages = getStageById(stageId);
		String batch = "";
		String orderId = "";
		for (Stage stg : stages) {
			batch = stg.getBatch();
			orderId = stg.getOrderId();
		}
		List<Stage> stagesByCode = getStagesByBatchOrder(batch, orderId);
		long i = 0;
		String lastId = "";
		String firstId = "";
		List<Arrow> arrows = new ArrayList<Arrow>();
		for (Stage stg : stagesByCode) {
			if (i != 0 ) {
				Arrow ar = new Arrow("ar" + i, lastId, stg.getIdStage());
				arrows.add(ar);
			}
			else {
				firstId = stg.getIdStage();
			}
			i++;
			lastId = stg.getIdStage();
		}
		//состав
		List<NomLinks> comp = getCompByStage(firstId);
		for (NomLinks cm : comp) {
			i++;
			Arrow ar = new Arrow("ar" + i, cm.getStageIdInput(), firstId);
			arrows.add(ar);
		}
		//входимость
		List<NomLinks> entries = getEntryByStage(lastId);
		for (NomLinks cm : entries) {
			i++;
			Arrow ar = new Arrow("ar" + i, lastId, cm.getStageId());
			arrows.add(ar);
		}
		return arrows;
	}

	public List<Stage> getStagesByBatch(String batch, String idOrder) {
		System.out.println("DBOrderProduction - readStagesByBatch");
		Query query = em.createQuery(READ_STAGE_BY_BATCH);
		query.setParameter("orderid", idOrder);
		query.setParameter("batch", batch);
		return query.getResultList();
	}

	public List<Stage> getStagesByBatchOrder(String batch, String idOrder) {
		System.out.println("DBOrderProduction - readStagesByBatchOrder");
		Query query = em.createQuery(READ_STAGE_BY_ID_ORDER);
		query.setParameter("orderid", idOrder);
		query.setParameter("batch", batch);
		return query.getResultList();
	}


	public List<Stage> getStageById(String stageid) {
		System.out.println("DBOrderProduction - readStageById");
		Query query = em.createQuery(READ_STAGE_BY_ID);
		query.setParameter("stageid", stageid);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Stage> getStageToPlanList(String idOrder, String mode) {
		System.out.println("DBOrderProduction - readStageToPlanList");
		String queryText;
		if (mode.equals("0")) { queryText = READ_STAGE_TO_PLAN_ASAP; }
		else { queryText = READ_STAGE_TO_PLAN_JIT; }
		Query query = em.createQuery(queryText);
		query.setParameter("orderid", idOrder);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Stage> getStage(String idOrder, long number, String batch) {
		System.out.println("DBOrderProduction - readStage");
		Query query = em.createQuery(READ_STAGE);
		query.setParameter("orderid", idOrder);
		query.setParameter("number", number);
		query.setParameter("batch", batch);
		return query.getResultList();
	}

	//NOMLINKS

	public List<NomLinks> getCompByStage(String idStage) {
		System.out.println("DBOrderProduction - readCompByStage");
		Query query = em.createQuery(READ_DEPENDENCIES_BY_STAGE);
		query.setParameter("idStage", idStage);
		return query.getResultList();
	}

	public List<NomLinks> getEntryByStage(String idStage) {
		System.out.println("DBOrderProduction - readEntryByStage");
		Query query = em.createQuery(READ_ENTRIES_BY_STAGE);
		query.setParameter("idStage", idStage);
		return query.getResultList();
	}

	public List<Task> getBatchList(String idOrder) {
		System.out.println("DBOrderProduction - getBatchList");
		Query query = em.createQuery(READ_BATCH);
		query.setParameter("orderid", idOrder);
		List<Stage> stgList = query.getResultList();
		List<Task> tsks = new ArrayList<Task>();
		String batch = "";
		Map <String, Integer> tempTsk = new HashMap<String, Integer>();
		int cost = 0;
		for (Stage stg : stgList){
			if (!batch.equals(stg.getBatch())){
				cost = 0;
				batch = stg.getBatch();
			}
			cost = cost + (int) stg.getNeedTime();
			tempTsk.put(stg.getBatch(), cost);
		}
		for (Map.Entry entry : tempTsk.entrySet()){
			Task tsk = new Task();
			tsk.setName((String) entry.getKey());
			tsk.setCost((Integer) entry.getValue());
			tsks.add(tsk);
		}
		return tsks;
	}

	public List<String> getDependencies(String orderId, String batch){
		System.out.println("DBOrderProduction - getDependencies");
		Query query = em.createQuery(READ_DEPENDENCIES);
		query.setParameter("batch", batch);
		query.setParameter("orderId", orderId);
		List<String> stages = query.getResultList();
		Iterator<String> itStage = stages.iterator();
		List<String> batchs = new ArrayList<String>();
		while (itStage.hasNext()) {
			String idStage = itStage.next();
			List<Stage> stds = getStageById(idStage);
			for (Stage st: stds){
				String code = st.getBatch();
				batchs.add(code);
				break;
			}
		}
		return batchs;
	}
	@SuppressWarnings("unchecked")
	public OrderProduction getOrder(String orderId) {
		System.out.println("DBOrderProduction - getOrder");
		Query query = em.createQuery(READ_ORDER);
		query.setParameter("orderid", orderId.toString());

		List<OrderProduction> orders = (List<OrderProduction>) query.getResultList();
		if (orders == null || orders.isEmpty()) {
			return null;
		}

		OrderProduction order = orders.get(0);

		Query queryState = em.createQuery(READ_ORDER_STATE);
		queryState.setParameter("orderid", order.getOrderId());
		queryState.setMaxResults(1);
		List<OrderProductionActualState> orderActualState = (List<OrderProductionActualState>) queryState.getResultList();


		if (orderActualState == null || orderActualState.isEmpty()) {
			order.setState("new"); }
		else{
			OrderProductionActualState actualState = orderActualState.get(0);
			order.setState(actualState.getState());
			order.setMode(actualState.getMode());
			order.setStartDate(actualState.getStartDate());
			order.setCompleteDate(actualState.getCompleteDate());
		}

		return order;
	}

	@Transactional
	public OrderProduction register(OrderProduction order) {
		System.out.println("DBOrderProduction - register");
		OrderProduction fOrder = getOrderProduction(order.getOrderId());
		if(fOrder != null){
			BeanUtils.copyProperties(order, em.find(OrderProduction.class, fOrder.getId()), "id");
			em.merge(fOrder);
		}else{
			em.persist(order);
			em.flush();
		}

		OrderProductionActualState actualState = new OrderProductionActualState();
		BeanUtils.copyProperties(order, actualState);
		em.persist(actualState);
		em.flush();

		return order;
	}

	//RESGROUP

	@Transactional
	public int deleteResGroupLoad(String id) {
		System.out.println("DBOrderProduction - deleteResGroupLoad");
		Query query = em.createQuery(DELETE_CAPACITY_BY_STAGE);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	@Transactional
	public void setReplanStage(String idStage, Date notEarlier) {
		System.out.println("DBOrderProduction - setReplanStage");
		Query query = em.createQuery(UPDATE_STAGE_NOT_EARLIER);
		query.setParameter("idStage", idStage);
		query.setParameter("notEarlier", notEarlier);
		query.executeUpdate();
	}

	@Transactional
	public void updateOrderStatus(OrderProduction order) {
		OrderProductionActualState actualState = new OrderProductionActualState();
		BeanUtils.copyProperties(order, actualState);
		em.persist(actualState);
		em.flush();
	}

	@Transactional
	public void updateStageCritical(String idStage, long isCritical) {
		System.out.println("DBOrderProduction - updateStageCritical");
		Query query = em.createQuery(UPDATE_STAGE_CRITICAL);
		query.setParameter("idStage", idStage);
		query.setParameter("isCritical", isCritical);
		query.executeUpdate();
	}
}


