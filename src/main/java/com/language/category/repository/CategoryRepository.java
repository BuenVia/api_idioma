package com.language.category.repository;

import com.language.category.entity.Category;
import com.language.common.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByLanguage(Language language);
    Optional<Category> findByIdAndLanguage(Long id, Language language);
}
