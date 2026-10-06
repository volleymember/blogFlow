package com.blog.domain.repository;

import com.blog.domain.entity.Comment;

import java.util.List;

/**
 * 评论仓储接口
 */
public interface CommentRepository {

    Comment findById(Long id);

    List<Comment> findByArticleId(Long articleId, Integer status);

    List<Comment> findList(Integer status, Integer offset, Integer limit);

    Long count(Integer status);

    void save(Comment comment);

    void updateStatus(Long id, Integer status);

    void deleteById(Long id);

    Long countTotal(Integer status);
}
