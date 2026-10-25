package com.language.vocab.model;

public class LessonSummary {

    private Long id;
    private String lessonTitle;
    private String body;
    private Long subjectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getlessonTitle() {
        return lessonTitle;
    }

    public void setlessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
