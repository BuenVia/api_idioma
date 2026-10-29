package com.language.vocab.service;


import com.language.vocab.exception.ResourceNotFoundException;
import com.language.vocab.model.Language;
import com.language.vocab.model.Subject;
import com.language.vocab.model.Vocab;
import com.language.vocab.model.VocabSummary;
import com.language.vocab.repository.SubjectRespository;
import com.language.vocab.repository.VocabRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Vocab> getAllVocab(Language language) {
        return vocabRepository.findBySubjectLanguage(language);
    }

    public VocabSummary getVocab(Language language, Long id) {
         Vocab vocabResult = vocabRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject", id));

        VocabSummary vocabSummary = new VocabSummary();
        vocabSummary.setId(vocabResult.getId());
        vocabSummary.setTerm(vocabResult.getTerm());
        vocabSummary.setTranslation(vocabResult.getTranslation());
        vocabSummary.setSubjectId(vocabResult.getSubject().getId());

         return vocabSummary;

    }

    public List<Vocab> getAllVocabBySubject(Language language, Subject subject) {
        return vocabRepository.findBySubject(subject);
    }

    public Vocab createVocab(Language language, Vocab vocab, Long subjectId) {
        Subject subject = subjectRespository.findByIdAndLanguage(subjectId, language)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", subjectId));

        vocab.setSubject(subject);

        return vocabRepository.save(vocab);
    }

    public Vocab updateVocab(Language language, Long id, VocabSummary vocab) {
        Vocab existingVocab = vocabRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", id));

        // Get the Subject
//        Subject subject = subjectRespository.findById(vocab.getSubjectId());

        Subject subject = subjectService.getSubject(language, vocab.getSubjectId());
        existingVocab.setSubject(subject);
        existingVocab.setTerm(vocab.getTerm());
        existingVocab.setTranslation(vocab.getTranslation());

        return vocabRepository.save(existingVocab);
    }

    public ResponseEntity<Void> deleteVocabById(Language language, Long id) {
        Vocab findVocab = vocabRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", id));

        vocabRepository.delete(findVocab);
        return ResponseEntity.noContent().build();
    }

}
