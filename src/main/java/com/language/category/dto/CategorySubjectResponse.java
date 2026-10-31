package com.language.category.dto;

public class CategorySubjectResponse {

    private Long id;
    private String subjectName;

    public CategorySubjectResponse(Long id, String subjectName) {
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
