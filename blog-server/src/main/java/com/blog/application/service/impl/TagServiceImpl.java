package com.blog.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.blog.application.service.TagService;
import com.blog.domain.common.BusinessException;
import com.blog.domain.common.Constants;
import com.blog.domain.entity.Tag;
import com.blog.domain.repository.TagRepository;
import com.blog.domain.repository.UserRepository;
import com.blog.infrastructure.persistence.mapper.ArticleTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务实现
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ArticleTagMapper articleTagMapper;
    private final UserRepository userRepository;

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> getWebTags(Long viewerId) {
        boolean admin = isAdmin(viewerId);
        return tagRepository.findAllForWeb(admin, admin ? null : Constants.TAG_MOMENTS);
    }

    @Override
    public Tag getTagById(Long id) {
        Tag tag = tagRepository.findById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return tag;
    }

    @Override
    public void createTag(Tag tag) {
        Tag existing = tagRepository.findByName(tag.getName());
        if (existing != null) {
            throw new BusinessException("标签名称已存在");
        }
        if (StrUtil.isBlank(tag.getSlug())) {
            tag.setSlug(cn.hutool.core.util.IdUtil.simpleUUID().substring(0, 8));
        }
        tagRepository.save(tag);
    }

    @Override
    public void updateTag(Tag tag) {
        Tag existing = tagRepository.findById(tag.getId());
        if (existing == null) {
            throw new BusinessException("标签不存在");
        }
        tagRepository.update(tag);
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        // 删除标签关联
        articleTagMapper.deleteByArticleId(id);
        tagRepository.deleteById(id);
    }

    private boolean isAdmin(Long userId) {
        return userId != null && Constants.ROLE_ADMIN.equals(userRepository.findRoleKeyByUserId(userId));
    }
}
