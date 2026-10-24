package com.language.vocab.repository;

import com.language.vocab.model.Note;
import com.language.vocab.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Note findBySubject(Subject subject);

}
