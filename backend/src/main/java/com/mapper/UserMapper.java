package com.mapper;

import com.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    User findById(Integer id);
    int insert(User user);
    int update(User user); // 新增
}