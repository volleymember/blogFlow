package com.blog.interfaces.controller.admin;

import com.blog.application.service.TagService;
import com.blog.domain.common.Result;
import com.blog.domain.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台标签管理控制器
 */
@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    /**
     * 标签列表
     */
    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagService.getAllTags());
    }

    /**
     * 标签详情
     */
    @GetMapping("/{id}")
    public Result<Tag> detail(@PathVariable Long id) {
        return Result.success(tagService.getTagById(id));
    }

    /**
     * 创建标签
     */
    @PostMapping
    public Result<Void> create(@RequestBody Tag tag) {
        tagService.createTag(tag);
        return Result.success();
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        tagService.updateTag(tag);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}
