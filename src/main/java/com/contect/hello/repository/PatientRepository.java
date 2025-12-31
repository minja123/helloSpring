package com.contect.hello.repository;

import com.contect.hello.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, PatientRepositoryCustom {

    List<Patient> findByNameContaining(String name);

    Page<Patient> findByNameContaining(String name, Pageable pageable);
}
