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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class DBResources {

	@PersistenceContext
	private EntityManager em;
		
	private static final String READ = "Select c From Division c where c.idBase =:idBase order by c.name";
	private static final String READ_RESGROUP = "Select c From ResGroup c order by c.name";
	private static final String READ_RESGROUP_BY_DIVISION = "Select c From ResGroup c where c.division = :division order by c.name";

	private static final String DELETE = "UPDATE Category c set c.statusCategory = 0 where c.idCategory IN (:ids) ";
	private static final String READ_RESGROUP_BY_DATE = "Select c.division as division, c.idBase as idBase, c.idResGroup as idResGroup, sum(c.available) as available, c.name as name  From RowCapacityResgroup c Where c.date >= :date1 and c.date < :date2 and c.idResGroup = :idResgroup group by c.idBase, c.division, c.name, c.idResGroup";
	private static final String READ_GRAFIC_BY_DATE = "Select c From RowCapacityResgroup c Where c.date = :date and c.idResGroup = :idResgroup and c.idStage = 'grafic'";
	private static final String READ_LOAD_BY_DATE = "Select c From RowCapacityResgroup c Where c.date = :date and c.idResGroup = :idResgroup and c.idStage <> 'grafic'";

	private static final String READ_LAST_RESGROUP = "Select c From RowCapacityResgroup c Where c.date >= :date1 and c.date < :date2 and c.idResGroup = :idResgroup order by c.date DESC";
	private static final String READ_FIRST_RESGROUP = "Select c From RowCapacityResgroup c Where c.date >= :date1 and c.date < :date2 and c.idResGroup = :idResgroup order by c.date";

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
			System.out.println("DBResources - registerDivision");
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
			System.out.println("DBResources - registerRowCalendar");
			em.persist(rowCalendar);
			em.flush();
			return rowCalendar;
		}

	@Transactional
	public RowCapacityResgroup updateRowCapacityResGroup(RowCapacityResgroup resGroup) throws ParseException {
			System.out.println("DBResources - updateRowCapacityResGroup");
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
			System.out.println("DBResources - registerRowCapacityResGroup");
			em.persist(resGroup);
			em.flush();
			return resGroup;
		}

	@Transactional
	public ResGroup registerResGroup(ResGroup resGroup) {
			System.out.println("DBResources - registerResGroup");
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
	public List<RowCapacityResgroup> getCapacityResgroupByDate(String idResGroup, Date date, long simultaneous) {

			boolean empty_int = false;
			System.out.println("DBResources - getCapacityResgroupByDate");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			List<RowCapacityResgroup> intervals = new ArrayList<RowCapacityResgroup>();

			Query query = em.createQuery(READ_GRAFIC_BY_DATE);
			query.setParameter("date", date);
			query.setParameter("idResgroup", idResGroup);

			List<RowCapacityResgroup> result = query.getResultList();
			for(RowCapacityResgroup interval: result) {
				Date gr_start = interval.getStart();
				Date gr_finish = interval.getFinish();

				if (simultaneous == 0) {
					//Получить загрузку
					Query queryLoad = em.createQuery(READ_LOAD_BY_DATE);
					queryLoad.setParameter("date", date);
					queryLoad.setParameter("idResgroup", idResGroup);

					List<RowCapacityResgroup> load = queryLoad.getResultList();
					for (RowCapacityResgroup intervalLoad : load) {
						//  |||||||||----------|
						if (gr_start.equals(intervalLoad.getStart()) && gr_finish.after(intervalLoad.getFinish())) {
							gr_start = intervalLoad.getFinish();
						}
						//  |----------|||||||||
						if (gr_start.before(intervalLoad.getStart()) && gr_finish.equals(intervalLoad.getFinish())) {
							gr_finish = intervalLoad.getFinish();
						}
						//  |----------|||||||||------------|
						if (gr_start.before(intervalLoad.getStart()) && gr_finish.after(intervalLoad.getFinish())) {
							//делаю допущение, второй интервал просто копируется и всегда возвращается
							RowCapacityResgroup newRSGRP = new RowCapacityResgroup();
							BeanUtils.copyProperties(interval, newRSGRP);
							newRSGRP.setStart(intervalLoad.getFinish());
							newRSGRP.setFinish(gr_finish);
							//intervals.add(newRSGRP);
							gr_start = intervalLoad.getFinish();//График меняется на конец загрузки
						}
						//Если интервала доступности нету
						if (gr_finish.equals(gr_start)) {
							empty_int = true;
							break;
						}
						else {
							long b = 0;
						}
					}
				}
				if (!empty_int) {
					RowCapacityResgroup newRSGRP = new RowCapacityResgroup();
					BeanUtils.copyProperties(interval, newRSGRP);
					newRSGRP.setStart(gr_start);
					newRSGRP.setFinish(gr_finish);
					intervals.add(newRSGRP);
				}
			}

			return intervals;
		}

	@Transactional
	public RowCapacityResgroup getResgroupByDate(String idResGroup, Date date) throws ParseException {
		System.out.println("DBResources - getResgroupByDate");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Query query = em.createQuery(READ_RESGROUP_BY_DATE);
		query.setParameter("date1", atStartOfDay(date));
		query.setParameter("date2", atEndOfDay(date));
		query.setParameter("idResgroup", idResGroup);

		RowCapacityResgroup newRSGRP= new RowCapacityResgroup();
		List<Object[]> result = query.getResultList();
		for(Object[] ob: result) {
			newRSGRP.setDivision(String.valueOf(ob[0]));
			newRSGRP.setIdBase(String.valueOf(ob[1]));
			newRSGRP.setIdResGroup(String.valueOf(ob[2]));
			newRSGRP.setAvailable(Long.valueOf(String.valueOf( ob[3])));
			newRSGRP.setDate(date);
			newRSGRP.setName(String.valueOf(ob[4]));
		}
		return newRSGRP;
	}

	@Transactional
	public RowCapacityResgroup getResgroupTimeByDate(String idResGroup, Date date, long mode){

		Query query;
		if (mode == 0) {
			query = em.createQuery(READ_LAST_RESGROUP);
			query.setMaxResults(1);
		} else {
			query = em.createQuery(READ_FIRST_RESGROUP);
			query.setMaxResults(1);
		}
		query.setParameter("date1", atStartOfDay(date));
		query.setParameter("date2", atEndOfDay(date));
		query.setParameter("idResgroup", idResGroup);

		RowCapacityResgroup newRSGRP= new RowCapacityResgroup();
		List<RowCapacityResgroup> result = query.getResultList();
		for(RowCapacityResgroup ob: result) {
			return ob;
		}
		return newRSGRP;
	}

	@Transactional
	public RowCapacityResgroup getAvailableResgroupByDate(String idResGroup, Date date) throws ParseException {
		System.out.println("DBResources - getAvailableResgroupByDate");
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
		System.out.println("DBResources - getAllAvailableResgroup");
		Query query = em.createQuery("Select c  From RowCapacityResgroup c Where c.idStage is null order by c.date");

		return query.getResultList();
	}

	@Transactional
	public List<RowCapacityResgroup> getAllCapacity() throws ParseException {
		System.out.println("DBResources - getAllCapacity");
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

	@SuppressWarnings("unchecked")
	public List<Division> getAll(String idBase) {
		System.out.println("DBResources - getAll");
		Query query = em.createQuery(READ);
		query.setParameter("idBase", idBase);
		return query.getResultList();
	}

	public List<ResGroup> getResGroups() {
		System.out.println("DBResources - getResGroups");
		Query query = em.createQuery(READ_RESGROUP);
		//query.setParameter("division", division);
		return query.getResultList();
	}

	public List<RowCalendar> getCalendar() {
		System.out.println("DBResources - getCalendar");
		Query query = em.createQuery("Select c From RowCalendar c where c.typeOfDay <> 1");
		//query.setParameter("division", division);
		return query.getResultList();
	}

	public List<ResGroup> getResGroupsByDivision(String division) {
		System.out.println("DBResources - getResGroupsByDivision");
		Query query = em.createQuery(READ_RESGROUP_BY_DIVISION);
		query.setParameter("division", division);
		return query.getResultList();
	}

	public static Date atStartOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
		return localDateTimeToDate(startOfDay);
	}

	public static Date atEndOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
		return localDateTimeToDate(endOfDay);
	}

	private static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	private static Date localDateTimeToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
}
