package com.example.venue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.venue.model.Result;
import com.example.venue.model.User;
import com.example.venue.model.Venue;
import com.example.venue.service.UserService;
import com.example.venue.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private VenueService venueService;

    // 获取所有用户
    @GetMapping("/users")
    public Result getAllUsers() {
        return Result.success(userService.list());
    }

    // 删除用户
    @DeleteMapping("/user/{id}")
    public Result deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    // 重置密码
    @PostMapping("/user/reset-password")
    public Result resetPassword(@RequestBody Map<String, Long> params) {
        Long userId = params.get("id");
        User user = userService.getById(userId);
        if(user != null) {
            user.setPassword("123456"); // 这里建议加密后再存，示例从简
            userService.updateById(user);
            return Result.success("密码已重置");
        }
        return Result.error("用户不存在");
    }

    // 获取所有场馆
    @GetMapping("/venues")
    public Result getAllVenues() {
        return Result.success(venueService.list(new QueryWrapper<Venue>().orderByDesc("id")));
    }
}