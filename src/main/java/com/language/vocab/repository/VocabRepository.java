package com.language.vocab.repository;

import com.language.common.enums.Language;
import com.language.subject.entity.Subject;
import com.language.vocab.entity.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VocabRepository extends JpaRepository<Vocab, Long> {

    List<Vocab> findBySubject(Subject subject);
    List<Vocab> findBySubjectLanguage(Language language);
    Optional<Vocab> findByIdAndSubjectLanguage(Long id, Language language);

}
