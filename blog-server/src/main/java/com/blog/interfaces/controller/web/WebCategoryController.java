package com.blog.interfaces.controller.web;

import com.blog.application.service.ArticleService;
import com.blog.application.service.CategoryService;
import com.blog.domain.common.PageResult;
import com.blog.domain.common.Result;
import com.blog.domain.entity.Article;
import com.blog.domain.entity.Category;
import com.blog.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台分类控制器
 */
@RestController
@RequestMapping("/api/web/categories")
@RequiredArgsConstructor
public class WebCategoryController {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    /**
     * 分类列表
     */
    @GetMapping
    public Result<List<Category>> list(@AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        return Result.success(categoryService.getWebCategories(viewerId));
    }

    /**
     * 分类下的文章
     */
    @GetMapping("/{slug}/articles")
    public Result<PageResult<Article>> articlesByCategory(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @AuthenticationPrincipal UserPrincipal principal) {
        Category category = categoryService.getCategoryBySlug(slug);
        Long viewerId = principal != null ? principal.getUserId() : null;
        PageResult<Article> result = articleService.getWebArticleList(category.getId(), null, viewerId, pageNum, pageSize);
        return Result.success(result);
    }
}
