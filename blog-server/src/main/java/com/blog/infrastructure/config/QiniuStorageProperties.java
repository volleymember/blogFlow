package com.blog.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

/**
 * 七牛云对象存储配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog.storage.qiniu")
public class QiniuStorageProperties {
    private Boolean enabled = false;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;
    private String protocol = "http";
    private String prefix = "blog";
    private DataSize maxFileSize = DataSize.ofMegabytes(20);
    private Long tokenExpireSeconds = 3600L;
    private Boolean privateDownloadEnabled = true;
    private Long downloadUrlExpireSeconds = 315360000L;
    private String region = "auto";
    private Integer connectTimeoutSeconds = 10;
    private Integer readTimeoutSeconds = 60;
    private Integer writeTimeoutSeconds = 60;
    private Integer retryMax = 1;
}
