package com.otopart.domain.product.service;

import com.otopart.domain.product.entity.Category;
import com.otopart.domain.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getMainCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrue();
    }

    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentIdAndActiveTrue(parentId);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı"));
    }

    @Transactional
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }
}