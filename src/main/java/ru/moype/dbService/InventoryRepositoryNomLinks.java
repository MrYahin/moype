package ru.moype.dbService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.moype.model.NomLinks;

@Repository
public interface InventoryRepositoryNomLinks extends JpaRepository<NomLinks, Long>{

}
