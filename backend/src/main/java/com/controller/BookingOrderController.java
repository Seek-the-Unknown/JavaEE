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

    // POST /api/orders - 创建新订单
    @PostMapping
    public Result<BookingOrder> createOrder(@RequestBody BookingOrder order) {
        // 在实际项目中，userId 应该从当前登录的用户Session或Token中获取，而不是由前端传递
        // 为了简化，我们暂时相信前端传递的userId
        BookingOrder newOrder = bookingOrderService.createBooking(order);
        return Result.success("创建订单成功", newOrder);
    }

    // GET /api/orders/user/{userId} - 获取某个用户的所有订单
    @GetMapping("/user/{userId}")
    public Result<List<BookingOrder>> getOrdersByUserId(@PathVariable Integer userId) {
        List<BookingOrder> orders = bookingOrderService.listOrdersByUserId(userId);
        return Result.success("查询用户订单成功", orders);
    }

    /**
     * ======== 新增接口 ========
     * 获取指定场馆在特定日期的已预约时间段
     */
    @Operation(summary = "获取场馆某日的预约时段")
    @GetMapping("/venue/{venueId}")
    public Result<List<BookingOrder>> getBookingsForDate(
            @PathVariable Integer venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingOrder> bookings = bookingOrderService.getBookingsByVenueAndDate(venueId, date);
        return Result.success(bookings);
    }

    @Operation(summary = "用户取消自己的订单")
    @PutMapping("/{orderId}/cancel")
    public Result<?> cancelOrder(@PathVariable Integer orderId) {
        // 同样，userId 应该从Token中解析，这里我们简化，从请求头获取
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