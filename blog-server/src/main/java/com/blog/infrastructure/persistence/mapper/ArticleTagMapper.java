package com.blog.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章标签关联Mapper
 */
@Mapper
public interface ArticleTagMapper {

    int insertBatch(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);

    int deleteByArticleId(@Param("articleId") Long articleId);

    List<Long> selectTagIdsByArticleId(@Param("articleId") Long articleId);
}
