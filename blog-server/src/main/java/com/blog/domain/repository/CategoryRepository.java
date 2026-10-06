package com.blog.domain.repository;

import com.blog.domain.entity.Category;

import java.util.List;

/**
 * 分类仓储接口
 */
public interface CategoryRepository {

    Category findById(Long id);

    Category findBySlug(String slug);

    List<Category> findAll();

    List<Category> findAllForWeb(Boolean includePrivateAll, String excludeTagName);

    void save(Category category);

    void update(Category category);

    void deleteById(Long id);

    Long countTotal();
}
