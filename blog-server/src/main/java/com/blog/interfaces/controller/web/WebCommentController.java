package com.blog.interfaces.controller.web;

import com.blog.application.service.CommentService;
import com.blog.domain.common.Result;
import com.blog.domain.entity.Comment;
import com.blog.infrastructure.security.UserPrincipal;
import com.blog.interfaces.dto.request.CommentRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台评论控制器
 */
@RestController
@RequestMapping("/api/web/comments")
@RequiredArgsConstructor
public class WebCommentController {

    private final CommentService commentService;

    /**
     * 获取文章评论
     */
    @GetMapping("/article/{articleId}")
    public Result<List<Comment>> listByArticle(@PathVariable Long articleId,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        return Result.success(commentService.getCommentsByArticleId(articleId, viewerId));
    }

    /**
     * 提交评论
     */
    @PostMapping
    public Result<Void> submit(@Valid @RequestBody CommentRequest request,
                               HttpServletRequest httpRequest,
                               @AuthenticationPrincipal UserPrincipal principal) {
        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        Long viewerId = principal != null ? principal.getUserId() : null;
        commentService.createComment(request, ip, userAgent, viewerId);
        return Result.success("评论提交成功，等待审核", null);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
