package com.blog.interfaces.controller.web;

import com.blog.application.service.TagService;
import com.blog.domain.common.Result;
import com.blog.domain.entity.Tag;
import com.blog.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台标签控制器
 */
@RestController
@RequestMapping("/api/web/tags")
@RequiredArgsConstructor
public class WebTagController {

    private final TagService tagService;

    /**
     * 标签列表
     */
    @GetMapping
    public Result<List<Tag>> list(@AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getUserId() : null;
        return Result.success(tagService.getWebTags(viewerId));
    }
}
