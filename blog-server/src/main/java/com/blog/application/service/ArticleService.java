package com.blog.application.service;

import com.blog.domain.common.PageResult;
import com.blog.domain.entity.Article;
import com.blog.interfaces.dto.request.ArticleRequest;
import com.blog.interfaces.dto.response.ArchiveVO;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {

    PageResult<Article> getAdminArticleList(Long categoryId, Integer status, Integer visibility,
                                            String keyword, Long viewerId, Integer pageNum, Integer pageSize);

    PageResult<Article> getWebArticleList(Long categoryId, String keyword, Long viewerId,
                                          Integer pageNum, Integer pageSize);

    PageResult<Article> getMomentsArticleList(Long viewerId, Integer pageNum, Integer pageSize);

    Article getArticleById(Long id);

    Article getArticleByIdForAdmin(Long id, Long viewerId);

    Article getWebArticleById(Long id, Long viewerId);

    Article getArticleBySlug(String slug);

    List<Article> getHotArticles(Long viewerId, Integer limit);

    void createArticle(ArticleRequest request, Long authorId);

    void updateArticle(Long id, ArticleRequest request, Long viewerId);

    void deleteArticle(Long id, Long viewerId);

    void incrementViewCount(Long id);

    List<ArchiveVO> getArchiveList(Long viewerId);
}
