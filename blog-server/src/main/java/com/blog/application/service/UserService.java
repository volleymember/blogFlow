package com.blog.application.service;

import com.blog.domain.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getUserList(String keyword);

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);
}
