package com.blog.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户请求
 */
@Data
public class UserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;

    private String avatar;

    private String email;

    private Integer status = 1;
}
