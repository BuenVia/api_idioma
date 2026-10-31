package com.language.lesson.controller;

import com.language.lesson.dto.CreateLessonRequest;
import com.language.common.enums.Language;
import com.language.lesson.entity.Lesson;
import com.language.lesson.dto.LessonResponse;
import com.language.lesson.dto.UpdateLessonRequest;
import com.language.lesson.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{language}/lesson")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/get/{id}")
    public LessonResponse getLesson(@PathVariable Language language, @PathVariable Long id) {
        return lessonService.getLessonById(language, id);
    }

    @GetMapping("/getbysubject/{subjectId}")
    public Lesson getLessonBySubject(@PathVariable Language language, @PathVariable Long subjectId) {
        return lessonService.getLessonBySubjectId(language, subjectId);
    }

    @PostMapping("/create")
    public Lesson createLesson(@PathVariable Language language, @RequestBody CreateLessonRequest request) {
        Lesson lesson = new Lesson();

        lesson.setLessonTitle(request.getLessonTitle());
        lesson.setBody(request.getBody());

        return lessonService.createLesson(language, lesson, request.getSubjectId());
    }

    @PutMapping("/update/{id}")
    public Lesson updateLesson(
            @PathVariable Language language, @PathVariable Long id,
            @RequestBody UpdateLessonRequest updateLessonRequest) {
        return lessonService.updateLesson(language, id, updateLessonRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Language language, @PathVariable Long id) {
        return lessonService.deleteLesson(language, id);
    }

}
