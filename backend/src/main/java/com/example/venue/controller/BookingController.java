package com.example.venue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.venue.controller.Result; // 确保引入了你的Result类
import com.example.venue.model.Booking;
import com.example.venue.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/booking") // 这就是前端找不到的那个路径
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired private BookingService bookingService;

    // 1. 创建预约
    @PostMapping("/create")
    public Result create(@RequestBody Map<String, Object> params,
                         @RequestAttribute("currentUserId") Long userId) {
        try {
            Long venueId = Long.parseLong(params.get("venueId").toString());
            LocalDateTime start = LocalDateTime.parse(params.get("startTime").toString());
            LocalDateTime end = LocalDateTime.parse(params.get("endTime").toString());
            bookingService.createBooking(userId, venueId, start, end);
            return Result.success("预约成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 2. 取消预约
    @PostMapping("/cancel")
    public Result cancel(@RequestBody Map<String, Object> params,
                         @RequestAttribute("currentUserId") Long userId) {
        try {
            Long bookingId = Long.parseLong(params.get("bookingId").toString());
            bookingService.cancelBooking(bookingId, userId);
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 3. 我的预约列表 (解决 404 的关键)
    @GetMapping("/my")
    public Result myBookings(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(bookingService.list(new QueryWrapper<Booking>().eq("user_id", userId).orderByDesc("id")));
    }
}