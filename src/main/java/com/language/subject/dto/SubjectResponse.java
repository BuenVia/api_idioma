package com.language.subject.dto;

import com.language.common.enums.Level;

import com.language.category.dto.CategorySummaryResponse;

public class SubjectResponse {

    private String subjectName;
    private Level level;
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
