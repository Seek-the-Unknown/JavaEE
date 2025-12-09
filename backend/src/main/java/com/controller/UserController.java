package com.controller;

import com.entity.User;
import com.service.UserService;
import com.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // POST /api/users/register - 用户注册
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            // 注册成功后，不应该返回用户的密码等敏感信息
            registeredUser.setPassword(null);
            return Result.success("注册成功", registeredUser);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    // POST /api/users/login - 用户登录
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userService.login(username, password);

        if (user != null) {
            // 登录成功，同样不返回密码
            user.setPassword(null);
            return Result.success("登录成功", user);
        } else {
            return Result.error(401, "用户名或密码错误");
        }
    }
}