package com.language.vocab.controller;

import com.language.vocab.model.Vocab;
import com.language.vocab.model.VocabSummary;
import com.language.vocab.model.CreateVocab;
import com.language.vocab.service.VocabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vocab")
public class VocabController {

    @Autowired
    VocabService vocabService;

    @GetMapping("/getall")
    public List<Vocab> getVocabAll() {
        return vocabService.getAllVocab();
    }

    @GetMapping("/get/{id}")
    public VocabSummary getVocab(@PathVariable Long id) {
        return vocabService.getVocab(id);
    }

    @PostMapping("/create")
    public Vocab createVocab(@RequestBody CreateVocab request) {
        Vocab vocab = new Vocab();

        vocab.setEsp(request.getEsp());
        vocab.setEng(request.getEng());

        return vocabService.createVocab(
                vocab,
                request.getSubjectId()
        );
    }

    @PutMapping("/update/{id}")
    public Vocab updateVocab(@PathVariable Long id, @RequestBody VocabSummary vocab) {
        return vocabService.updateVocab(id, vocab);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVocab(@PathVariable Long id) {
        return vocabService.deleteVocabById(id);
    }


}
