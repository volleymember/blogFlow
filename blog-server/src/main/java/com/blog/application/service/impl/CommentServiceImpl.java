package com.blog.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.blog.application.service.CommentService;
import com.blog.application.service.SiteConfigService;
import com.blog.domain.common.BusinessException;
import com.blog.domain.common.Constants;
import com.blog.domain.common.PageResult;
import com.blog.domain.entity.Article;
import com.blog.domain.entity.Comment;
import com.blog.domain.repository.ArticleRepository;
import com.blog.domain.repository.CommentRepository;
import com.blog.domain.repository.TagRepository;
import com.blog.domain.repository.UserRepository;
import com.blog.infrastructure.persistence.mapper.ArticleMapper;
import com.blog.interfaces.dto.request.CommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ArticleMapper articleMapper;
    private final SiteConfigService siteConfigService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Comment> getCommentsByArticleId(Long articleId, Long viewerId) {
        ensureArticleAccessible(articleId, viewerId);
        List<Comment> comments = commentRepository.findByArticleId(articleId, Constants.COMMENT_STATUS_APPROVED);
        return buildCommentTree(comments);
    }

    @Override
    public PageResult<Comment> getCommentList(Integer status, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Comment> comments = commentRepository.findList(status, offset, pageSize);
        Long total = commentRepository.count(status);
        return PageResult.of(comments, total, pageNum, pageSize);
    }

    @Override
    public void createComment(CommentRequest request, String ip, String userAgent, Long viewerId) {
        ensureArticleAccessible(request.getArticleId(), viewerId);
        // 防刷检查
        checkRateLimit(ip);

        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        comment.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        comment.setReplyToId(request.getReplyToId());
        comment.setNickname(request.getNickname());
        comment.setEmail(request.getEmail());
        comment.setWebsite(request.getWebsite());
        comment.setContent(request.getContent());
        comment.setIp(ip);
        comment.setUserAgent(userAgent);

        // 检查是否需要审核
        var config = siteConfigService.getSiteConfig();
        comment.setStatus(config.getCommentAudit() ? Constants.COMMENT_STATUS_PENDING : Constants.COMMENT_STATUS_APPROVED);

        commentRepository.save(comment);

        // 更新文章评论数
        articleMapper.updateCommentCount(request.getArticleId(), 1);
    }

    @Override
    public void auditComment(Long id, Integer status) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        commentRepository.updateStatus(id, status);

        // 如果是通过审核，更新文章评论数
        if (status == Constants.COMMENT_STATUS_APPROVED && comment.getStatus() != Constants.COMMENT_STATUS_APPROVED) {
            articleMapper.updateCommentCount(comment.getArticleId(), 1);
        }
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        commentRepository.deleteById(id);

        // 更新文章评论数
        if (comment.getStatus() == Constants.COMMENT_STATUS_APPROVED) {
            articleMapper.updateCommentCount(comment.getArticleId(), -1);
        }
    }

    /**
     * 构建评论树
     */
    private List<Comment> buildCommentTree(List<Comment> comments) {
        Map<Long, List<Comment>> parentMap = comments.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<Comment> rootComments = parentMap.getOrDefault(0L, new ArrayList<>());
        rootComments.forEach(comment -> setChildren(comment, parentMap));
        return rootComments;
    }

    private void setChildren(Comment comment, Map<Long, List<Comment>> parentMap) {
        List<Comment> children = parentMap.get(comment.getId());
        if (children != null && !children.isEmpty()) {
            comment.setChildren(children);
            children.forEach(child -> setChildren(child, parentMap));
        }
    }

    /**
     * 防刷检查
     */
    private void checkRateLimit(String ip) {
        String key = "blog:comment:limit:" + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }
        if (count != null && count > 5) {
            throw new BusinessException("评论过于频繁，请稍后再试");
        }
    }

    private void ensureArticleAccessible(Long articleId, Long viewerId) {
        Article article = articleRepository.findById(articleId);
        if (article == null
                || article.getStatus() == null
                || article.getStatus() != Constants.ARTICLE_STATUS_PUBLISHED) {
            throw new BusinessException(404, "文章不存在");
        }
        boolean privateArticle = article.getVisibility() != null
                && article.getVisibility() == Constants.ARTICLE_VISIBILITY_PRIVATE;
        boolean momentsArticle = tagRepository.findByArticleId(articleId).stream()
                .anyMatch(tag -> Constants.TAG_MOMENTS.equals(tag.getName()));
        if ((privateArticle || momentsArticle) && !isAdmin(viewerId)) {
            throw new BusinessException(403, momentsArticle ? "随心记仅管理员可查看评论" : "私密文章仅管理员可查看评论");
        }
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        return Constants.ROLE_ADMIN.equals(userRepository.findRoleKeyByUserId(userId));
    }
}
