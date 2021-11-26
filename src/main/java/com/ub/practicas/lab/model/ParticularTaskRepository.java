package com.ub.practicas.lab.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticularTaskRepository extends JpaRepository<ParticularTask, Long>{
    List<ParticularTask> findByPatientIdEquals(Long Id);
    Long deleteByCheckedAndPatientId(boolean cumplimiento, Long id);
    List<ParticularTask> findByPatientIdOrderByDateDayAscTaskTimeAsc(Long Id);
}
