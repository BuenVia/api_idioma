package com.language.vocab.controller;

import com.language.vocab.model.Language;
import com.language.vocab.model.Vocab;
import com.language.vocab.model.VocabSummary;
import com.language.vocab.model.CreateVocab;
import com.language.vocab.service.VocabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{language}/vocab")
public class VocabController {

    @Autowired
    VocabService vocabService;

    @GetMapping("/getall")
    public List<Vocab> getVocabAll(@PathVariable Language language) {
        return vocabService.getAllVocab(language);
    }

    @GetMapping("/get/{id}")
    public VocabSummary getVocab(@PathVariable Language language, @PathVariable Long id) {
        return vocabService.getVocab(language, id);
    }

    @PostMapping("/create")
    public Vocab createVocab(@PathVariable Language language, @RequestBody CreateVocab request) {
        Vocab vocab = new Vocab();

        vocab.setTerm(request.getTerm());
        vocab.setTranslation(request.getTranslation());

        return vocabService.createVocab(
                language,
                vocab,
                request.getSubjectId()
        );
    }

    @PutMapping("/update/{id}")
    public Vocab updateVocab(
            @PathVariable Language language, @PathVariable Long id, @RequestBody VocabSummary vocab) {
        return vocabService.updateVocab(language, id, vocab);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVocab(@PathVariable Language language, @PathVariable Long id) {
        return vocabService.deleteVocabById(language, id);
    }


}
