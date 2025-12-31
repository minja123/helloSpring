package com.contect.hello.repository;

import com.contect.hello.domain.Patient;
import com.contect.hello.domain.PatientSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientRepositoryCustom {
    Page<Patient> search(PatientSearchCond cond, Pageable pageable);
}
