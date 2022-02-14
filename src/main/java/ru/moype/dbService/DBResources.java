package ru.moype.dbService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.moype.model.Division;
import ru.moype.model.RowCapacityResgroup;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
public class DBResources {

		@PersistenceContext
		private EntityManager em;
		
		private static final String READ = "Select c From Division c";
		private static final String DELETE = "UPDATE Category c set c.statusCategory = 0 where c.idCategory IN (:ids) ";
		private static final String READ_RESGROUP_BY_DATE = "Select c From RowCapacityResgroup c Where c.date = :date and c.idResGroup = :idResgroup";

		@SuppressWarnings("unchecked")
		public List<Division> getAll() {
			Query query = em.createQuery(READ);
			return query.getResultList();
		}
		
		//@Transactional
		//public int delete(Long[] ids) {
		//	List<Long> longlist = new ArrayList<Long>();
		//	for (Long value : ids) {
		//		longlist.add(value);
		//	}
			
		//	Query query = em.createQuery(DELETE);
		//	query.setParameter("ids", Arrays.asList(ids));
		//	return query.executeUpdate();
		//}
		
		@Transactional
		public Division registerDivision(Division division) {
			if(division.getId() != null){
				em.find(Division.class, division.getId());
				em.merge(division);
			}else{
				em.persist(division);
				em.flush();
			}
			return division;
		}

		@Transactional
		public RowCapacityResgroup registerResGroup(RowCapacityResgroup resGroup) {
			RowCapacityResgroup fRowResGroup = getResgoupByDate(resGroup.getIdResGroup(), resGroup.getDate());
			if(fRowResGroup != null){
				BeanUtils.copyProperties(resGroup, em.find(RowCapacityResgroup.class, fRowResGroup.getId()), "id");
				em.merge(fRowResGroup);
			}else{
				em.persist(resGroup);
				em.flush();
			}
			return resGroup;
		}

		@Transactional
		public RowCapacityResgroup getResgoupByDate(String idResGroup, Date date) {
			Query query = em.createQuery(READ_RESGROUP_BY_DATE);
			query.setParameter("date", date);
			query.setParameter("idResgroup", idResGroup);
			List<RowCapacityResgroup> entityList = query.getResultList();
			if (entityList.size() != 0) {
				return entityList.get(0);
			}
			else
				return null;
		}
}
