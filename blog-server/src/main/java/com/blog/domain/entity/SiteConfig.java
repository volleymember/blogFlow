package com.blog.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网站配置实体
 */
@Data
public class SiteConfig {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
    private LocalDateTime updateTime;
}
