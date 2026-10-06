package com.blog.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类请求
 */
@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String slug;

    private String description;

    private Integer sortOrder;
}
