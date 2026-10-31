package com.language.subject.dto;

public class SubjectSummaryResponse {

    private Long id;
    private String subjectName;

    public SubjectSummaryResponse(Long id, String subjectName) {
        this.id = id;
        this.subjectName = subjectName;
    }

    public Long getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

}
