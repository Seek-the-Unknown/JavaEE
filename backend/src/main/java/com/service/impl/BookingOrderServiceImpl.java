package com.service.impl;

import com.entity.BookingOrder;
import com.entity.User;
import com.entity.Venue;
import com.mapper.BookingOrderMapper;
import com.mapper.UserMapper;
import com.mapper.VenueMapper;
import com.service.BookingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingOrderServiceImpl implements BookingOrderService {

    @Autowired
    private BookingOrderMapper bookingOrderMapper;
    @Autowired
    private VenueMapper venueMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public BookingOrder createBooking(BookingOrder order) {
        return processOrder(order);
    }

    @Transactional
    @Override
    public List<BookingOrder> createBatchBookings(List<BookingOrder> orders) {
        synchronized (this) {
            List<BookingOrder> results = new ArrayList<>();
            for (BookingOrder order : orders) {
                results.add(processOrder(order));
            }
            return results;
        }
    }

    // 统一处理订单逻辑：检查冲突 -> 计算价格 -> 扣减余额 -> 保存
    private BookingOrder processOrder(BookingOrder order) {
        Venue venue = venueMapper.findById(order.getVenueId());
        if (venue == null) throw new RuntimeException("场馆不存在");

        // 1. 检查时间冲突
        int conflict = bookingOrderMapper.countOverlappingBookings(
                order.getVenueId(), order.getBookingDate(),
                order.getStartTime(), order.getEndTime());
        if (conflict > 0) {
            throw new RuntimeException(order.getStartTime() + " 时段已被占用");
        }

        // 2. 计算金额
        long hours = ChronoUnit.HOURS.between(order.getStartTime(), order.getEndTime());
        if (hours <= 0) throw new RuntimeException("时间无效");
        BigDecimal amount = venue.getPricePerHour().multiply(new BigDecimal(hours));

        // 3. 扣减余额
        User user = userMapper.findById(order.getUserId());
        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足！需支付: " + amount + "，当前: " + user.getBalance());
        }
        user.setBalance(user.getBalance().subtract(amount));
        userMapper.update(user);

        // 4. 保存订单
        order.setTotalAmount(amount);
        order.setStatus(1);
        bookingOrderMapper.insert(order);
        return order;
    }

    @Override
    public List<BookingOrder> listOrdersByUserId(Integer userId) {
        return bookingOrderMapper.findByUserId(userId);
    }

    @Override
    public List<BookingOrder> getBookingsByVenueAndDate(Integer venueId, LocalDate bookingDate) {
        return bookingOrderMapper.findBookingsByVenueAndDate(venueId, bookingDate);
    }

    @Transactional
    @Override
    public boolean cancelBooking(Integer orderId, Integer userId) {
        BookingOrder order = bookingOrderMapper.findById(orderId);
        if (order != null && order.getUserId().equals(userId) && order.getStatus() == 1) {
            // 这里为了简化，取消暂不退款，实际项目应退款
            return bookingOrderMapper.updateStatus(orderId, userId, 3) > 0;
        }
        return false;
    }
}