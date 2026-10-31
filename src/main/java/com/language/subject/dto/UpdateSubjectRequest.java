package com.language.subject.dto;

import com.language.common.enums.Level;

public class UpdateSubjectRequest {

    private String subjectName;
    private Level level;
    private Long categoryId;
    private boolean removeCategory;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isRemoveCategory() {
        return removeCategory;
    }

    public void setRemoveCategory(boolean removeCategory) {
        this.removeCategory = removeCategory;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
