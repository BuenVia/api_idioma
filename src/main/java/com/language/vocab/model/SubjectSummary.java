package com.language.vocab.model;

public class SubjectSummary {

    private Long id;
    private String subjectName;

    public SubjectSummary(Long id, String subjectName) {
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
