package com.blog.interfaces.controller.web;

import com.blog.application.service.ArticleService;
import com.blog.domain.common.PageResult;
import com.blog.domain.common.Result;
import com.blog.domain.entity.Article;
import com.blog.infrastructure.security.UserPrincipal;
import com.blog.interfaces.dto.response.ArchiveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台文章控制器
 */
@RestController
@RequestMapping("/api/web/articles")
@RequiredArgsConstructor
public class WebArticleController {

    private final ArticleService articleService;

    /**
     * 文章列表
     */
    @GetMapping
    public Result<PageResult<Article>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        PageResult<Article> result = articleService.getWebArticleList(categoryId, keyword, viewerId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 随心记文章列表
     */
    @GetMapping("/moments")
    public Result<PageResult<Article>> moments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        return Result.success(articleService.getMomentsArticleList(viewerId, pageNum, pageSize));
    }

    /**
     * 文章详情
     */
    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id,
                                  @AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        Article article = articleService.getWebArticleById(id, viewerId);
        articleService.incrementViewCount(id);
        return Result.success(article);
    }

    /**
     * 热门文章
     */
    @GetMapping("/hot")
    public Result<List<Article>> hotArticles(@RequestParam(defaultValue = "10") Integer limit,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        return Result.success(articleService.getHotArticles(viewerId, limit));
    }

    /**
     * 归档
     */
    @GetMapping("/archive")
    public Result<List<ArchiveVO>> archive(@AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        return Result.success(articleService.getArchiveList(viewerId));
    }
}
