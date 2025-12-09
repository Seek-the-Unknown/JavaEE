package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 允许所有路径
                        .allowedOriginPatterns("*") // 允许所有来源（在开发阶段可以用*，生产环境建议指定前端域名）
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                        .allowCredentials(true) // 允许携带Cookie
                        .maxAge(3600); // 预检请求的有效期
            }
        };
    }
}