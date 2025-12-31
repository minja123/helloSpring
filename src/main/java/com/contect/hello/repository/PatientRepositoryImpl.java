package com.contect.hello.repository;

import com.contect.hello.domain.Patient;
import com.contect.hello.domain.PatientSearchCond;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.contect.hello.domain.QPatient.patient;

@RequiredArgsConstructor
public class PatientRepositoryImpl implements PatientRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Patient> search(PatientSearchCond cond, Pageable pageable) {
        List<Patient> content = queryFactory
                .selectFrom(patient)
                .where(
                        nameLike(cond.getName()),
                        genderEq(cond.getGender()),
                        birthBetween(cond.getStartDate(), cond.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(patient.count())
                .from(patient)
                .where(
                        nameLike(cond.getName()),
                        genderEq(cond.getGender()),
                        birthBetween(cond.getStartDate(), cond.getEndDate())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // --- 동적 쿼리를 위한 조건 메서드 (재사용 가능) ---

    private BooleanExpression nameLike(String name) {
        return StringUtils.hasText(name) ? patient.name.contains(name) : null;
    }

    private BooleanExpression genderEq(String gender) {
        return StringUtils.hasText(gender) ? patient.gender.eq(gender) : null;
    }

    private BooleanExpression birthBetween(String start, String end) {
        if (!StringUtils.hasText(start) || !StringUtils.hasText(end)) return null;
        return patient.birth.between(start, end);
    }
}
