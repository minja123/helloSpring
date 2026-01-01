package com.contect.hello.repository;

import com.contect.hello.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, PatientRepositoryCustom {

    List<Patient> findByNameContaining(String name);

    Page<Patient> findByNameContaining(String name, Pageable pageable);

    @Query("select p from Patient p left join fetch p.memos m where p.id = :id order by m.createdDate desc")
    Optional<Patient> findByIdWithMemos(@Param("id") Long id);
}
