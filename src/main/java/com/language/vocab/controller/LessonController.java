package com.language.vocab.controller;

import com.language.vocab.model.CreateLesson;
import com.language.vocab.model.Lesson;
import com.language.vocab.model.LessonSummary;
import com.language.vocab.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/get/{id}")
    public LessonSummary getLesson(@PathVariable Long id) {
        return lessonService.getLessonById(id);
    }

    @GetMapping("/getbysubject/{subjectId}")
    public Lesson getLessonBySubject(@PathVariable Long subjectId) {
        return lessonService.getLessonBySubjectId(subjectId);
    }

    @PostMapping("/create")
    public Lesson createLesson(@RequestBody CreateLesson request) {
        Lesson lesson = new Lesson();

        lesson.setLessonTitle(request.getLessonTitle());
        lesson.setBody(request.getBody());

        return lessonService.createLesson(lesson, request.getSubjectId());
    }

    @PutMapping("/update/{id}")
    public Lesson updateLesson(@PathVariable Long id, @RequestBody LessonSummary lessonSummary) {
        return lessonService.updateLesson(id, lessonSummary);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        return lessonService.deleteLesson(id);
    }

}
