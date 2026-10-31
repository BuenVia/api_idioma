package com.language.vocab.service;


import com.language.common.exception.ResourceNotFoundException;
import com.language.common.enums.Language;
import com.language.subject.repository.SubjectRepository;
import com.language.subject.service.SubjectService;
import com.language.subject.entity.Subject;
import com.language.vocab.entity.Vocab;
import com.language.vocab.dto.UpdateVocabRequest;
import com.language.vocab.dto.VocabResponse;
import com.language.vocab.repository.VocabRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("vocabService")
public class VocabService {

    private final VocabRepository vocabRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectService subjectService;

    public VocabService(VocabRepository vocabRepository, SubjectRepository subjectRepository, SubjectService subjectService) {
        this.vocabRepository = vocabRepository;
        this.subjectRepository = subjectRepository;
        this.subjectService = subjectService;
    }

    public List<Vocab> getAllVocab(Language language) {
        return vocabRepository.findBySubjectLanguage(language);
    }

    public VocabResponse getVocab(Language language, Long id) {
         Vocab vocabResult = vocabRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject", id));

        VocabResponse vocabResponse = new VocabResponse();
        vocabResponse.setId(vocabResult.getId());
        vocabResponse.setTerm(vocabResult.getTerm());
        vocabResponse.setTranslation(vocabResult.getTranslation());
        vocabResponse.setSubjectId(vocabResult.getSubject().getId());

         return vocabResponse;

    }

    public List<Vocab> getAllVocabBySubject(Language language, Subject subject) {
        return vocabRepository.findBySubject(subject);
    }

    public Vocab createVocab(Language language, Vocab vocab, Long subjectId) {
        Subject subject = subjectRepository.findByIdAndLanguage(subjectId, language)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", subjectId));

        vocab.setSubject(subject);

        return vocabRepository.save(vocab);
    }

    public Vocab updateVocab(Language language, Long id, UpdateVocabRequest updateVocabRequest) {
        Vocab existingVocab = vocabRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", id));

        // Get the Subject
//        Subject subject = subjectRespository.findById(vocab.getSubjectId());

        Subject subject = subjectService.getSubject(language, updateVocabRequest.getSubjectId());
        existingVocab.setSubject(subject);
        existingVocab.setTerm(updateVocabRequest.getTerm());
        existingVocab.setTranslation(updateVocabRequest.getTranslation());

        return vocabRepository.save(existingVocab);
    }

    public ResponseEntity<Void> deleteVocabById(Language language, Long id) {
        Vocab findVocab = vocabRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", id));

        vocabRepository.delete(findVocab);
        return ResponseEntity.noContent().build();
    }

}
