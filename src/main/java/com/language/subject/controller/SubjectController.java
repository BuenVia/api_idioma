package com.language.subject.controller;

import com.language.common.enums.Language;
import com.language.subject.dto.CreateSubjectRequest;
import com.language.subject.dto.SubjectResponse;
import com.language.subject.dto.SubjectSummaryResponse;
import com.language.subject.dto.UpdateSubjectRequest;
import com.language.subject.entity.Subject;
import com.language.subject.service.SubjectService;
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
    public List<SubjectSummaryResponse> getSubjectNames(@PathVariable Language language) {
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
    public SubjectResponse createSubject(@PathVariable Language language, @RequestBody CreateSubjectRequest createSubjectRequest) {
        return subjectService.createSubject(language, createSubjectRequest);
    }

    @PutMapping("/update/{id}")
    public Subject updateSubject(@PathVariable Language language, @PathVariable Long id,
            @RequestBody UpdateSubjectRequest updateSubjectRequest) {
        return subjectService.updateSubject(language, id, updateSubjectRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Language language, @PathVariable Long id) {
        return subjectService.deleteSubject(language, id);
    }

}
