package com.example.venue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.venue.model.Booking;
import com.example.venue.model.Venue;
import com.example.venue.service.BookingService;
import com.example.venue.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/booking") // ★★★ 确认这里是 /api/booking
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired private BookingService bookingService;
    @Autowired private VenueService venueService;

    // 1. 提交预约
    @PostMapping("/create")
    public Result create(@RequestBody Booking booking, @RequestAttribute("currentUserId") Long userId) {
        // 校验场馆是否存在
        Venue venue = venueService.getById(booking.getVenueId());
        if (venue == null) return Result.error("场馆不存在");

        booking.setUserId(userId);
        booking.setOwnerId(venue.getOwnerId());
        booking.setStatus(1); // 1=已支付/已预定
        booking.setCreateTime(LocalDateTime.now());

        // 保存订单
        bookingService.save(booking);
        return Result.success("预约成功");
    }

    // 2. 我的订单列表 (解决“订单没加载”的问题)
    @GetMapping("/my")
    public Result myBookings(@RequestAttribute("currentUserId") Long userId) {
        QueryWrapper<Booking> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.orderByDesc("create_time"); // 按时间倒序
        return Result.success(bookingService.list(qw));
    }

    // 3. 取消订单
    @PostMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        Booking booking = bookingService.getById(id);
        if(booking == null) return Result.error("订单不存在");

        booking.setStatus(2); // 2=已取消
        bookingService.updateById(booking);
        return Result.success("取消成功");
    }
}