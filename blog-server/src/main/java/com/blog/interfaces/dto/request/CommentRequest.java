package com.blog.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论请求
 */
@Data
public class CommentRequest {
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    private Long parentId;

    private Long replyToId;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String email;

    private String website;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
