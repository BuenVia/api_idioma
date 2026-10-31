package com.language.subject.dto;

import com.language.category.dto.CategorySummaryResponse;

public class SubjectResponse {

    private String subjectName;
    private Long subjectId;
    private CategorySummaryResponse category;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public CategorySummaryResponse getCategory() {
        return category;
    }

    public void setCategory(CategorySummaryResponse category) {
        this.category = category;
    }

}
