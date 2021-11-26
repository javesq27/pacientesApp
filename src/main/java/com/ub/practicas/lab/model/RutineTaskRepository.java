package com.ub.practicas.lab.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutineTaskRepository extends JpaRepository<RutineTask, Long>{
    List<RutineTask> findByPatientId(Long id);
}
