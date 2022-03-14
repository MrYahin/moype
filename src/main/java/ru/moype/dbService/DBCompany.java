package ru.moype.dbService;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.moype.model.Company;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class DBCompany {

		@PersistenceContext
		private EntityManager em;
		
		private static final String READ = "Select c From Company c where c.state = 'active'";
		private static final String DELETE = "UPDATE Company c set c.state = 'disable' where c.id IN (:ids) ";
		
		@SuppressWarnings("unchecked")
		public List<Company> getAll() {
			Query query = em.createQuery(READ);
			return query.getResultList();
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
		public Company register(Company cmp) {
			if(cmp.getId() != null){
				em.find(Company.class, cmp.getId());
				em.merge(cmp);
			}else{
				em.persist(cmp);
				em.flush();
			}
			return cmp;
		}
}
