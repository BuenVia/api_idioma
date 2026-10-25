package com.language.vocab.model;

import java.util.Date;

public class CreateLesson {

    private String lessonTitle;
    private String body;
    private Long subjectId;
    private Date createdAt = new Date();

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
