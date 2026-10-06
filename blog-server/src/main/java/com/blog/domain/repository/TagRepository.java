package com.blog.domain.repository;

import com.blog.domain.entity.Tag;

import java.util.List;

/**
 * 标签仓储接口
 */
public interface TagRepository {

    Tag findById(Long id);

    Tag findByName(String name);

    List<Tag> findAll();

    List<Tag> findAllForWeb(Boolean includePrivateAll, String restrictedTagName);

    List<Tag> findByArticleId(Long articleId);

    void save(Tag tag);

    void update(Tag tag);

    void deleteById(Long id);
}
