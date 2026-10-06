package com.blog.application.service.impl;

import com.blog.application.service.AuthService;
import com.blog.domain.common.BusinessException;
import com.blog.domain.common.Constants;
import com.blog.domain.entity.User;
import com.blog.domain.repository.UserRepository;
import com.blog.infrastructure.security.JwtUtils;
import com.blog.interfaces.dto.request.LoginRequest;
import com.blog.interfaces.dto.request.RegisterRequest;
import com.blog.interfaces.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        String roleKey = userRepository.findRoleKeyByUserId(user.getId());

        return new LoginResponse(token, user.getUsername(), user.getNickname(), roleKey);
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        User existing = userRepository.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        Long roleId = userRepository.findRoleIdByKey(Constants.ROLE_USER);
        if (roleId == null) {
            throw new BusinessException("普通用户角色未初始化");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null && !request.getNickname().isBlank()
                ? request.getNickname()
                : request.getUsername());
        user.setEmail(request.getEmail());
        user.setStatus(Constants.USER_STATUS_NORMAL);

        userRepository.save(user);
        userRepository.assignRole(user.getId(), roleId);

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getNickname(), Constants.ROLE_USER);
    }

    @Override
    public void logout() {
        // JWT 无状态，服务端无需处理
    }
}
