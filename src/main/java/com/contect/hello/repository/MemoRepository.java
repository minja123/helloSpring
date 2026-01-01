package com.contect.hello.repository;

import com.contect.hello.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo,Long> {

    List<Memo> findByPatientId(Long id);
}
