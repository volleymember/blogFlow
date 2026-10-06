package com.blog.interfaces.controller.admin;

import com.blog.application.service.CategoryService;
import com.blog.domain.common.Result;
import com.blog.domain.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台分类管理控制器
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 分类列表
     */
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.getAllCategories());
    }

    /**
     * 分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> detail(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Void> create(@RequestBody Category category) {
        categoryService.createCategory(category);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
