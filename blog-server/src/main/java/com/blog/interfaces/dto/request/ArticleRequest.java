package com.blog.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 文章请求
 */
@Data
public class ArticleRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String slug;

    private String summary;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String coverImage;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private List<Long> tagIds;

    private Integer isTop = 0;

    private Integer status = 0;

    /**
     * 可见性：0公开 1私密
     */
    private Integer visibility = 0;

    private String seoKeywords;

    private String seoDescription;
}
