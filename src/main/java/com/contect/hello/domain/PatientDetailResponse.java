package com.contect.hello.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class PatientDetailResponse {
    private Long id;
    private String name;
    private String birth;
    private String gender;
    private String cn;
    private List<MemoResponse> memos;

    public static PatientDetailResponse from(Patient patient) {
        List<MemoResponse> memoResponses = patient.getMemos().stream()
                .map(MemoResponse::from)
                .collect(Collectors.toList());

        return new PatientDetailResponse(
                patient.getId(),
                patient.getName(),
                patient.getBirth(),
                patient.getGender(),
                patient.getCn(),
                memoResponses
        );
    }
}
