package com.blog.infrastructure.persistence.mapper;

import com.blog.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    List<User> selectList(@Param("keyword") String keyword);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);

    String selectRoleKeyByUserId(@Param("userId") Long userId);

    Long selectRoleIdByKey(@Param("roleKey") String roleKey);

    int deleteUserRoles(@Param("userId") Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
