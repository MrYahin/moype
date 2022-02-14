package ru.moype.dbService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import jade.wrapper.AgentContainer;
import ru.moype.model.OrderProduction;
import ru.moype.model.Stage;

@Repository
public class DBOrderProduction{

	@PersistenceContext
	private EntityManager em;
	
	private static final String READ_ORDERS = "Select c From OrderProduction c";
	private static final String READ_ORDER = "Select c From OrderProduction c where c.orderId = :orderid";
	private static final String READ_ORDER_STATE = "Select c From OrderProduction c where c.state = :state";
	
	private static final String READ_STAGES = "Select c From Stage c where c.orderId = :orderid";
	private static final String READ_STAGE_BY_ID = "Select c From Stage c where c.idStage = :stageid";
	private static final String READ_STAGE_TO_PLAN_ASAP = "Select c From Stage c where c.orderId = :orderid and c.state = 'new' order by c.codeNom, c.number";
	private static final String READ_STAGE_TO_PLAN_JIT = "Select c From Stage c where c.orderId = :orderid and c.state = 'new' order by c.codeNom, c.number DESC";
	private static final String READ_STAGE = "Select c From Stage c where c.orderId = :orderid and c.number = :number and c.codeNom = :codeNom";
	private static final String UPDATE_ORDER_STATUS = "Update OrderProduction c Set c.state='start' where c.orderId = :orderid";
	
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
		
//		@Transactional
//		public int delete(Long[] ids) {
//			List<Long> longlist = new ArrayList<Long>();
//			for (Long value : ids) {
//				longlist.add(value);
//			}
			
//			Query query = em.createQuery(DELETE);
//			query.setParameter("ids", Arrays.asList(ids));
//			return query.executeUpdate();
//		}
		
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


