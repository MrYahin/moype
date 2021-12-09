package ru.moype.dbService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.moype.model.Category;

@Repository
public class DBCategory {

		@PersistenceContext
		private EntityManager em;
		
		private static final String READ = "Select c From Category c where c.statusCategory = 1 order by c.idParentCategory ";
		private static final String DELETE = "UPDATE Category c set c.statusCategory = 0 where c.idCategory IN (:ids) ";
		
		@SuppressWarnings("unchecked")
		public List<Category> getAll() {
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
		public Category register(Category category) {
			if(category.getIdCategory() != null){
				em.find(Category.class, category.getIdCategory());
				em.merge(category);
			}else{
				em.persist(category);
				em.flush();
			}
			return category;
		}
}
