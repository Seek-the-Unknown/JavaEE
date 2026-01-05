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

    // 1. 注册拦截器：解析 Token 并注入 userId
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(redisTemplate))
                .addPathPatterns("/**") // 拦截所有接口
                .excludePathPatterns(   // 排除不需要登录的接口
                        "/api/user/login",
                        "/api/user/register",
                        "/api/venues",      // 场馆列表
                        "/api/file/upload", // 图片上传
                        "/images/**"        // 图片资源
                );
    }

    // 2. 配置静态资源映射 (图片保存路径)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 请确保这里的路径是你电脑上实际存储图片的路径
        // 如果是 Windows，格式如 "file:D:/venue_images/"
        // 如果是 Mac/Linux，格式如 "file:/Users/xxx/venue_images/"
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:F:/venue_images/");
    }
}