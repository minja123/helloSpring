package com.contect.hello.service;

import com.contect.hello.domain.Patient;
import com.contect.hello.domain.PatientForm;
import com.contect.hello.domain.PatientSearchCond;
import com.contect.hello.repository.PatientRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final FileStore fileStore;

    public Long count() {
        return patientRepository.count();
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Page<Patient> getPatientList(PatientSearchCond cond, Pageable pageable) {
        // Specification을 사용하여 동적으로 쿼리 조건을 생성합니다.
//        Specification<Patient> spec = ((root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (StringUtils.hasText(cond.getName())) {
//                predicates.add(cb.like(root.get("name"), "%" + cond.getName() + "%"));
//            }
//            if (StringUtils.hasText(cond.getGender())) {
//                predicates.add(cb.equal(root.get("gender"), cond.getGender()));
//            }
//            if (StringUtils.hasText(cond.getStartDate()) && StringUtils.hasText(cond.getEndDate())) {
//                predicates.add(cb.between(root.get("birth"), cond.getStartDate(), cond.getEndDate()));
//            }
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        });

        return patientRepository.search(cond, pageable);
    }

    public List<Patient> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return patientRepository.findAll();
        }
        return patientRepository.findByNameContaining(name);
    }

    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 환자가 없습니다."));
        return patient;
    }

    @Transactional
    public void update(Long id, PatientForm updateForm, MultipartFile file) throws IOException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 환자가 없습니다."));

        if (file != null && !file.isEmpty()) {
            if (patient.getStoredFileName() != null) {
                fileStore.deleteFile(patient.getStoredFileName());
            }

            String storedFileName = fileStore.storeFile(file);
            patient.updateStoredFileName(storedFileName);
        }

        patient.updateInfo(
                updateForm.getName(),
                updateForm.getBirth(),
                updateForm.getGender(),
                updateForm.getCn());
    }

    public void delete(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 환자가 없습니다."));
        patientRepository.delete(patient);

    }


}
