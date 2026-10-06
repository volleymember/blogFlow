package com.blog.application.service;

import com.blog.domain.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    List<Category> getAllCategories();

    List<Category> getWebCategories(Long viewerId);

    Category getCategoryById(Long id);

    Category getCategoryBySlug(String slug);

    void createCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Long id);
}
