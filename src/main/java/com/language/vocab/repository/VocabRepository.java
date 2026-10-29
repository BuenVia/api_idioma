package com.language.vocab.repository;

import com.language.vocab.model.Language;
import com.language.vocab.model.Subject;
import com.language.vocab.model.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VocabRepository extends JpaRepository<Vocab, Long> {

    List<Vocab> findBySubject(Subject subject);
    List<Vocab> findBySubjectLanguage(Language language);
    Optional<Vocab> findByIdAndSubjectLanguage(Long id, Language language);

}
