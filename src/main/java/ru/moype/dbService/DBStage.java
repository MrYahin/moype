package ru.moype.dbService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.moype.model.Category;
import ru.moype.model.Stage;
import ru.moype.model.NomLinks;

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
		private static final String READINPUTS = "Select c From nomlinks c where c.stageIdOutput = :stageId";
		private static final String READOUTPUTS = "Select c From nomlinks c where c.stageIdInput = :stageId";

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
			if(stage.getId() != 0L){
				em.find(Stage.class, stage.getId());
				em.merge(stage);
			}else{
				em.persist(stage);
				em.flush();
			}
			return stage;
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
		
		public Stage save(Stage stage) {
			return repository.save(stage);
		}
		
		public NomLinks saveLink(NomLinks link) {
			return repositoryNomLinks.save(link);
		}		
}
