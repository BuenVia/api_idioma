package com.language.vocab.controller;

import com.language.vocab.model.CreateLesson;
import com.language.vocab.model.Language;
import com.language.vocab.model.Lesson;
import com.language.vocab.model.LessonSummary;
import com.language.vocab.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{language}/lesson")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/get/{id}")
    public LessonSummary getLesson(@PathVariable Language language, @PathVariable Long id) {
        return lessonService.getLessonById(language, id);
    }

    @GetMapping("/getbysubject/{subjectId}")
    public Lesson getLessonBySubject(@PathVariable Language language, @PathVariable Long subjectId) {
        return lessonService.getLessonBySubjectId(language, subjectId);
    }

    @PostMapping("/create")
    public Lesson createLesson(@PathVariable Language language, @RequestBody CreateLesson request) {
        Lesson lesson = new Lesson();

        lesson.setLessonTitle(request.getLessonTitle());
        lesson.setBody(request.getBody());

        return lessonService.createLesson(language, lesson, request.getSubjectId());
    }

    @PutMapping("/update/{id}")
    public Lesson updateLesson(
            @PathVariable Language language, @PathVariable Long id,
            @RequestBody LessonSummary lessonSummary) {
        return lessonService.updateLesson(language, id, lessonSummary);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Language language, @PathVariable Long id) {
        return lessonService.deleteLesson(language, id);
    }

}
