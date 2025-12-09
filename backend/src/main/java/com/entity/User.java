package com.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok注解，自动生成getter, setter, toString等方法
public class User {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Integer isAdmin;
    private LocalDateTime createTime;
}