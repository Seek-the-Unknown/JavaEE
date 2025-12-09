package com.config;

import com.entity.User;
import com.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest; // 注意是 jakarta.*
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AdminAuthAspect {

    @Autowired
    private UserMapper userMapper; // 临时用于获取用户信息

    /**
     * 定义一个切面，拦截所有被 @AdminRequired 注解标记的方法
     */
    @Around("@annotation(com.config.AdminRequired)")
    public Object checkAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // ---- 这里的用户身份验证逻辑是【简化】的 ----
        // 在真实项目中，你应该从请求头中的Token解析出用户ID
        // 这里我们为了方便，从请求头里直接获取 'userId'
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null) {
            throw new RuntimeException("权限不足：缺少用户信息");
        }

        try {
            Integer userId = Integer.parseInt(userIdStr);
            // 这里应该从缓存（如Redis）获取用户信息，为了简化直接查库
            User user = userMapper.findById(userId); // 你需要在UserMapper中添加findById方法

            if (user == null || user.getIsAdmin() != 1) {
                throw new RuntimeException("权限不足：需要管理员身份");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("权限不足：用户ID格式错误");
        }

        // 如果权限校验通过，则继续执行原方法
        return joinPoint.proceed();
    }
}