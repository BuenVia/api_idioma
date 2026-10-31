package com.language.vocab.service;


import com.language.category.entity.Category;
import com.language.common.exception.ResourceNotFoundException;
import com.language.common.enums.Language;
import com.language.subject.repository.SubjectRepository;
import com.language.subject.service.SubjectService;
import com.language.subject.entity.Subject;
import com.language.vocab.entity.Vocab;
import com.language.vocab.dto.UpdateVocabRequest;
import com.language.vocab.dto.VocabCategoryGroupResponse;
import com.language.vocab.dto.VocabResponse;
import com.language.vocab.repository.VocabRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * All vocab for the language, grouped by category and then subject.
     * Categories and subjects are sorted by name (case-insensitive), with the
     * uncategorised group last; vocab within a subject is in id order.
     * Subjects with no vocab, and categories with no such subjects, are not included.
     */
    public List<VocabCategoryGroupResponse> getAllVocabGrouped(Language language) {
        Comparator<String> byName = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
        Comparator<Vocab> order = Comparator
                .comparing((Vocab v) -> v.getSubject().getCategory(),
                        Comparator.nullsLast(Comparator.comparing(Category::getCategoryName, byName)
                                .thenComparing(Category::getId)))
                .thenComparing(v -> v.getSubject().getSubjectName(), byName)
                .thenComparing(v -> v.getSubject().getId())
                .thenComparing(Vocab::getId);

        List<Vocab> vocabs = new ArrayList<>(vocabRepository.findWithSubjectBySubjectLanguage(language));
        vocabs.sort(order);

        // Insertion-ordered maps keep the sort order. Keys may be null (uncategorised),
        // which LinkedHashMap allows but Collectors.groupingBy does not.
        Map<Category, Map<Subject, List<Vocab>>> grouped = new LinkedHashMap<>();
        for (Vocab vocab : vocabs) {
            Subject subject = vocab.getSubject();
            grouped.computeIfAbsent(subject.getCategory(), c -> new LinkedHashMap<>())
                    .computeIfAbsent(subject, s -> new ArrayList<>())
                    .add(vocab);
        }

        List<VocabCategoryGroupResponse> response = new ArrayList<>();
        grouped.forEach((category, subjects) -> {
            List<VocabCategoryGroupResponse.SubjectGroup> subjectGroups = new ArrayList<>();
            subjects.forEach((subject, subjectVocabs) -> subjectGroups.add(
                    new VocabCategoryGroupResponse.SubjectGroup(
                            subject.getId(),
                            subject.getSubjectName(),
                            subject.getLevel(),
                            subjectVocabs.stream()
                                    .map(v -> new VocabCategoryGroupResponse.VocabItem(
                                            v.getId(), v.getTerm(), v.getTranslation()))
                                    .toList())));
            response.add(new VocabCategoryGroupResponse(
                    category == null ? null : category.getId(),
                    category == null ? null : category.getCategoryName(),
                    subjectGroups));
        });
        return response;
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
        vocabResponse.setLevel(vocabResult.getSubject().getLevel());

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
