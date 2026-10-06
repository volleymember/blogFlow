package com.blog.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体
 */
@Data
public class Comment {
    private Long id;
    private Long articleId;
    private Long parentId;
    private Long replyToId;
    private String nickname;
    private String email;
    private String website;
    private String content;
    private String ip;
    private String userAgent;
    private Integer status;
    private LocalDateTime createTime;

    // 关联属性
    private String articleTitle;
    private String replyToNickname;
    private List<Comment> children;
}
