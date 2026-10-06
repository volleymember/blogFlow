package com.blog.domain.repository;

import com.blog.domain.entity.Article;

import java.util.List;

/**
 * 文章仓储接口
 */
public interface ArticleRepository {

    Article findById(Long id);

    Article findBySlug(String slug);

    List<Article> findList(Long categoryId, Integer status, Integer visibility, String keyword,
                           String tagName, String excludeTagName, Long viewerId,
                           Boolean includePrivateAll, Integer offset, Integer limit);

    Long count(Long categoryId, Integer status, Integer visibility, String keyword,
               String tagName, String excludeTagName, Long viewerId, Boolean includePrivateAll);

    List<Article> findHotArticles(Integer limit, Boolean includePrivateAll, String excludeTagName);

    void save(Article article);

    void update(Article article);

    void deleteById(Long id);

    void incrementViewCount(Long id, Long count);

    Long countTotal(Integer status);

    List<Article> findArchiveList(Boolean includePrivateAll, String excludeTagName);
}
