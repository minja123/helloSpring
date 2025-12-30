package com.contect.hello.domain;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PatientForm {
    private Long id; // ID를 폼에 포함
    @NotBlank(message = "환자 이름은 필수입니다.")
    private String name;
    @NotBlank(message = "생년월일은 필수입니다.")
    private String birth;
    @NotBlank(message = "성별은 필수입니다.")
    private String gender;
    @Size(min = 5, message = "증상은 5글자 이상 작성해주세요.")
    private String cn;
    private MultipartFile imageFile;


    public PatientForm(String name, String birth, String gender, String cn) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.cn = cn;
    }
}
