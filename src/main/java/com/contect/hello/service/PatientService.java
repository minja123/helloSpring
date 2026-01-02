package com.contect.hello.service;

import com.contect.hello.domain.*;
import com.contect.hello.repository.MemoRepository;
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
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final FileStore fileStore;
    private final MemoRepository memoRepository;

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

    public PatientDetailResponse findById(Long id) {
        Patient patient = patientRepository.findByIdWithMemos(id)
                .orElseThrow(() -> new NoSuchElementException("해당 환자가 없습니다."));
        return PatientDetailResponse.from(patient);
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

    @Transactional
    public void delete(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 환자가 없습니다."));
        patientRepository.delete(patient);
    }

    @Transactional
    public void addMemo(Long id, String memo) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 환자가 없습니다"));

        Memo memos = new Memo();
        memos.setContent(memo);
        memos.setPatient(patient);

        memoRepository.save(memos);
    }

    public List<Memo> getMemosByPatientId(Long id) {
        return memoRepository.findByPatientId(id);
    }

    @Transactional
    public Long deleteMemo(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("메모 정보가 존재하지 않습니다."));
        Long patientId = memo.getPatient().getId();
        memoRepository.delete(memo);
        return patientId;
    }

    @Transactional
    public Long updateMemo(Long memoId, String newContent) {
        //1.수정할 메모를 영속성 컨텍스트에 올립니다.
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new NoSuchElementException("메모가 존재하지 않습니다."));

        //2.객체의 값만 바꿉니다.
        memo.setContent(newContent);

        return memo.getPatient().getId();
    }


}
