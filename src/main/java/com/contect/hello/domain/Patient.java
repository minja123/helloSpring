package com.contect.hello.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String birth;
    private String gender;
    private String cn;
    private String storedFileName;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Memo> memos = new ArrayList<>();


    public Patient(String name, String birth, String gender, String cn, String storedFileName) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.cn = cn;
        this.storedFileName = storedFileName;
    }

    public Patient(PatientForm form) {
        this.name = form.getName();
        this.birth = form.getBirth();
        this.gender = form.getGender();
        this.cn = form.getCn();
    }

    /**
     * 환자 정보 수정 메서드
     * 단순히 Setter를 여러 개 호출하는 것보다, 하나의 비즈니스 메서드로 관리하는 것이 깔끔합니다.
     */
    public void updateInfo(String name, String birth, String gender, String cn) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.cn = cn;
    }

    // 파일명만 따로 업데이트하는 메서드
    public void updateStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }
}
