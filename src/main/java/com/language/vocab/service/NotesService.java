package com.language.vocab.service;

import com.language.vocab.exception.ResourceNotFoundException;
import com.language.vocab.model.Note;
import com.language.vocab.model.NoteSummary;
import com.language.vocab.model.Subject;
import com.language.vocab.repository.NoteRepository;
import com.language.vocab.repository.SubjectRespository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class NotesService {

    private final NoteRepository noteRepository;
    private final SubjectRespository subjectRespository;

    public NotesService(NoteRepository noteRepository, SubjectRespository subjectRespository) {
        this.noteRepository = noteRepository;
        this.subjectRespository = subjectRespository;
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
    }

    public Note getNoteBySubjectId(Long subjectId) {
        Subject subject = subjectRespository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

        return noteRepository.findBySubject(subject);
    }

    public Note createNote(Note note, Long subjectId) {
        Subject subject = subjectRespository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

        note.setSubject(subject);

        return noteRepository.save(note);
    }

    public Note updateNote(Long id, NoteSummary noteSummary) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));

        Subject subject = subjectRespository.findById(noteSummary.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));

        existingNote.setSubject(subject);
        existingNote.setNoteTitle(noteSummary.getNoteTitle());
        existingNote.setBody(noteSummary.getBody());

        return noteRepository.save(existingNote);
    }

    public ResponseEntity<Void> deleteNote(Long id) {
        Note findNote = new Note();

        try {
            findNote = noteRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Note", id));
            if(Objects.equals(findNote.getId(), id)) {
                noteRepository.deleteById(id);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

}
