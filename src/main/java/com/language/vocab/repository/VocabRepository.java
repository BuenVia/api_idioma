package com.language.vocab.repository;

import com.language.common.enums.Language;
import com.language.subject.entity.Subject;
import com.language.vocab.entity.Vocab;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VocabRepository extends JpaRepository<Vocab, Long> {

    List<Vocab> findBySubject(Subject subject);
    List<Vocab> findBySubjectLanguage(Language language);
    Optional<Vocab> findByIdAndSubjectLanguage(Long id, Language language);

    // Loads each vocab's subject, the subject's category and lesson in one query
    // (left joins). Without the graph Hibernate runs extra selects per subject,
    // because Subject.category and Subject.lesson are eager.
    @EntityGraph(attributePaths = {"subject", "subject.category", "subject.lesson"})
    List<Vocab> findWithSubjectBySubjectLanguage(Language language);

}
