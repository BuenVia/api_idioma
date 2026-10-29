package com.language.vocab.repository;

import com.language.vocab.model.Language;
import com.language.vocab.model.Lesson;
import com.language.vocab.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Lesson findBySubject(Subject subject);
    Optional<Lesson> findByIdAndSubjectLanguage(Long id, Language language);

}
