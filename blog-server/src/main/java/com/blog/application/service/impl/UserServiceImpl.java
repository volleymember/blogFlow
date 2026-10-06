package com.blog.application.service.impl;

import com.blog.application.service.UserService;
import com.blog.domain.common.BusinessException;
import com.blog.domain.common.Constants;
import com.blog.domain.entity.User;
import com.blog.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getUserList(String keyword) {
        return userRepository.findAll(keyword);
    }

    @Override
    @Transactional
    public void createUser(User user) {
        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        if (user.getStatus() == null) {
            user.setStatus(Constants.USER_STATUS_NORMAL);
        }
        Long roleId = resolveRoleId(user.getRoleKey());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        userRepository.assignRole(user.getId(), roleId);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existing = userRepository.findById(user.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        // 如果修改了密码，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        userRepository.update(user);

        if (user.getRoleKey() != null && !user.getRoleKey().isBlank()) {
            userRepository.assignRole(user.getId(), resolveRoleId(user.getRoleKey()));
        }
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    private Long resolveRoleId(String roleKey) {
        String normalizedRole = (roleKey == null || roleKey.isBlank()) ? Constants.ROLE_USER : roleKey;
        if (!Objects.equals(normalizedRole, Constants.ROLE_ADMIN)
                && !Objects.equals(normalizedRole, Constants.ROLE_USER)) {
            throw new BusinessException("不支持的用户权限");
        }

        Long roleId = userRepository.findRoleIdByKey(normalizedRole);
        if (roleId == null) {
            throw new BusinessException("用户权限未初始化");
        }
        return roleId;
    }
}
