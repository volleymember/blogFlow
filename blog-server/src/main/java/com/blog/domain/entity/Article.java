package com.blog.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体
 */
@Data
public class Article {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isTop;
    private Integer status;
    private Integer visibility;
    private String seoKeywords;
    private String seoDescription;
    private Long authorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
    private Integer deleted;

    // 关联属性
    private String categoryName;
    private String categorySlug;
    private String authorName;
    private List<Tag> tags;
}
