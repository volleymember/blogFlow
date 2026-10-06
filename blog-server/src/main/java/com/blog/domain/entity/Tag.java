package com.blog.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体
 */
@Data
public class Tag {
    private Long id;
    private String name;
    private String slug;
    private String color;
    private LocalDateTime createTime;

    // 统计属性
    private Integer articleCount;
}
