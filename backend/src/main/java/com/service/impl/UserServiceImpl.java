package com.service.impl;

import com.entity.User;
import com.mapper.UserMapper;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public User register(User user) throws Exception {
        // 1. 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new Exception("用户名 '" + user.getUsername() + "' 已被注册！");
        }

        // 2. 对密码进行加密处理 (重要！)
        // 在实际项目中，绝不能明文存储密码。这里为了简化，暂时使用明文。
        // 推荐使用Spring Security的BCryptPasswordEncoder。
        // String encodedPassword = passwordEncoder.encode(user.getPassword());
        // user.setPassword(encodedPassword);

        // 3. 插入数据库
        user.setIsAdmin(0); // 默认注册为普通用户
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            // 用户不存在
            return null;
        }

        // 2. 验证密码
        // 同样，这里是简化的明文比对。实际项目需要用加密器进行匹配。
        // if (passwordEncoder.matches(password, user.getPassword())) { ... }
        if (user.getPassword().equals(password)) {
            // 登录成功
            return user;
        }

        // 密码错误
        return null;
    }
}