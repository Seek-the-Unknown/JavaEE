package com.mapper;

import com.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 新增用户 (用于注册)
     * @param user 用户对象
     * @return 影响的行数
     */
    int insert(User user);

    User findById(Integer id);
}