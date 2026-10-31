package com.language.category.dto;

public class CategorySummaryResponse {

    private Long id;
    private String categoryName;

    public CategorySummaryResponse(Long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

}
