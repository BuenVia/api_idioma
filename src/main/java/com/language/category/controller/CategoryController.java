package com.language.category.controller;

import com.language.category.dto.CategoryResponse;
import com.language.category.dto.CategorySummaryResponse;
import com.language.category.dto.CreateCategoryRequest;
import com.language.category.dto.UpdateCategoryRequest;
import com.language.category.service.CategoryService;
import com.language.common.enums.Language;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{language}/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/getcategorynames")
    public List<CategorySummaryResponse> getCategoryNames(@PathVariable Language language) {
        return categoryService.getCategoryNames(language);
    }

    @GetMapping("/get/{id}")
    public CategoryResponse getCategory(@PathVariable Language language, @PathVariable Long id) {
        return categoryService.getCategory(language, id);
    }

    @PostMapping("/create")
    public CategorySummaryResponse createCategory(@PathVariable Language language,
            @RequestBody CreateCategoryRequest createCategoryRequest) {
        return categoryService.createCategory(language, createCategoryRequest);
    }

    @PutMapping("/update/{id}")
    public CategorySummaryResponse updateCategory(@PathVariable Language language, @PathVariable Long id,
            @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        return categoryService.updateCategory(language, id, updateCategoryRequest);
    }

}
