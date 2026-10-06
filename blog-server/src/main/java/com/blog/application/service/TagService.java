package com.blog.application.service;

import com.blog.domain.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    List<Tag> getAllTags();

    List<Tag> getWebTags(Long viewerId);

    Tag getTagById(Long id);

    void createTag(Tag tag);

    void updateTag(Tag tag);

    void deleteTag(Long id);
}
