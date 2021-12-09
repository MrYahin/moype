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
	
	private static final String READ_STAGE = "Select c From Stage c where c.orderId = :orderid";	
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
		Query query = em.createQuery(READ_STAGE);
		query.setParameter("orderid", idOrder);
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
		
//		@Transactional
//		public Category register(Category category) {
//			if(category.getIdCategory() != null){
//				em.find(Category.class, category.getIdCategory());
//				em.merge(category);
//			}else{
//				em.persist(category);
//				em.flush();
//			}
//			return category;
//		}
}
