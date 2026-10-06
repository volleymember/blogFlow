package com.blog.interfaces.dto.response;

import com.blog.domain.entity.Article;
import com.blog.domain.entity.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象
 */
@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isTop;
    private Integer status;
    private Integer visibility;
    private String seoKeywords;
    private String seoDescription;
    private Long authorId;
    private String authorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
    private List<Tag> tags;

    public static ArticleVO fromEntity(Article article) {
        if (article == null) return null;
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSlug(article.getSlug());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setCoverImage(article.getCoverImage());
        vo.setCategoryId(article.getCategoryId());
        vo.setCategoryName(article.getCategoryName());
        vo.setCategorySlug(article.getCategorySlug());
        vo.setViewCount(article.getViewCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setCommentCount(article.getCommentCount());
        vo.setIsTop(article.getIsTop());
        vo.setStatus(article.getStatus());
        vo.setVisibility(article.getVisibility());
        vo.setSeoKeywords(article.getSeoKeywords());
        vo.setSeoDescription(article.getSeoDescription());
        vo.setAuthorId(article.getAuthorId());
        vo.setAuthorName(article.getAuthorName());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        vo.setPublishTime(article.getPublishTime());
        vo.setTags(article.getTags());
        return vo;
    }
}
