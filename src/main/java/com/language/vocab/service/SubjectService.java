package com.language.vocab.service;

import com.language.vocab.exception.ResourceNotFoundException;
import com.language.vocab.model.*;
import com.language.vocab.repository.SubjectRespository;
import com.language.vocab.repository.VocabRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("subjectService")
public class SubjectService {

    private final SubjectRespository subjectRespository;
    private final VocabRepository vocabRepository;

    public SubjectService(SubjectRespository subjectRespository, VocabRepository vocabRepository) {
        this.subjectRespository = subjectRespository;
        this.vocabRepository = vocabRepository;
    }

    public List<SubjectSummary> getSubjectNames() {
        return subjectRespository.findAll().
                stream()
                .map(subject -> new SubjectSummary(
                        subject.getId(),
                        subject.getSubjectName()
                ))
                .toList();
    }

    public List<Subject> getAllSubjects() {
        return subjectRespository.findAll();
    }

    public Subject getSubject(Long id) {
        return subjectRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    public ReadSubject createSubject(CreateSubject request) {

        Subject subject = new Subject();
        subject.setSubjectName(request.getSubjectName());

        Subject savedSubject = subjectRespository.save(subject);

        ReadSubject response = new ReadSubject();
        response.setSubjectId(savedSubject.getId());
        response.setSubjectName(savedSubject.getSubjectName());

        return response;
    }

    public Subject updateSubject(Long id, Subject subject) {
        Subject existingSubject = subjectRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        existingSubject.setSubjectName(subject.getSubjectName());

        return subjectRespository.save(existingSubject);
    }

    public ResponseEntity<Void> deleteSubject(Long id) {
        Subject findSubject = new Subject();
        try {
            // First delete all associated vocab
            findSubject = subjectRespository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Subject not found: " + id));

            List<Vocab> findVocabs = vocabRepository.findBySubject(findSubject);
            for (Vocab vocab : findVocabs) {
                vocabRepository.deleteById(vocab.getId());
            }

            // Then delete the subject
            subjectRespository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
}
