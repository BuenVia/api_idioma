package com.language.category.dto;

import java.util.List;

public class CategoryResponse {

    private Long id;
    private String categoryName;
    private List<CategorySubjectResponse> subjects;

    public CategoryResponse(Long id, String categoryName, List<CategorySubjectResponse> subjects) {
        this.id = id;
        this.categoryName = categoryName;
        this.subjects = subjects;
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<CategorySubjectResponse> getSubjects() {
        return subjects;
    }

}
