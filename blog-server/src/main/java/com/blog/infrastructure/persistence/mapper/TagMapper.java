package com.blog.infrastructure.persistence.mapper;

import com.blog.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper
 */
@Mapper
public interface TagMapper {

    Tag selectById(@Param("id") Long id);

    Tag selectByName(@Param("name") String name);

    List<Tag> selectAll();

    List<Tag> selectAllForWeb(@Param("includePrivateAll") Boolean includePrivateAll,
                              @Param("restrictedTagName") String restrictedTagName);

    List<Tag> selectByArticleId(@Param("articleId") Long articleId);

    int insert(Tag tag);

    int update(Tag tag);

    int deleteById(@Param("id") Long id);
}
