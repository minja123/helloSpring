package com.contect.hello.service;

import com.contect.hello.domain.Patient;
import com.contect.hello.domain.PatientForm;
import com.contect.hello.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public Page<Patient> getPatientList(String searchName, int page) {
        // 한 페이제에 10개씩, ID 역순(최신순)으로 정렬하는 요청서 생성
        // 참고: JPA는  페이지 번호가 0부터 시작합니다.
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        if (searchName == null || searchName.isBlank()) {
            return patientRepository.findAll(pageable);
        }

        return patientRepository.findByNameContaining(searchName, pageable);
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
