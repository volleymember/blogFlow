package com.blog.infrastructure.persistence.repository;

import com.blog.domain.entity.Comment;
import com.blog.domain.repository.CommentRepository;
import com.blog.infrastructure.persistence.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论仓储实现
 */
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentMapper commentMapper;

    @Override
    public Comment findById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public List<Comment> findByArticleId(Long articleId, Integer status) {
        return commentMapper.selectByArticleId(articleId, status);
    }

    @Override
    public List<Comment> findList(Integer status, Integer offset, Integer limit) {
        return commentMapper.selectList(status, offset, limit);
    }

    @Override
    public Long count(Integer status) {
        return commentMapper.selectCount(status);
    }

    @Override
    public void save(Comment comment) {
        commentMapper.insert(comment);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        commentMapper.updateStatus(id, status);
    }

    @Override
    public void deleteById(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    public Long countTotal(Integer status) {
        return commentMapper.selectTotalCount(status);
    }
}
