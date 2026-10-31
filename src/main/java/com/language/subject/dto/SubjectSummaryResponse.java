package com.language.subject.dto;

import com.language.category.dto.CategorySummaryResponse;
import com.language.common.enums.Level;

public class SubjectSummaryResponse {

    private Long id;
    private String subjectName;
    private Level level;
    private CategorySummaryResponse category;

    public SubjectSummaryResponse(Long id, String subjectName, Level level, CategorySummaryResponse category) {
        this.id = id;
        this.subjectName = subjectName;
        this.level = level;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Level getLevel() {
        return level;
    }

    public CategorySummaryResponse getCategory() {
        return category;
    }

}
