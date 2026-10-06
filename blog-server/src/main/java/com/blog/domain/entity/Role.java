package com.blog.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体
 */
@Data
public class Role {
    private Long id;
    private String roleName;
    private String roleKey;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
