package com.language.lesson.service;

import com.language.common.exception.ResourceNotFoundException;
import com.language.common.enums.Language;
import com.language.lesson.entity.Lesson;
import com.language.lesson.dto.LessonResponse;
import com.language.lesson.dto.UpdateLessonRequest;
import com.language.subject.entity.Subject;
import com.language.lesson.repository.LessonRepository;
import com.language.subject.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;

    public LessonService(LessonRepository lessonRepository, SubjectRepository subjectRepository) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
    }

    public LessonResponse getLessonById(Language language, Long id) {
        Lesson findLesson = lessonRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("lesson", id));

        LessonResponse lessonResponse = new LessonResponse();
        lessonResponse.setId(findLesson.getId());
        lessonResponse.setLessonTitle(findLesson.getLessonTitle());
        lessonResponse.setBody(findLesson.getBody());
        lessonResponse.setSubjectId(findLesson.getSubject().getId());
        return lessonResponse;
    }

    public Lesson getLessonBySubjectId(Language language, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

        return lessonRepository.findBySubject(subject);
    }

    public Lesson createLesson(Language language, Lesson lesson, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

        lesson.setSubject(subject);

        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(Language language, Long id, UpdateLessonRequest updateLessonRequest) {
        Lesson existingLesson = lessonRepository.findByIdAndSubjectLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("lesson", id));

        Subject subject = subjectRepository.findById(updateLessonRequest.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("lesson", id));

        existingLesson.setSubject(subject);
        existingLesson.setLessonTitle(updateLessonRequest.getLessonTitle());
        existingLesson.setBody(updateLessonRequest.getBody());

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
