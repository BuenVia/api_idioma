package com.language.subject.dto;

public class UpdateSubjectRequest {

    private String subjectName;

    public UpdateSubjectRequest(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
