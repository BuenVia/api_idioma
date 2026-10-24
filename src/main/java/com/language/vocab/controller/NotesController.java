package com.language.vocab.controller;

import com.language.vocab.model.CreateNote;
import com.language.vocab.model.Note;
import com.language.vocab.model.NoteSummary;
import com.language.vocab.service.NotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NotesController {

    private final NotesService notesService;

    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping("/get/{id}")
    public Note getNote(@PathVariable Long id) {
        return notesService.getNoteById(id);
    }

    @GetMapping("/getbysubject/{subjectId}")
    public Note getNoteBySubject(@PathVariable Long subjectId) {
        return notesService.getNoteBySubjectId(subjectId);
    }

    @PostMapping("/create")
    public Note createNote(@RequestBody CreateNote request) {
        Note note = new Note();

        note.setNoteTitle(request.getNoteTitle());
        note.setBody(request.getBody());

        return notesService.createNote(note, request.getSubjectId());
    }

    @PutMapping("/update/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody NoteSummary noteSummary) {
        return notesService.updateNote(id, noteSummary);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        return notesService.deleteNote(id);
    }

}
