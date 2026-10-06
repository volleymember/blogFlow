package com.blog.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.blog.application.service.CategoryService;
import com.blog.domain.common.BusinessException;
import com.blog.domain.common.Constants;
import com.blog.domain.entity.Category;
import com.blog.domain.repository.CategoryRepository;
import com.blog.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getWebCategories(Long viewerId) {
        boolean admin = isAdmin(viewerId);
        return categoryRepository.findAllForWeb(admin, admin ? null : Constants.TAG_MOMENTS);
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public void createCategory(Category category) {
        if (StrUtil.isBlank(category.getSlug())) {
            category.setSlug(cn.hutool.core.util.IdUtil.simpleUUID().substring(0, 8));
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        categoryRepository.save(category);
    }

    @Override
    public void updateCategory(Category category) {
        Category existing = categoryRepository.findById(category.getId());
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }
        categoryRepository.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        categoryRepository.deleteById(id);
    }

    private boolean isAdmin(Long userId) {
        return userId != null && Constants.ROLE_ADMIN.equals(userRepository.findRoleKeyByUserId(userId));
    }
}
