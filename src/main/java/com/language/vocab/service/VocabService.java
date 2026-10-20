package com.language.vocab.service;


import com.language.vocab.exception.ResourceNotFoundException;
import com.language.vocab.model.Subject;
import com.language.vocab.model.Vocab;
import com.language.vocab.model.VocabSummary;
import com.language.vocab.repository.SubjectRespository;
import com.language.vocab.repository.VocabRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("vocabService")
public class VocabService {

    private final VocabRepository vocabRepository;
    private final SubjectRespository subjectRespository;
    private final SubjectService subjectService;

    public VocabService(VocabRepository vocabRepository, SubjectRespository subjectRespository, SubjectService subjectService) {
        this.vocabRepository = vocabRepository;
        this.subjectRespository = subjectRespository;
        this.subjectService = subjectService;
    }

    public List<Vocab> getAllVocab() {
        return vocabRepository.findAll();
    }

    public VocabSummary getVocab(Long id) {
         Vocab vocabResult = vocabRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject", id));

        VocabSummary vocabSummary = new VocabSummary();
        vocabSummary.setId(vocabResult.getId());
        vocabSummary.setEsp(vocabResult.getEsp());
        vocabSummary.setEng(vocabResult.getEng());
        vocabSummary.setSubjectId(vocabResult.getSubject().getId());

         return vocabSummary;

    }

    public List<Vocab> getAllVocabBySubject(Subject subject) {
        return vocabRepository.findBySubject(subject);
    }

    public Vocab createVocab(Vocab vocab, Long subjectId) {
        Subject subject = subjectRespository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", subjectId));

        vocab.setSubject(subject);

        return vocabRepository.save(vocab);
    }

    public Vocab updateVocab(Long id, VocabSummary vocab) {
        Vocab existingSubject = vocabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", id));

        // Get the Subject
//        Subject subject = subjectRespository.findById(vocab.getSubjectId());

        Subject subject = subjectService.getSubject(vocab.getSubjectId());
        existingSubject.setSubject(subject);
        existingSubject.setEsp(vocab.getEsp());
        existingSubject.setEng(vocab.getEng());

        return vocabRepository.save(existingSubject);
    }

    public ResponseEntity<Void> deleteVocabById(Long id) {
        Vocab findVocab = new Vocab();
        try {
            findVocab = vocabRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vocab not found: " + id));
            if (Objects.equals(findVocab.getId(), id)) {
                vocabRepository.deleteById(id);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

}
