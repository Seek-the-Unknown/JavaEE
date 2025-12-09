// backend/src/main/java/com/controller/BookingOrderController.java

package com.controller;

import com.entity.BookingOrder;
import com.service.BookingOrderService;
import com.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class BookingOrderController {

    @Autowired
    private BookingOrderService bookingOrderService;

    // 单个预约 (保持不变)
    @PostMapping
    public Result<BookingOrder> createOrder(@RequestBody BookingOrder order) {
        BookingOrder newOrder = bookingOrderService.createBooking(order);
        return Result.success("预约成功", newOrder);
    }

    // ======== 新增：批量预约接口 ========
    @Operation(summary = "批量创建订单")
    @PostMapping("/batch")
    public Result<List<BookingOrder>> createBatchOrders(@RequestBody List<BookingOrder> orders) {
        List<BookingOrder> newOrders = bookingOrderService.createBatchBookings(orders);
        return Result.success("批量预约成功", newOrders);
    }

    // 获取用户订单 (保持不变)
    @GetMapping("/user/{userId}")
    public Result<List<BookingOrder>> getOrdersByUserId(@PathVariable Integer userId) {
        List<BookingOrder> orders = bookingOrderService.listOrdersByUserId(userId);
        return Result.success("查询用户订单成功", orders);
    }

    // 获取场馆占用情况 (保持不变)
    @Operation(summary = "获取场馆某日的预约时段")
    @GetMapping("/venue/{venueId}")
    public Result<List<BookingOrder>> getBookingsForDate(
            @PathVariable Integer venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingOrder> bookings = bookingOrderService.getBookingsByVenueAndDate(venueId, date);
        return Result.success(bookings);
    }

    // 取消订单 (保持不变)
    @Operation(summary = "用户取消自己的订单")
    @PutMapping("/{orderId}/cancel")
    public Result<?> cancelOrder(@PathVariable Integer orderId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        Integer userId = Integer.parseInt(userIdStr);

        boolean success = bookingOrderService.cancelBooking(orderId, userId);
        if (success) {
            return Result.success("订单取消成功", null);
        } else {
            return Result.error(400, "订单取消失败，可能订单不存在或状态不允许取消");
        }
    }
}