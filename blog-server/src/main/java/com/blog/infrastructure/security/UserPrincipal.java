package com.blog.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户身份主体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {
    private Long userId;
    private String username;
}
