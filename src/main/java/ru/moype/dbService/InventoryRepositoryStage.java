package ru.moype.dbService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.moype.model.Stage;

@Repository
public interface InventoryRepositoryStage extends JpaRepository<Stage, Long>{

}
