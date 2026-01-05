package com.example.venue.controller;

import com.example.venue.model.User;
import com.example.venue.service.UserService;
import com.example.venue.controller.Result; // 假设 Result 和 Controller 在同包，或者自己 import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private StringRedisTemplate redisTemplate;

    // --- 登录 (生成 Token) ---
    @PostMapping("/login")
    public Result login(@RequestBody User loginForm) {
        User user = userService.login(loginForm.getUsername(), loginForm.getPassword());
        if (user == null) {
            return Result.error("账号或密码错误");
        }

        // 1. 生成 Token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 2. 存入 Redis (Key=token, Value=userId, 2小时过期)
        redisTemplate.opsForValue().set("login:token:" + token, user.getId().toString(), 2, TimeUnit.HOURS);

        // 3. 返回 Token 和用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        user.setPassword(null); // 抹除密码
        data.put("user", user);

        return Result.success(data);
    }

    // --- 注册 ---
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // --- 退出登录 ---
    @PostMapping("/logout")
    public Result logout(@RequestHeader("token") String token) {
        redisTemplate.delete("login:token:" + token);
        return Result.success("已退出");
    }

    // --- 获取当前用户信息 (自动从 Token 拿 ID) ---
    @GetMapping("/me")
    public Result getMe(@RequestAttribute("currentUserId") Long userId) {
        User user = userService.getById(userId);
        user.setPassword(null);
        return Result.success(user);
    }

    // --- 充值 (自动从 Token 拿 ID) ---
    @PostMapping("/recharge")
    public Result recharge(@RequestBody Map<String, Object> params,
                           @RequestAttribute("currentUserId") Long userId) {
        BigDecimal amount = new BigDecimal(params.get("amount").toString());

        User user = userService.getById(userId);
        user.setBalance(user.getBalance().add(amount));
        userService.updateById(user);

        return Result.success("充值成功，当前余额: " + user.getBalance());
    }
}