package com.service;

import com.entity.BookingOrder;

import java.time.LocalDate;
import java.util.List;

public interface BookingOrderService {

    /**
     * 创建一个新的预约订单
     * @param order 订单信息
     * @return 创建成功的订单
     */
    BookingOrder createBooking(BookingOrder order);

    /**
     * 获取指定用户的所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<BookingOrder> listOrdersByUserId(Integer userId);

    List<BookingOrder> getBookingsByVenueAndDate(Integer venueId, LocalDate bookingDate);

    boolean cancelBooking(Integer orderId, Integer userId);

}