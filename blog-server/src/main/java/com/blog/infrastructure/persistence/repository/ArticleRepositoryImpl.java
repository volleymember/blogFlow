package com.blog.infrastructure.persistence.repository;

import com.blog.domain.entity.Article;
import com.blog.domain.repository.ArticleRepository;
import com.blog.infrastructure.persistence.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章仓储实现
 */
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleMapper articleMapper;

    @Override
    public Article findById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public Article findBySlug(String slug) {
        return articleMapper.selectBySlug(slug);
    }

    @Override
    public List<Article> findList(Long categoryId, Integer status, Integer visibility, String keyword,
                                  String tagName, String excludeTagName, Long viewerId,
                                  Boolean includePrivateAll, Integer offset, Integer limit) {
        return articleMapper.selectList(categoryId, status, visibility, keyword, tagName, excludeTagName,
                viewerId, includePrivateAll, offset, limit);
    }

    @Override
    public Long count(Long categoryId, Integer status, Integer visibility, String keyword,
                      String tagName, String excludeTagName, Long viewerId, Boolean includePrivateAll) {
        return articleMapper.selectCount(categoryId, status, visibility, keyword, tagName, excludeTagName,
                viewerId, includePrivateAll);
    }

    @Override
    public List<Article> findHotArticles(Integer limit, Boolean includePrivateAll, String excludeTagName) {
        return articleMapper.selectHotArticles(limit, includePrivateAll, excludeTagName);
    }

    @Override
    public void save(Article article) {
        articleMapper.insert(article);
    }

    @Override
    public void update(Article article) {
        articleMapper.update(article);
    }

    @Override
    public void deleteById(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id, Long count) {
        articleMapper.updateViewCount(id, count);
    }

    @Override
    public Long countTotal(Integer status) {
        return articleMapper.selectTotalCount(status);
    }

    @Override
    public List<Article> findArchiveList(Boolean includePrivateAll, String excludeTagName) {
        return articleMapper.selectArchiveList(includePrivateAll, excludeTagName);
    }
}
