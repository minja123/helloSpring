package com.contect.hello.domain;

import lombok.Data;

@Data
public class PatientSearchCond {
    private String name;
    private String gender;
    private String startDate;
    private String endDate;
}
