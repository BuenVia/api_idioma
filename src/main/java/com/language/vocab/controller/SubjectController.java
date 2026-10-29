package com.language.vocab.controller;

import com.language.vocab.model.*;
import com.language.vocab.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{language}/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/getsubjectnames")
    public List<SubjectSummary> getSubjectNames(@PathVariable Language language) {
        return subjectService.getSubjectNames(language);
    }

    @GetMapping("/getall")
    public List<Subject> getSubjects(@PathVariable Language language) {
        return subjectService.getAllSubjects(language);
    }

    @GetMapping("/get/{id}")
    public Subject getSubject(@PathVariable Language language, @PathVariable Long id) {
        Subject subject = subjectService.getSubject(language, id);
        return subject;
    }

    @PostMapping("/create")
    public ReadSubject createSubject(@PathVariable Language language, @RequestBody CreateSubject createSubject) {
        return subjectService.createSubject(language, createSubject);
    }

    @PutMapping("/update/{id}")
    public Subject updateSubject(
            @PathVariable Language language, @PathVariable Long id, @RequestBody SubjectSummary subjectSummary) {
        return subjectService.updateSubject(language, id, subjectSummary);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Language language, @PathVariable Long id) {
        return subjectService.deleteSubject(language, id);
    }

}
