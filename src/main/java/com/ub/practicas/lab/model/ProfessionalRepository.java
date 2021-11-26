package com.ub.practicas.lab.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    Optional<Professional> findByUsername(String email);    
    List<Professional> findByNameLike(String title);
    Boolean existsByIdNumber(Long idNumber);
}
