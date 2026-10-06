package com.language.vocab.service;

import com.language.vocab.exception.ResourceNotFoundException;
import com.language.vocab.model.Subject;
import com.language.vocab.repository.SubjectRespository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("subjectService")
public class SubjectService {

    private final SubjectRespository subjectRespository;

    public SubjectService(SubjectRespository subjectRespository) {
        this.subjectRespository = subjectRespository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRespository.findAll();
    }

    public Subject getSubject(Long id) {
        return subjectRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    public Subject createSubject(Subject subject) {
        return subjectRespository.save(subject);
    }

    public Subject updateSubject(Long id, Subject subject) {
        Subject existingSubject = subjectRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        existingSubject.setSubjectName(subject.getSubjectName());

        return subjectRespository.save(existingSubject);
    }

    public String deleteSubject(Long id) {
        Subject findSubject = new Subject();
        try {
            findSubject = subjectRespository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Subject not found: " + id));
            subjectRespository.deleteById(id);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "Successfully deleted subject: " + id + " " + findSubject.getSubjectName();
    }
}
