package com.blog.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签请求
 */
@Data
public class TagRequest {
    @NotBlank(message = "标签名称不能为空")
    private String name;

    private String slug;

    private String color;
}
