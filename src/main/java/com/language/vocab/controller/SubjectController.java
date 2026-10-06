package com.language.vocab.controller;

import com.language.vocab.model.Subject;
import com.language.vocab.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/getall")
    public List<Subject> getSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/get/{id}")
    public Subject getSubject(@PathVariable Long id) {
        return subjectService.getSubject(id);
    }

    @PostMapping("/create")
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }

    @PutMapping("/update/{id}")
    public Subject updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        return subjectService.updateSubject(id, subject);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        return subjectService.deleteSubject(id);
    }

}
