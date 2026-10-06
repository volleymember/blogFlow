package com.blog.domain.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 访问日志实体
 */
@Data
public class VisitLog {
    private Long id;
    private String ip;
    private String url;
    private Long articleId;
    private String userAgent;
    private String referer;
    private LocalDate visitDate;
    private LocalDateTime visitTime;
}
