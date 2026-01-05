package com.example.venue.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.venue.model.User;
import com.example.venue.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    // --- 登录逻辑 ---
    public User login(String username, String password) {
        // 1. 先只根据用户名查出用户
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", username);
        User user = getOne(qw);

        // 2. 如果用户不存在，直接返回 null
        if (user == null) {
            return null;
        }

        // 3. 关键步骤：校验密码
        // BCrypt.checkpw(用户输入的明文, 数据库里的密文)
        // 注意：如果你之前有旧用户是明文存储的，这里会报错或匹配失败。
        // 建议清空旧用户数据，或者加个判断：if(password.equals(user.getPassword()))
        if (BCrypt.checkpw(password, user.getPassword())) {
            return user; // 密码正确
        } else {
            return null; // 密码错误
        }
    }

    // --- 注册逻辑 ---
    public void register(User user) {
        // 1. 检查用户名是否已存在
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", user.getUsername());
        if (count(qw) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 关键步骤：加密密码 (加盐是自动的)
        // hashpw 会生成一个带盐的哈希值，例如：$2a$10$......
        String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(passwordHash);

        // 3. 设置默认余额等
        if (user.getBalance() == null) {
            user.setBalance(new java.math.BigDecimal("0.00"));
        }
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        // 4. 保存到数据库
        save(user);
    }
}