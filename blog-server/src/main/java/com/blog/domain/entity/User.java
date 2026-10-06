package com.blog.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;

    // 关联属性
    private String roleKey;
}
