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
    public Lesson getlesson(@PathVariable Long id) {
        return lessonService.getlessonById(id);
    }

    @GetMapping("/getbysubject/{subjectId}")
    public Lesson getlessonBySubject(@PathVariable Long subjectId) {
        return lessonService.getlessonBySubjectId(subjectId);
    }

    @PostMapping("/create")
    public Lesson createlesson(@RequestBody CreateLesson request) {
        Lesson lesson = new Lesson();

        lesson.setLessonTitle(request.getlessonTitle());
        lesson.setBody(request.getBody());

        return lessonService.createlesson(lesson, request.getSubjectId());
    }

    @PutMapping("/update/{id}")
    public Lesson updatelesson(@PathVariable Long id, @RequestBody LessonSummary lessonSummary) {
        return lessonService.updatelesson(id, lessonSummary);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletelesson(@PathVariable Long id) {
        return lessonService.deletelesson(id);
    }

}
