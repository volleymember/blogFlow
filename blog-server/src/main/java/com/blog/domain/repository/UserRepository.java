package com.blog.domain.repository;

import com.blog.domain.entity.User;

import java.util.List;

/**
 * 用户仓储接口
 */
public interface UserRepository {

    User findById(Long id);

    User findByUsername(String username);

    List<User> findAll(String keyword);

    void save(User user);

    void update(User user);

    void deleteById(Long id);

    String findRoleKeyByUserId(Long userId);

    Long findRoleIdByKey(String roleKey);

    void assignRole(Long userId, Long roleId);
}
