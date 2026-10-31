package com.language.lesson.repository;

import com.language.common.enums.Language;
import com.language.lesson.entity.Lesson;
import com.language.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Lesson findBySubject(Subject subject);
    Optional<Lesson> findByIdAndSubjectLanguage(Long id, Language language);

}
