package com.language.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.language.common.enums.Language;
import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String categoryName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonIgnore
    private Language language;

    public Category() {
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
