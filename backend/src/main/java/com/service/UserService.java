package com.service;

import com.entity.User;

public interface UserService {

    /**
     * 用户注册
     * @param user 包含用户名和密码的用户对象
     * @return 注册成功的用户对象
     * @throws Exception 如果用户名已存在则抛出异常
     */
    User register(User user) throws Exception;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象，如果失败则返回null
     */
    User login(String username, String password);
}