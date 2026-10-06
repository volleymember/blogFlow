package com.blog.interfaces.controller.admin;

import com.blog.application.service.CommentService;
import com.blog.domain.common.PageResult;
import com.blog.domain.common.Result;
import com.blog.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台评论管理控制器
 */
@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    /**
     * 评论列表
     */
    @GetMapping
    public Result<PageResult<Comment>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Comment> result = commentService.getCommentList(status, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 审核评论
     */
    @PutMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        commentService.auditComment(id, status);
        return Result.success();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }
}
