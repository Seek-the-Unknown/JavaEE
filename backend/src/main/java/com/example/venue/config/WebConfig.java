package com.example.venue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 1. 配置静态资源映射 (保持你原来的代码)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:F:/venue_images/");
    }

    // 2. ★★★ 配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(redisTemplate))
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(   // 排除不需要登录的接口
                        "/api/user/login",      // 登录
                        "/api/user/register",   // 注册
                        "/api/venues",          // 场馆列表(公开)
                        "/api/venue/{id}",      // 场馆详情(公开)
                        "/images/**",           // 图片资源
                        "/api/file/**"          // 文件上传(如果需要公开的话)
                );
    }
}