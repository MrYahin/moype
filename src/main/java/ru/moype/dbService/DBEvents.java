package ru.moype.dbService;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.moype.model.Event;
import ru.moype.model.OrderProduction;
import ru.moype.model.OrderProductionActualState;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class DBEvents {

		@PersistenceContext
		private EntityManager em;
		
		private static final String READ = "Select c From Event c where c.state = 'new'";
		private static final String READ_EVENTS = "Select c From Event c where c.state = 'new' order by c.timeOfEvent";
		private static final String DELETE = "UPDATE Event c set c.state = 'disable' where c.id IN (:ids) ";
		private static final String UPDATE_EVENT_STATUS = "Update Event c Set c.state = :state where c.orderId = :orderid and c.type = :type and c.timeOfEvent = :time";
		
		@SuppressWarnings("unchecked")
		public List<Event> getAll() {
			Query query = em.createQuery(READ);
			return query.getResultList();
		}

		public Event getFirstEvent() {
			Query query = em.createQuery(READ_EVENTS);
			query.setMaxResults(1);
			List<Event> actualEvent = query.getResultList();
			if (actualEvent.size() != 0) {
				return actualEvent.get(0);
			}
			else
				return null;
		}

		@Transactional
		public int delete(Long[] ids) {
			List<Long> longlist = new ArrayList<Long>();
			for (Long value : ids) {
				longlist.add(value);
			}
			
			Query query = em.createQuery(DELETE);
			query.setParameter("ids", Arrays.asList(ids));
			return query.executeUpdate();
		}
		
		@Transactional
		public Event register(Event ev) {
			if(ev.getId() != 0){
				em.find(Event.class, ev.getId());
				em.merge(ev);
			}else{
				em.persist(ev);
				em.flush();
			}
			return ev;
		}

	@Transactional
	public void update(Event ev) {
		System.out.println("DBEvent- updateEvent");
		Query query = em.createQuery(UPDATE_EVENT_STATUS);
		query.setParameter("orderid", ev.getOrderId());
		query.setParameter("type", ev.getType());
		query.setParameter("time", ev.getTime());
		query.setParameter("state", ev.getState());
		query.executeUpdate();
	}
}
