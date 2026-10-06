package com.language.vocab.controller;

import com.language.vocab.model.Vocab;
import com.language.vocab.repository.VocabRequest;
import com.language.vocab.service.VocabService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Vocab getVocab(@PathVariable Long id) {
        return vocabService.getVocab(id);
    }

    @PostMapping("/create")
    public Vocab createVocab(@RequestBody VocabRequest request) {
        Vocab vocab = new Vocab();

        vocab.setEsp(request.getEsp());
        vocab.setEng(request.getEng());

        return vocabService.createVocab(
                vocab,
                request.getSubjectId()
        );
    }

    @PutMapping("/update/{id}")
    public Vocab updateVocab(@PathVariable Long id, @RequestBody Vocab vocab) {
        return vocabService.updateVocab(id, vocab);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteVocab(@PathVariable Long id) {
        return vocabService.deleteVocab(id);
    }


}
