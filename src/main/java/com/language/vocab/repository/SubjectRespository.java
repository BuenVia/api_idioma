package com.language.vocab.repository;

import com.language.vocab.model.Language;
import com.language.vocab.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRespository extends JpaRepository<Subject, Long> {
    List<Subject> findByLanguage(Language language);
    Optional<Subject> findByIdAndLanguage(Long id, Language language);
}
