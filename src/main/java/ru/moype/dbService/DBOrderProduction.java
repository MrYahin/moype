package ru.moype.dbService;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import jade.wrapper.AgentContainer;
import ru.moype.model.NomLinks;
import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;
import ru.moype.resources.Arrow;
import ru.moype.resources.Task;

@Repository
public class DBOrderProduction{

	@PersistenceContext
	private EntityManager em;
	
	private static final String READ_ORDERS = "Select c From OrderProduction c";
	private static final String READ_ORDER = "Select c From OrderProduction c where c.orderId = :orderid";
	private static final String READ_ORDER_STATE = "Select c From OrderProduction c where c.state = :state";
	
	private static final String READ_STAGES = "Select c From Stage c where c.orderId = :orderid";
	private static final String READ_STAGE_BY_ID = "Select c From Stage c where c.idStage = :stageid";
	private static final String READ_STAGE_BY_CODENOM = "Select c From Stage c where c.codeNom = :codeNom and c.orderId =:orderid";
	private static final String READ_STAGE_TO_PLAN_ASAP = "Select c From Stage c where c.orderId = :orderid and c.state = 'new' order by c.codeNom, c.number";
	private static final String READ_STAGE_TO_PLAN_JIT = "Select c From Stage c where c.orderId = :orderid and c.state = 'new' order by c.codeNom, c.number DESC";
	private static final String READ_STAGE = "Select c From Stage c where c.orderId = :orderid and c.number = :number and c.codeNom = :codeNom";
	private static final String UPDATE_ORDER_STATUS = "Update OrderProduction c Set c.state='start' where c.orderId = :orderid";
	private static final String UPDATE_STAGE = "Update Stage c Set c.isCritical = :isCritical where c.idStage = :idStage";

	private static final String READ_CODENOME = "Select c From Stage c where c.orderId = :orderid order by c.codeNom";

//	private static final String READ_CODENOME = "Select c.codeNom as name, sum(c.needTime) as cost From Stage c where c.orderId = :orderid group by c.codeNom";
	private static final String READ_DEPENDENCIES = "Select c.stageIdInput From NomLinks c where c.orderId = :orderId and c.codeNom = :codeNom order by c.codeNom";
	//private static final String DELETE = "UPDATE Category c set c.statusCategory = 0 where c.idCategory IN (:ids) ";
	
	//private static final String READ = "Select c From orderproduction c";
		//private static final String DELETE = "UPDATE Category c set c.statusCategory = 0 where c.idCategory IN (:ids) ";
		
//    public List<OrderProduction> getAll() {
//        Session session = sessionFactory.openSession();
//        OrderProductionDAO dao = new OrderProductionDAO(session);
//        return (List<OrderProduction>)dao.getAll();
//    }   
    
	public DBOrderProduction() {
    }	
	
	@SuppressWarnings("unchecked")
	public List<OrderProduction> getAll() {
		Query query = em.createQuery(READ_ORDERS);
		return query.getResultList();
	}    

	@SuppressWarnings("unchecked")
	public List<OrderProduction> getAllState(String state) {
		Query query = em.createQuery(READ_ORDER_STATE);
		query.setParameter("state", state);		
		return query.getResultList();
	}    

	@SuppressWarnings("unchecked")
	public OrderProduction getOrder(String orderId) {
		Query query = em.createQuery(READ_ORDER);
		query.setParameter("orderid", orderId.toString());		
		
		List<OrderProduction> orders = (List<OrderProduction>) query.getResultList();
        if (orders == null || orders.isEmpty()) {
            return null;
        }
       return orders.get(0);
	} 	
	
	@SuppressWarnings("unchecked")
	public List<Stage> readStageList(String idOrder) {
		Query query = em.createQuery(READ_STAGES);
		query.setParameter("orderid", idOrder);
		return query.getResultList();
	}

	public List<Arrow> readArrowsByStage(String stageId) {

		//цепочка этапов
		List<Stage> stages = readStageById(stageId);
		String codeNom = "";
		String orderId = "";
		for (Stage stg : stages) {
			codeNom = stg.getCodeNom();
			orderId = stg.getOrderId();
		}
		List<Stage> stagesByCode = readStagesByCodeNomOrder(codeNom, orderId);
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
		List<NomLinks> comp = readCompByStage(firstId);
		for (NomLinks cm : comp) {
			i++;
			Arrow ar = new Arrow("ar" + i, cm.getStageIdInput(), firstId);
			arrows.add(ar);
		}
		//входимость
		List<NomLinks> entries = readEntryByStage(lastId);
		for (NomLinks cm : entries) {
			i++;
			Arrow ar = new Arrow("ar" + i, lastId, cm.getStageId());
			arrows.add(ar);
		}
		return arrows;
	}

	public List<Stage> readStagesByCodeNom(String codeNom, String idOrder) {
		Query query = em.createQuery(READ_STAGE_BY_CODENOM);
		query.setParameter("orderid", idOrder);
		query.setParameter("codeNom", codeNom);
		return query.getResultList();
	}

	public List<Stage> readStagesByCodeNomOrder(String codeNom, String idOrder) {
		Query query = em.createQuery("Select c From Stage c where c.codeNom = :codeNom and c.orderId =:orderid order by c.number");
		query.setParameter("orderid", idOrder);
		query.setParameter("codeNom", codeNom);
		return query.getResultList();
	}

	public List<NomLinks> readCompByStage(String idStage) {
		Query query = em.createQuery("Select c From NomLinks c where c.stageId = :idStage");
		query.setParameter("idStage", idStage);
		return query.getResultList();
	}

	public List<NomLinks> readEntryByStage(String idStage) {
		Query query = em.createQuery("Select c From NomLinks c where c.stageIdInput = :idStage");
		query.setParameter("idStage", idStage);
		return query.getResultList();
	}

	public List<Task> getCodeNomeList(String idOrder) {
		Query query = em.createQuery(READ_CODENOME);
		query.setParameter("orderid", idOrder);
		List<Stage> stgList = query.getResultList();
		List<Task> tsks = new ArrayList<Task>();
		String codenome = "";
		Map <String, Integer> tempTsk = new HashMap<String, Integer>();
		int cost = 0;
		for (Stage stg : stgList){
			if (!codenome.equals(stg.getCodeNom())){
				cost = 0;
				codenome = stg.getCodeNom();
			}
			cost = cost + (int) stg.getNeedTime();
			tempTsk.put(stg.getCodeNom(), cost);
		}
		for (Map.Entry entry : tempTsk.entrySet()){
			Task tsk = new Task();
			tsk.setName((String) entry.getKey());
			tsk.setCost((Integer) entry.getValue());
			tsks.add(tsk);
		}
		return tsks;
	}

	public List<String> getDependencies(String orderId, String codeNom){
		Query query = em.createQuery(READ_DEPENDENCIES);
		query.setParameter("codeNom", codeNom);
		query.setParameter("orderId", orderId);
		List<String> stages = query.getResultList();
		Iterator<String> itStage = stages.iterator();
		List<String> codeNoms = new ArrayList<String>();
		while (itStage.hasNext()) {
			String idStage = itStage.next();
			//Query query1 = em.createQuery("Select c.codeNom From Stage c where c.orderId = :orderId and c.idStage = :idStage");
			//query1.setParameter("idStage", idStage);
			//query1.setParameter("orderId", orderId);
			List<Stage> stds = readStageById(idStage);
			for (Stage st: stds){
				String code = st.getCodeNom();
				codeNoms.add(code);
				break;
			}
		}
		return codeNoms;
	}

	public List<Stage> readStageById(String stageid) {
		Query query = em.createQuery(READ_STAGE_BY_ID);
		query.setParameter("stageid", stageid);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Stage> readStageToPlanList(String idOrder, String mode) {
		String queryText;
		if (mode.equals("0")) { queryText = READ_STAGE_TO_PLAN_ASAP; }
		else { queryText = READ_STAGE_TO_PLAN_JIT; }
		Query query = em.createQuery(queryText);
		query.setParameter("orderid", idOrder);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Stage> readStage(String idOrder, long number, String codeNom) {
		Query query = em.createQuery(READ_STAGE);
		query.setParameter("orderid", idOrder);
		query.setParameter("number", number);
		query.setParameter("codeNom", codeNom);
		return query.getResultList();
	}

	@Transactional
	public void updateOrder(String orderId) {
		Query query = em.createQuery(UPDATE_ORDER_STATUS);
		query.setParameter("orderid", orderId);
		query.executeUpdate();
	}

	@Transactional
	public void updateStage(String idStage, long isCritical) {
		Query query = em.createQuery(UPDATE_STAGE);
		query.setParameter("idStage", idStage);
		query.setParameter("isCritical", isCritical);
		query.executeUpdate();
	}

	@Transactional
	public int deleteResGroupLoad(String id) {
		Query query = em.createQuery("DELETE FROM RowCapacityResgroup c where c.idStage =:id");
		query.setParameter("id", id);
		return query.executeUpdate();
	}
		
	@Transactional
	public OrderProduction register(OrderProduction order) {
		OrderProduction fOrder = getOrderProduction(order.getOrderId());
		if(fOrder != null){
			BeanUtils.copyProperties(order, em.find(OrderProduction.class, fOrder.getId()), "id");
			em.merge(fOrder);
		}else{
			em.persist(order);
			em.flush();
		}
		return order;
	}

	@Transactional
	public OrderProduction getOrderProduction(String orderId) {
		Query query = em.createQuery("Select c From OrderProduction c where c.orderId = :orderid");
		query.setParameter("orderid", orderId);
		List<OrderProduction> entityList = query.getResultList();
		if (entityList.size() != 0) {
			return entityList.get(0);
		}
		else
		return null;
	}

}


