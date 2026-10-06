package com.blog.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.blog.application.service.ArticleService;
import com.blog.application.service.StorageService;
import com.blog.domain.common.BusinessException;
import com.blog.domain.common.Constants;
import com.blog.domain.common.PageResult;
import com.blog.domain.entity.Article;
import com.blog.domain.entity.Tag;
import com.blog.domain.repository.ArticleRepository;
import com.blog.domain.repository.TagRepository;
import com.blog.domain.repository.UserRepository;
import com.blog.infrastructure.persistence.mapper.ArticleTagMapper;
import com.blog.interfaces.dto.request.ArticleRequest;
import com.blog.interfaces.dto.response.ArchiveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章服务实现
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ArticleTagMapper articleTagMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StorageService storageService;

    @Override
    public PageResult<Article> getAdminArticleList(Long categoryId, Integer status, Integer visibility,
                                                   String keyword, Long viewerId, Integer pageNum, Integer pageSize) {
        boolean admin = isAdmin(viewerId);
        if (!admin && Objects.equals(visibility, Constants.ARTICLE_VISIBILITY_PRIVATE)) {
            return PageResult.of(List.of(), 0L, pageNum, pageSize);
        }
        Integer effectiveVisibility = admin ? visibility : Integer.valueOf(Constants.ARTICLE_VISIBILITY_PUBLIC);
        return queryArticleList(categoryId, status, effectiveVisibility, keyword, null, null,
                viewerId, admin, pageNum, pageSize);
    }

    @Override
    public PageResult<Article> getWebArticleList(Long categoryId, String keyword, Long viewerId,
                                                 Integer pageNum, Integer pageSize) {
        boolean admin = isAdmin(viewerId);
        PageResult<Article> result = queryArticleList(
                categoryId,
                Constants.ARTICLE_STATUS_PUBLISHED,
                admin ? null : Integer.valueOf(Constants.ARTICLE_VISIBILITY_PUBLIC),
                keyword,
                null,
                admin ? null : Constants.TAG_MOMENTS,
                null,
                admin,
                pageNum,
                pageSize
        );
        result.getList().forEach(this::hideContent);
        return result;
    }

    @Override
    public PageResult<Article> getMomentsArticleList(Long viewerId, Integer pageNum, Integer pageSize) {
        ensureAdmin(viewerId, "随心记仅管理员可访问");
        PageResult<Article> result = queryArticleList(
                null,
                Constants.ARTICLE_STATUS_PUBLISHED,
                null,
                null,
                Constants.TAG_MOMENTS,
                null,
                null,
                true,
                pageNum,
                pageSize
        );
        result.getList().forEach(this::hideContent);
        return result;
    }

    private PageResult<Article> queryArticleList(Long categoryId, Integer status, Integer visibility,
                                                 String keyword, String tagName, String excludeTagName,
                                                 Long viewerId, Boolean includePrivateAll,
                                                 Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Article> articles = articleRepository.findList(
                categoryId, status, visibility, keyword, tagName, excludeTagName,
                viewerId, includePrivateAll, offset, pageSize);
        Long total = articleRepository.count(categoryId, status, visibility, keyword, tagName, excludeTagName,
                viewerId, includePrivateAll);

        // 填充标签和可访问资源地址
        articles.forEach(article -> {
            fillTags(article);
            resolveAssetUrls(article);
        });

        return PageResult.of(articles, total, pageNum, pageSize);
    }

    @Override
    public Article getArticleById(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        fillTags(article);
        resolveAssetUrls(article);
        return article;
    }

    @Override
    public Article getArticleByIdForAdmin(Long id, Long viewerId) {
        Article article = getArticleById(id);
        ensureRestrictedArticleAccessible(article, viewerId);
        return article;
    }

    @Override
    public Article getWebArticleById(Long id, Long viewerId) {
        Article article = articleRepository.findById(id);
        if (article == null || !Objects.equals(article.getStatus(), Constants.ARTICLE_STATUS_PUBLISHED)) {
            throw new BusinessException(404, "文章不存在");
        }
        fillTags(article);
        resolveAssetUrls(article);
        ensureWebArticleAccessible(article, viewerId);
        return article;
    }

    @Override
    public Article getArticleBySlug(String slug) {
        Article article = articleRepository.findBySlug(slug);
        if (article == null || !Objects.equals(article.getStatus(), Constants.ARTICLE_STATUS_PUBLISHED)) {
            throw new BusinessException(404, "文章不存在");
        }
        fillTags(article);
        resolveAssetUrls(article);
        if (isPrivate(article) || hasRestrictedTag(article)) {
            throw new BusinessException(404, "文章不存在");
        }
        return article;
    }

    @Override
    public List<Article> getHotArticles(Long viewerId, Integer limit) {
        boolean admin = isAdmin(viewerId);
        List<Article> articles = articleRepository.findHotArticles(limit, admin, admin ? null : Constants.TAG_MOMENTS);
        articles.forEach(this::fillTags);
        articles.forEach(this::hideContent);
        return articles;
    }

    @Override
    @Transactional
    public void createArticle(ArticleRequest request, Long authorId) {
        ensureRestrictedRequestAccessible(request, authorId);
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSlug(StrUtil.isBlank(request.getSlug()) ?
                cn.hutool.core.util.IdUtil.simpleUUID().substring(0, 8) : request.getSlug());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setIsTop(defaultValue(request.getIsTop(), 0));
        article.setStatus(defaultValue(request.getStatus(), Constants.ARTICLE_STATUS_DRAFT));
        article.setVisibility(defaultValue(request.getVisibility(), Constants.ARTICLE_VISIBILITY_PUBLIC));
        article.setSeoKeywords(request.getSeoKeywords());
        article.setSeoDescription(request.getSeoDescription());
        article.setAuthorId(authorId);
        article.setViewCount(0L);
        article.setLikeCount(0);
        article.setCommentCount(0);

        if (Objects.equals(article.getStatus(), Constants.ARTICLE_STATUS_PUBLISHED)) {
            article.setPublishTime(LocalDateTime.now());
        }

        articleRepository.save(article);

        // 保存标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            articleTagMapper.insertBatch(article.getId(), request.getTagIds());
        }
        clearArticleCaches();
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleRequest request, Long viewerId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        fillTags(article);
        ensureRestrictedArticleAccessible(article, viewerId);
        ensureRestrictedRequestAccessible(request, viewerId);
        Integer oldStatus = article.getStatus();

        article.setTitle(request.getTitle());
        article.setSlug(request.getSlug());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setIsTop(defaultValue(request.getIsTop(), 0));
        article.setStatus(defaultValue(request.getStatus(), Constants.ARTICLE_STATUS_DRAFT));
        article.setVisibility(defaultValue(request.getVisibility(), Constants.ARTICLE_VISIBILITY_PUBLIC));
        article.setSeoKeywords(request.getSeoKeywords());
        article.setSeoDescription(request.getSeoDescription());

        // 草稿发布时设置发布时间
        if (!Objects.equals(oldStatus, Constants.ARTICLE_STATUS_PUBLISHED)
                && Objects.equals(article.getStatus(), Constants.ARTICLE_STATUS_PUBLISHED)) {
            article.setPublishTime(LocalDateTime.now());
        }

        articleRepository.update(article);

        // 更新标签关联
        articleTagMapper.deleteByArticleId(id);
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            articleTagMapper.insertBatch(id, request.getTagIds());
        }
        clearArticleCaches();
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long viewerId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        fillTags(article);
        ensureRestrictedArticleAccessible(article, viewerId);
        articleRepository.deleteById(id);
        articleTagMapper.deleteByArticleId(id);
        clearArticleCaches();
    }

    @Override
    public void incrementViewCount(Long id) {
        // 使用 Redis 计数，定时任务同步到数据库
        String key = Constants.REDIS_ARTICLE_VIEW + id;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    @Override
    public List<ArchiveVO> getArchiveList(Long viewerId) {
        boolean admin = isAdmin(viewerId);
        List<Article> articles = articleRepository.findArchiveList(admin, admin ? null : Constants.TAG_MOMENTS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return articles.stream().map(article -> {
            ArchiveVO vo = new ArchiveVO();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle());
            vo.setSlug(article.getSlug());
            vo.setVisibility(article.getVisibility());
            vo.setCreateTime(article.getCreateTime());
            vo.setYearMonth(article.getCreateTime().format(formatter));
            return vo;
        }).collect(Collectors.toList());
    }

    private void fillTags(Article article) {
        if (article != null) {
            List<Tag> tags = tagRepository.findByArticleId(article.getId());
            article.setTags(tags);
        }
    }

    private void resolveAssetUrls(Article article) {
        if (article == null) {
            return;
        }
        article.setCoverImage(storageService.resolveUrl(article.getCoverImage()));
        article.setContent(storageService.resolveContentUrls(article.getContent()));
    }

    private boolean isPrivate(Article article) {
        return Objects.equals(defaultValue(article.getVisibility(), Constants.ARTICLE_VISIBILITY_PUBLIC),
                Constants.ARTICLE_VISIBILITY_PRIVATE);
    }

    private boolean hasRestrictedTag(Article article) {
        return article.getTags() != null
                && article.getTags().stream().anyMatch(tag -> Objects.equals(Constants.TAG_MOMENTS, tag.getName()));
    }

    private void hideContent(Article article) {
        if (article != null) {
            article.setContent(null);
            article.setSeoKeywords(null);
            article.setSeoDescription(null);
        }
    }

    private void ensureWebArticleAccessible(Article article, Long viewerId) {
        if (!isPrivate(article) && !hasRestrictedTag(article)) {
            return;
        }
        ensureAdmin(viewerId, hasRestrictedTag(article) ? "随心记仅管理员可查看" : "私密文章仅管理员可查看");
    }

    private void ensureRestrictedArticleAccessible(Article article, Long viewerId) {
        if (hasRestrictedTag(article)) {
            ensureAdmin(viewerId, "随心记仅管理员可访问");
        }
        if (isPrivate(article)) {
            ensureAdmin(viewerId, "私密文章仅管理员可访问");
        }
    }

    private void ensureRestrictedRequestAccessible(ArticleRequest request, Long viewerId) {
        if (Objects.equals(request.getVisibility(), Constants.ARTICLE_VISIBILITY_PRIVATE)) {
            ensureAdmin(viewerId, "私密文章仅管理员可保存");
        }
        List<Long> tagIds = request.getTagIds();
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        boolean containsRestrictedTag = tagIds.stream()
                .map(tagRepository::findById)
                .filter(Objects::nonNull)
                .anyMatch(tag -> Objects.equals(Constants.TAG_MOMENTS, tag.getName()));
        if (containsRestrictedTag) {
            ensureAdmin(viewerId, "随心记标签仅管理员可使用");
        }
    }

    private void ensureAdmin(Long userId, String message) {
        if (!isAdmin(userId)) {
            throw new BusinessException(403, message);
        }
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        return Constants.ROLE_ADMIN.equals(userRepository.findRoleKeyByUserId(userId));
    }

    private Integer defaultValue(Integer value, Integer fallback) {
        return value != null ? value : fallback;
    }

    private void clearArticleCaches() {
        redisTemplate.delete(Constants.REDIS_HOT_ARTICLES);
    }
}
