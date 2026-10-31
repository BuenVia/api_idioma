package com.language.category.service;

import com.language.category.dto.CategoryResponse;
import com.language.category.dto.CreateCategoryRequest;
import com.language.category.dto.UpdateCategoryRequest;
import com.language.category.dto.CategorySubjectResponse;
import com.language.category.dto.CategorySummaryResponse;
import com.language.category.entity.Category;
import com.language.category.repository.CategoryRepository;
import com.language.common.enums.Language;
import com.language.common.exception.ResourceNotFoundException;
import com.language.subject.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubjectRepository subjectRepository;

    public CategoryService(CategoryRepository categoryRepository, SubjectRepository subjectRepository) {
        this.categoryRepository = categoryRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<CategorySummaryResponse> getCategoryNames(Language language) {
        return categoryRepository.findByLanguage(language)
                .stream()
                .map(category -> new CategorySummaryResponse(
                        category.getId(),
                        category.getCategoryName()
                ))
                .toList();
    }

    public CategoryResponse getCategory(Language language, Long id) {
        Category category = categoryRepository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        List<CategorySubjectResponse> subjects = subjectRepository.findByCategoryAndLanguage(category, language)
                .stream()
                .map(subject -> new CategorySubjectResponse(
                        subject.getId(),
                        subject.getSubjectName()
                ))
                .toList();

        return new CategoryResponse(category.getId(), category.getCategoryName(), subjects);
    }

    public CategorySummaryResponse createCategory(Language language, CreateCategoryRequest request) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setLanguage(language);

        Category savedCategory = categoryRepository.save(category);
        return new CategorySummaryResponse(savedCategory.getId(), savedCategory.getCategoryName());
    }

    public CategorySummaryResponse updateCategory(Language language, Long id, UpdateCategoryRequest request) {
        Category existingCategory = categoryRepository.findByIdAndLanguage(id, language)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        existingCategory.setCategoryName(request.getCategoryName());

        Category savedCategory = categoryRepository.save(existingCategory);
        return new CategorySummaryResponse(savedCategory.getId(), savedCategory.getCategoryName());
    }
}
