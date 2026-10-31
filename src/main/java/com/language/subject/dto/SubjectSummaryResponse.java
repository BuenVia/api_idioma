package com.language.subject.dto;

import com.language.category.dto.CategorySummaryResponse;

public class SubjectSummaryResponse {

    private Long id;
    private String subjectName;
    private CategorySummaryResponse category;

    public SubjectSummaryResponse(Long id, String subjectName, CategorySummaryResponse category) {
        this.id = id;
        this.subjectName = subjectName;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public CategorySummaryResponse getCategory() {
        return category;
    }

}
