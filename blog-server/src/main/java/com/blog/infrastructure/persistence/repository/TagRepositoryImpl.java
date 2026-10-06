package com.blog.infrastructure.persistence.repository;

import com.blog.domain.entity.Tag;
import com.blog.domain.repository.TagRepository;
import com.blog.infrastructure.persistence.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标签仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final TagMapper tagMapper;

    @Override
    public Tag findById(Long id) {
        return tagMapper.selectById(id);
    }

    @Override
    public Tag findByName(String name) {
        return tagMapper.selectByName(name);
    }

    @Override
    public List<Tag> findAll() {
        return tagMapper.selectAll();
    }

    @Override
    public List<Tag> findAllForWeb(Boolean includePrivateAll, String restrictedTagName) {
        return tagMapper.selectAllForWeb(includePrivateAll, restrictedTagName);
    }

    @Override
    public List<Tag> findByArticleId(Long articleId) {
        return tagMapper.selectByArticleId(articleId);
    }

    @Override
    public void save(Tag tag) {
        tagMapper.insert(tag);
    }

    @Override
    public void update(Tag tag) {
        tagMapper.update(tag);
    }

    @Override
    public void deleteById(Long id) {
        tagMapper.deleteById(id);
    }
}
