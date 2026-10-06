package com.blog.infrastructure.persistence.mapper;

import com.blog.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper {

    Category selectById(@Param("id") Long id);

    Category selectBySlug(@Param("slug") String slug);

    List<Category> selectAll();

    List<Category> selectAllForWeb(@Param("includePrivateAll") Boolean includePrivateAll,
                                   @Param("excludeTagName") String excludeTagName);

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Long id);

    Long selectTotalCount();
}
