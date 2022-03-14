package ru.moype.dbService;

import org.hibernate.Criteria;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.moype.model.Division;
import ru.moype.model.ResGroup;
import ru.moype.model.RowCalendar;
import ru.moype.model.RowCapacityResgroup;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class DBResources {

		@PersistenceContext
		private EntityManager em;
		
		private static final String READ = "Select c From Division c where c.idBase =:idBase";
		private static final String READ_RESGROUP = "Select c From ResGroup c";
		private static final String READ_RESGROUP_BY_DIVISION = "Select c From ResGroup c where c.division = :division";

		private static final String DELETE = "UPDATE Category c set c.statusCategory = 0 where c.idCategory IN (:ids) ";
		private static final String READ_RESGROUP_BY_DATE = "Select c.division as division, c.idBase as idBase, c.idResGroup as idResGroup, sum(c.available) as available, c.date as date, c.name as name  From RowCapacityResgroup c Where c.date = :date and c.idResGroup = :idResgroup group by c.idBase, c.division, c.date, c.name, c.idResGroup";

	@SuppressWarnings("unchecked")
		public List<Division> getAll(String idBase) {
			Query query = em.createQuery(READ);
			query.setParameter("idBase", idBase);
			return query.getResultList();
		}

		public List<ResGroup> getResGroups() {
			Query query = em.createQuery(READ_RESGROUP);
			//query.setParameter("division", division);
			return query.getResultList();
		}

		public List<RowCalendar> getCalendar() {
			Query query = em.createQuery("Select c From RowCalendar c where c.typeOfDay <> 1");
			//query.setParameter("division", division);
			return query.getResultList();
		}

		public List<ResGroup> getResGroupsByDivision(String division) {
			Query query = em.createQuery(READ_RESGROUP_BY_DIVISION);
			query.setParameter("division", division);
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
		public RowCalendar registerRowCalendar(RowCalendar rowCalendar) {
			em.persist(rowCalendar);
			em.flush();
			return rowCalendar;
		}

		@Transactional
		public RowCapacityResgroup updateRowCapacityResGroup(RowCapacityResgroup resGroup) throws ParseException {
			RowCapacityResgroup fRowResGroup = getResgroupByDate(resGroup.getIdResGroup(), resGroup.getDate());
				if (fRowResGroup.getId() != null) {
					BeanUtils.copyProperties(resGroup, em.find(RowCapacityResgroup.class, fRowResGroup.getId()), "id");
					em.merge(fRowResGroup);
				} else {
					em.persist(resGroup);
					em.flush();
				}
			return resGroup;
		}

		@Transactional
		public RowCapacityResgroup registerRowCapacityResGroup(RowCapacityResgroup resGroup){
			em.persist(resGroup);
			em.flush();
			return resGroup;
		}

		@Transactional
		public ResGroup registerResGroup(ResGroup resGroup) {
			if(resGroup.getId() != null){
				em.find(ResGroup.class, resGroup.getId());
				em.merge(resGroup);
			}else{
				em.persist(resGroup);
				em.flush();
			}
			return resGroup;
		}

		@Transactional
		public RowCapacityResgroup getResgroupByDate(String idResGroup, Date date) throws ParseException {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			Query query = em.createQuery(READ_RESGROUP_BY_DATE);
			query.setParameter("date", date);
			query.setParameter("idResgroup", idResGroup);

			RowCapacityResgroup newRSGRP= new RowCapacityResgroup();
			List<Object[]> result = query.getResultList();
			for(Object[] ob: result) {
				newRSGRP.setDivision(String.valueOf(ob[0]));
				newRSGRP.setIdBase(String.valueOf(ob[1]));
				newRSGRP.setIdResGroup(String.valueOf(ob[2]));
				newRSGRP.setAvailable(Long.valueOf(String.valueOf( ob[3])));
				newRSGRP.setDate(formatter.parse(String.valueOf(ob[4])));
				newRSGRP.setName(String.valueOf(ob[5]));
			}
			return newRSGRP;
		}

	@Transactional
	public RowCapacityResgroup getAvailableResgroupByDate(String idResGroup, Date date) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Query query = em.createQuery("Select c  From RowCapacityResgroup c Where c.date = :date and c.idResGroup = :idResgroup and c.idStage is null");
		query.setParameter("date", date);
		query.setParameter("idResgroup", idResGroup);

		RowCapacityResgroup newRSGRP= new RowCapacityResgroup();
		List<RowCapacityResgroup> result = query.getResultList();
		for(RowCapacityResgroup ob: result) {
			return ob;
		}
		return newRSGRP;
	}

	@Transactional
	public List<RowCapacityResgroup> getAllAvailableResgroup() throws ParseException {

		Query query = em.createQuery("Select c  From RowCapacityResgroup c Where c.idStage is null order by c.date");

		return query.getResultList();
	}

	@Transactional
	public List<RowCapacityResgroup> getAllCapacity() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		List<RowCapacityResgroup> listResource = new ArrayList<>();

		//Query query = em.createQuery("Select c.division as division, c.idBase as idBase, c.idResGroup as idResGroup, sum(c.available) as available, c.date as date, c.name as name  From RowCapacityResgroup c where c.date > '2022-02-01' and c.date > '2022-02-15' group by c.idBase, c.division, c.date, c.name, c.idResGroup order by c.date");
		Query query = em.createQuery("Select c.division as division, c.idBase as idBase, c.idResGroup as idResGroup, sum(c.available) as available, c.date as date, c.name as name  From RowCapacityResgroup c group by c.idBase, c.division, c.date, c.name, c.idResGroup order by c.date");

		List<Object[]> result = query.getResultList();
		for(Object[] ob: result) {
			RowCapacityResgroup newRSGRP= new RowCapacityResgroup();
			newRSGRP.setDivision(String.valueOf(ob[0]));
			newRSGRP.setIdBase(String.valueOf(ob[1]));
			newRSGRP.setIdResGroup(String.valueOf(ob[2]));
			newRSGRP.setAvailable(Long.valueOf(String.valueOf( ob[3])));
			newRSGRP.setDate(formatter.parse(String.valueOf(ob[4])));
			newRSGRP.setName(String.valueOf(ob[5]));
			listResource.add(newRSGRP);
		}
		return listResource;
	}
}
