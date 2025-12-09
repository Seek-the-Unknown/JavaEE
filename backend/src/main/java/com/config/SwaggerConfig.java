package com.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("场馆预约系统 API 文档")
                        .version("1.0.0")
                        .description("这是一个为课程设计开发的场馆在线预约系统的后端API文档。"));
    }
}