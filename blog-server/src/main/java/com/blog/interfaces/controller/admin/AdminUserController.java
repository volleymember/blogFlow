package com.blog.interfaces.controller.admin;

import com.blog.application.service.UserService;
import com.blog.domain.common.Result;
import com.blog.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 用户列表
     */
    @GetMapping
    public Result<List<User>> list(@RequestParam(required = false) String keyword) {
        return Result.success(userService.getUserList(keyword));
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    public Result<User> detail(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<Void> create(@RequestBody User user) {
        userService.createUser(user);
        return Result.success();
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
