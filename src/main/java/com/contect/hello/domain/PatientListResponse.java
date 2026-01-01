package com.contect.hello.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientListResponse {
    private Long id;
    private String name;
    private String birth;
    private String gender;
    private String cn;

    public static PatientListResponse from(Patient patient) {
        return new PatientListResponse(
                patient.getId(),
                patient.getName(),
                patient.getBirth(),
                patient.getGender(),
                patient.getCn()
                );
    }

}
