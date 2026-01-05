package com.example.venue.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate redisTemplate;

    public LoginInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 请求 (解决跨域预检问题)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从 Header 获取 Token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            response.setStatus(401); // 401 未授权
            return false;
        }

        // 3. 去 Redis 验证 Token
        String key = "login:token:" + token;
        String userIdStr = redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            response.setStatus(401); // Token 过期或无效
            return false;
        }

        // 4. ★★★ 关键步骤：将 userId 存入 Request 域
        // 这样后续的 Controller 不用再查一遍，直接用 @RequestAttribute 取
        request.setAttribute("currentUserId", Long.parseLong(userIdStr));

        // 5. 自动续期 (只要用户在操作，就重置为 2 小时)
        redisTemplate.expire(key, 2, TimeUnit.HOURS);

        return true;
    }
}