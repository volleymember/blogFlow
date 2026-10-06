package com.blog.infrastructure.persistence.repository;

import com.blog.domain.entity.Category;
import com.blog.domain.repository.CategoryRepository;
import com.blog.infrastructure.persistence.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类仓储实现
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public Category findById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public Category findBySlug(String slug) {
        return categoryMapper.selectBySlug(slug);
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<Category> findAllForWeb(Boolean includePrivateAll, String excludeTagName) {
        return categoryMapper.selectAllForWeb(includePrivateAll, excludeTagName);
    }

    @Override
    public void save(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void update(Category category) {
        categoryMapper.update(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public Long countTotal() {
        return categoryMapper.selectTotalCount();
    }
}
