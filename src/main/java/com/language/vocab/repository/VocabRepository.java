package com.language.vocab.repository;

import com.language.vocab.model.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabRepository extends JpaRepository<Vocab, Long> {
}
