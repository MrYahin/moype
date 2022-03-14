package ru.moype.dbService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.moype.model.*;

@Repository
public class DBStage {

		@PersistenceContext
		private EntityManager em;
		
	    @Autowired
	    InventoryRepositoryStage repository;		
	    @Autowired
	    InventoryRepositoryNomLinks repositoryNomLinks;
	    
		private static final String READ = "Select c From Stage c where c.codeNom = :codeNom order by c.number";
		private static final String DELETE = "UPDATE Category c set c.statusCategory = 0 where c.idCategory IN (:ids) ";
		private static final String READINPUTS = "Select c From NomLinks c where c.stageId = :stageId";
		private static final String READOUTPUTS = "Select c From NomLinks c where c.stageIdInput = :stageId";

		public DBStage() {
	    }			
		
		@SuppressWarnings("unchecked")
		public List<Stage> getAll(String codeNom) {
			Query query = em.createQuery(READ);
			query.setParameter("codeNom", codeNom);
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
		public Stage register(Stage stage) {
			Stage fStage = getStageById(stage.getIdStage());
			if(fStage != null){
				BeanUtils.copyProperties(stage, em.find(Stage.class, fStage.getId()), "id");
				em.merge(fStage);
			}else{
				em.persist(stage);
				em.flush();
			}
			return stage;
		}

		@Transactional
		public NomLinks registerNomLink(NomLinks rowNomLink) {
			NomLinks fNomLink = getNomLink(rowNomLink);
			if(fNomLink != null){
				BeanUtils.copyProperties(rowNomLink, em.find(NomLinks.class, fNomLink.getId()), "id");
				em.merge(fNomLink);
			}else{
				em.persist(rowNomLink);
				em.flush();
			}
			return rowNomLink;
		}

		@Transactional
		public RowStageSchemeResgroup registerScheme(RowStageSchemeResgroup rowScheme) {
			RowStageSchemeResgroup fScheme = getScheme(rowScheme);
			if(fScheme != null){
				BeanUtils.copyProperties(rowScheme, em.find(RowStageSchemeResgroup.class, fScheme.getId()), "id");
				em.merge(fScheme);
			}else{
				em.persist(rowScheme);
				em.flush();
			}
			return rowScheme;
		}

		@Transactional
		public List<NomLinks> getInputs(String stageId){
			Query query = em.createQuery(READINPUTS);
			query.setParameter("stageId", stageId);
			return query.getResultList();
		}

		@Transactional
		public List<NomLinks> getOutputs(String stageId){
			Query query = em.createQuery(READOUTPUTS);
			query.setParameter("stageId", stageId);
			return query.getResultList();
		}

		@Transactional
		public List<RowStageSchemeResgroup> getSchemeResGroups(String stageId){
			Query query = em.createQuery("Select c From RowStageSchemeResgroup c where c.idStage = :stageId");
			query.setParameter("stageId", stageId);
			return query.getResultList();
		}

		@Transactional
		public Stage getStageById(String stageId) {
			Query query = em.createQuery("Select c From Stage c where c.idStage = :stageId");
			query.setParameter("stageId", stageId);
			List<Stage> entityList = query.getResultList();
			if (entityList.size() != 0) {
				return entityList.get(0);
			}
			else
				return null;
		}

		@Transactional
		public NomLinks getNomLink(NomLinks nomLink) {
			Query query = em.createQuery("Select c From NomLinks c where c.stageId = :stageId and c.stageIdInput = :stageIdInput and c.codeNom = :codeNom and c.orderId = :orderId and c.idBase = :idBase");
			query.setParameter("stageId", nomLink.getStageId());
			query.setParameter("stageIdInput", nomLink.getStageIdInput());
			query.setParameter("codeNom", nomLink.getCodeNom());
			query.setParameter("orderId", nomLink.getOrderId());
			query.setParameter("idBase", nomLink.getIdBase());
			List<NomLinks> entityList = query.getResultList();
			if (entityList.size() != 0) {
				return entityList.get(0);
			}
			else
				return null;
		}

		@Transactional
		public RowStageSchemeResgroup getScheme(RowStageSchemeResgroup scheme) {
			Query query = em.createQuery("Select c From RowStageSchemeResgroup c where c.idStage = :stageId and c.idResGroup = :idResGroup and c.idBase = :idBase");
			query.setParameter("stageId", scheme.getIdStage());
			query.setParameter("idResGroup", scheme.getIdResGroup());
			query.setParameter("idBase", scheme.getIdBase());
			List<RowStageSchemeResgroup> entityList = query.getResultList();
			if (entityList.size() != 0) {
				return entityList.get(0);
			}
			else
				return null;
		}

		public Stage save(Stage stage) {
			return repository.save(stage);
		}
		
		public NomLinks saveLink(NomLinks link) {
			return repositoryNomLinks.save(link);
		}		
}
