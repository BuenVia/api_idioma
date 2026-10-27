package com.language.vocab.model;

import java.util.Date;

public class ReadLesson {

    private String lessonTitle;
    private String body;
    private Date createdAt;
    private Long lessonId;

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

    public Long getlessonId() {
        return lessonId;
    }

    public void setlessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}
