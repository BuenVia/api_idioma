package com.language.subject.service;

import com.language.common.enums.Language;
import com.language.common.exception.ResourceNotFoundException;
import com.language.lesson.entity.Lesson;
import com.language.subject.dto.CreateSubjectRequest;
import com.language.subject.dto.SubjectResponse;
import com.language.subject.dto.SubjectSummaryResponse;
import com.language.subject.dto.UpdateSubjectRequest;
import com.language.subject.entity.Subject;
import com.language.lesson.repository.LessonRepository;
import com.language.subject.repository.SubjectRepository;
import com.language.vocab.repository.VocabRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("subjectService")
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final VocabRepository vocabRepository;
    private final LessonRepository lessonRepository;

    public SubjectService(SubjectRepository subjectRepository, VocabRepository vocabRepository, LessonRepository lessonRepository) {
        this.subjectRepository = subjectRepository;
        this.vocabRepository = vocabRepository;
        this.lessonRepository = lessonRepository;
    }

    public List<SubjectSummaryResponse> getSubjectNames(Language language) {
        return subjectRepository.findByLanguage(language).
                stream()
                .map(subject -> new SubjectSummaryResponse(
                        subject.getId(),
                        subject.getSubjectName()
                ))
                .toList();
    }

    public List<Subject> getAllSubjects(Language language) {
        return subjectRepository.findByLanguage(language);
    }

    public Subject getSubject(Language language, Long id) {
        return subjectRepository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    public SubjectResponse createSubject(Language language, CreateSubjectRequest request) {

        Subject subject = new Subject();
        subject.setSubjectName(request.getSubjectName());
        subject.setLanguage(language);

        Subject savedSubject = subjectRepository.save(subject);

        SubjectResponse response = new SubjectResponse();
        response.setSubjectId(savedSubject.getId());
        response.setSubjectName(savedSubject.getSubjectName());

        return response;
    }

    public Subject updateSubject(Language language, Long id, UpdateSubjectRequest updateSubjectRequest) {
        Subject existingSubject = subjectRepository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        existingSubject.setSubjectName(updateSubjectRequest.getSubjectName());

        return subjectRepository.save(existingSubject);
    }

    @Transactional
    public ResponseEntity<Void> deleteSubject(Language language, Long id) {
        Subject findSubject = subjectRepository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        // First delete all associated vocab
        vocabRepository.deleteAll(vocabRepository.findBySubject(findSubject));

        // Then delete the associated lesson
        if (findSubject.getLesson() != null) {
            Lesson findLesson = lessonRepository.findByIdAndSubjectLanguage(findSubject.getLesson().getId(), language)
                    .orElseThrow(() -> new ResourceNotFoundException("lesson", id));
            Subject subject = findLesson.getSubject();
            if (subject != null) {
                subject.setLesson(null);
            }
            lessonRepository.delete(findLesson);
        }
        // Then delete the subject
        subjectRepository.delete(findSubject);
        return ResponseEntity.noContent().build();
    }
}
