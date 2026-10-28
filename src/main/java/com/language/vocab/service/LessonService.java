package com.language.vocab.service;

import com.language.vocab.exception.ResourceNotFoundException;
import com.language.vocab.model.Language;
import com.language.vocab.model.Lesson;
import com.language.vocab.model.LessonSummary;
import com.language.vocab.model.Subject;
import com.language.vocab.repository.LessonRepository;
import com.language.vocab.repository.SubjectRespository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SubjectRespository subjectRespository;

    public LessonService(LessonRepository lessonRepository, SubjectRespository subjectRespository) {
        this.lessonRepository = lessonRepository;
        this.subjectRespository = subjectRespository;
    }

    public LessonSummary getLessonById(Language language, Long id) {
        Lesson findLesson = lessonRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("lesson", id));

        LessonSummary lessonSummary = new LessonSummary();
        lessonSummary.setId(findLesson.getId());
        lessonSummary.setLessonTitle(findLesson.getLessonTitle());
        lessonSummary.setBody(findLesson.getBody());
        lessonSummary.setSubjectId(findLesson.getSubject().getId());
        return lessonSummary;
    }

    public Lesson getLessonBySubjectId(Language language, Long subjectId) {
        Subject subject = subjectRespository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

        return lessonRepository.findBySubject(subject);
    }

    public Lesson createLesson(Language language, Lesson lesson, Long subjectId) {
        Subject subject = subjectRespository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

        lesson.setSubject(subject);

        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(Language language, Long id, LessonSummary lessonSummary) {
        Lesson existingLesson = lessonRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("lesson", id));

        Subject subject = subjectRespository.findById(lessonSummary.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("lesson", id));

        existingLesson.setSubject(subject);
        existingLesson.setLessonTitle(lessonSummary.getLessonTitle());
        existingLesson.setBody(lessonSummary.getBody());

        return lessonRepository.save(existingLesson);
    }

    public ResponseEntity<Void> deleteLesson(Language language, Long id) {
        Lesson findLesson = lessonRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("lesson", id));

        Subject subject = findLesson.getSubject();
        if (subject != null) {
            subject.setLesson(null);
        }

        lessonRepository.delete(findLesson);
        return ResponseEntity.noContent().build();
    }

}
