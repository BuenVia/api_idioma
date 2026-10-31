package com.language.vocab.controller;

import com.language.common.enums.Language;
import com.language.vocab.entity.Vocab;
import com.language.vocab.dto.CreateVocabRequest;
import com.language.vocab.dto.UpdateVocabRequest;
import com.language.vocab.dto.VocabResponse;
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
    public VocabResponse getVocab(@PathVariable Language language, @PathVariable Long id) {
        return vocabService.getVocab(language, id);
    }

    @PostMapping("/create")
    public Vocab createVocab(@PathVariable Language language, @RequestBody CreateVocabRequest request) {
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
            @PathVariable Language language, @PathVariable Long id, @RequestBody UpdateVocabRequest updateVocabRequest) {
        return vocabService.updateVocab(language, id, updateVocabRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVocab(@PathVariable Language language, @PathVariable Long id) {
        return vocabService.deleteVocabById(language, id);
    }


}
