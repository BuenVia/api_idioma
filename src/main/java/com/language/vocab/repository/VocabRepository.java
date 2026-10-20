package com.language.vocab.repository;

import com.language.vocab.model.Subject;
import com.language.vocab.model.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabRepository extends JpaRepository<Vocab, Long> {

    List<Vocab> findBySubject(Subject subject);

}
