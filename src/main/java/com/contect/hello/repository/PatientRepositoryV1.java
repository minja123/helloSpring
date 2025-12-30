package com.contect.hello.repository;

import com.contect.hello.domain.Patient;
import com.contect.hello.domain.PatientForm;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PatientRepositoryV1 {

    private final Map<Long, Patient> store = new HashMap<>();
    private Long sequence = 0L;

    public List<Patient> list() {
        return new ArrayList<>(store.values());
    }

    public Patient save(PatientForm form) {
        Patient patient = new Patient(form);
        patient.setId(++sequence);
        store.put(sequence, patient);
        return patient;
    }

    public Patient findById(Long id) {
        return store.get(id);
    }

    public Patient update(Long id, PatientForm updateForm) {
        Patient patient = store.get(id);
        patient.setName(updateForm.getName());
        patient.setBirth(updateForm.getBirth());
        patient.setGender(updateForm.getGender());
        patient.setCn(updateForm.getCn());

        return patient;
    }

    public void delete(Long id) {
        store.remove(id);
    }


}
