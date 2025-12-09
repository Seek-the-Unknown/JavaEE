package com.controller;

import com.entity.User;
import com.mapper.UserMapper;
import com.service.UserService;
import com.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        User loggedUser = userService.login(user.getUsername(), user.getPassword());
        return Result.success("登录成功", loggedUser);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) throws Exception {
        User newUser = userService.register(user);
        return Result.success("注册成功", newUser);
    }

    // 新增：获取当前用户信息（含最新余额）
    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uid = req.getHeader("userId");
        if (uid == null) return Result.error(401, "未登录");
        User user = userMapper.findById(Integer.parseInt(uid));
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }
}