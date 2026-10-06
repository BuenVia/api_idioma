package com.language.vocab.service;


import com.language.vocab.exception.ResourceNotFoundException;
import com.language.vocab.model.Subject;
import com.language.vocab.model.Vocab;
import com.language.vocab.repository.SubjectRespository;
import com.language.vocab.repository.VocabRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("vocabService")
public class VocabService {

    private final VocabRepository vocabRepository;
    private final SubjectRespository subjectRespository;

    public VocabService(VocabRepository vocabRepository, SubjectRespository subjectRespository) {
        this.vocabRepository = vocabRepository;
        this.subjectRespository = subjectRespository;
    }

    public List<Vocab> getAllVocab() {
        return vocabRepository.findAll();
    }

    public Vocab getVocab(Long id) {
        return vocabRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject", id));
    }

    public Vocab createVocab(Vocab vocab, Long subjectId) {
        Subject subject = subjectRespository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", subjectId));

        vocab.setSubject(subject);

        return vocabRepository.save(vocab);
    }

    public Vocab updateVocab(Long id, Vocab vocab) {
        Vocab existingSubject = vocabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocab", id));

        existingSubject.setSubject(vocab.getSubject());
        existingSubject.setEsp(vocab.getEsp());
        existingSubject.setEng(vocab.getEng());

        return vocabRepository.save(existingSubject);
    }

    public String deleteVocab(Long id) {
        Vocab findVocab = new Vocab();
        try {
            findVocab = vocabRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vocab not found: " + id));
            vocabRepository.deleteById(id);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "Successfully deleted vocab: " + id + "  " + findVocab.getId();
    }

}
