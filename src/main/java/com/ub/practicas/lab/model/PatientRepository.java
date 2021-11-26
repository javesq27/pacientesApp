package com.ub.practicas.lab.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{
    Optional<Patient> findByUsername(String email);
    List<Patient> findByProId(Long id);
}
