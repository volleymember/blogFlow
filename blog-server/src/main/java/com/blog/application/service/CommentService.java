package com.blog.application.service;

import com.blog.domain.common.PageResult;
import com.blog.domain.entity.Comment;
import com.blog.interfaces.dto.request.CommentRequest;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {

    List<Comment> getCommentsByArticleId(Long articleId, Long viewerId);

    PageResult<Comment> getCommentList(Integer status, Integer pageNum, Integer pageSize);

    void createComment(CommentRequest request, String ip, String userAgent, Long viewerId);

    void auditComment(Long id, Integer status);

    void deleteComment(Long id);
}
