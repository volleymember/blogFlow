package com.blog.infrastructure.persistence.mapper;

import com.blog.domain.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper {

    Comment selectById(@Param("id") Long id);

    List<Comment> selectByArticleId(@Param("articleId") Long articleId,
                                    @Param("status") Integer status);

    List<Comment> selectList(@Param("status") Integer status,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    Long selectCount(@Param("status") Integer status);

    int insert(Comment comment);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(@Param("id") Long id);

    Long selectTotalCount(@Param("status") Integer status);
}
