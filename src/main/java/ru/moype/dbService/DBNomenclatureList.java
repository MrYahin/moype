package ru.moype.dbService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.moype.model.Nomenclature;

@Repository
public class DBNomenclatureList {

		@PersistenceContext
		private EntityManager em;
		
		private static final String READ = "Select N From Nomenclature N";
		
		@SuppressWarnings("unchecked")
		public List<Nomenclature> getAll() {
			Query query = em.createQuery(READ);
			return query.getResultList();
		}

}
