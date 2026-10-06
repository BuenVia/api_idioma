package com.language.vocab.repository;

import com.language.vocab.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRespository extends JpaRepository<Subject, Long> {
}
