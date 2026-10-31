package com.language.subject.repository;

import com.language.common.enums.Language;
import com.language.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByLanguage(Language language);
    Optional<Subject> findByIdAndLanguage(Long id, Language language);
}
