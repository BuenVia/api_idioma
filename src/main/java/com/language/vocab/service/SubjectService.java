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

    public List<SubjectSummary> getSubjectNames(Language language) {
        return subjectRespository.findByLanguage(language).
                stream()
                .map(subject -> new SubjectSummary(
                        subject.getId(),
                        subject.getSubjectName()
                ))
                .toList();
    }

    public List<Subject> getAllSubjects(Language language) {
        return subjectRespository.findByLanguage(language);
    }

    public Subject getSubject(Language language, Long id) {
        return subjectRespository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    public ReadSubject createSubject(Language language, CreateSubject request) {

        Subject subject = new Subject();
        subject.setSubjectName(request.getSubjectName());
        subject.setLanguage(language);

        Subject savedSubject = subjectRespository.save(subject);

        ReadSubject response = new ReadSubject();
        response.setSubjectId(savedSubject.getId());
        response.setSubjectName(savedSubject.getSubjectName());

        return response;
    }

    public Subject updateSubject(Language language, Long id, Subject subject) {
        Subject existingSubject = subjectRespository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        existingSubject.setSubjectName(subject.getSubjectName());

        return subjectRespository.save(existingSubject);
    }

    public ResponseEntity<Void> deleteSubject(Language language, Long id) {
        Subject findSubject = subjectRespository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        // First delete all associated vocab
        vocabRepository.deleteAll(vocabRepository.findBySubject(findSubject));

        // Then delete the subject
        subjectRespository.delete(findSubject);
        return ResponseEntity.noContent().build();
    }
}
