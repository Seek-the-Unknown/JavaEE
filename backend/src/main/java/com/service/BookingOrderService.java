// backend/src/main/java/com/service/BookingOrderService.java

package com.service;

import com.entity.BookingOrder;
import java.time.LocalDate;
import java.util.List;

public interface BookingOrderService {

    // 单个预约
    BookingOrder createBooking(BookingOrder order);

    // ======== 新增：批量预约 ========
    List<BookingOrder> createBatchBookings(List<BookingOrder> orders);

    List<BookingOrder> listOrdersByUserId(Integer userId);

    List<BookingOrder> getBookingsByVenueAndDate(Integer venueId, LocalDate bookingDate);

    boolean cancelBooking(Integer orderId, Integer userId);
}