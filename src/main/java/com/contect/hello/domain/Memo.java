package com.contect.hello.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    //환자 엔티티와의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) //지연로딩 설정
    @JoinColumn(name = "patient_id") //DB에 생성될 FK 컬럼명
    private Patient patient;

    @CreationTimestamp
    private LocalDateTime createdDate;
}
