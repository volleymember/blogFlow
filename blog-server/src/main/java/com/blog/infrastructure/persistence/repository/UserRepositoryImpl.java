package com.blog.infrastructure.persistence.repository;

import com.blog.domain.entity.User;
import com.blog.domain.repository.UserRepository;
import com.blog.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户仓储实现
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> findAll(String keyword) {
        return userMapper.selectList(keyword);
    }

    @Override
    public void save(User user) {
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public String findRoleKeyByUserId(Long userId) {
        return userMapper.selectRoleKeyByUserId(userId);
    }

    @Override
    public Long findRoleIdByKey(String roleKey) {
        return userMapper.selectRoleIdByKey(roleKey);
    }

    @Override
    public void assignRole(Long userId, Long roleId) {
        userMapper.deleteUserRoles(userId);
        userMapper.insertUserRole(userId, roleId);
    }
}
