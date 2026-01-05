package com.example.venue.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.venue.model.*;
import com.example.venue.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class BookingService extends ServiceImpl<BookingMapper, Booking> {

    @Autowired private UserMapper userMapper;
    @Autowired private VenueMapper venueMapper;

    @Transactional(rollbackFor = Exception.class)
    public void createBooking(Long userId, Long venueId, LocalDateTime start, LocalDateTime end) {
        Venue venue = venueMapper.selectById(venueId);
        if (venue == null || venue.getStatus() == 0) throw new RuntimeException("场馆不可用");

        // 1. 禁止自己租自己
        if (venue.getOwnerId().equals(userId)) {
            throw new RuntimeException("操作失败：不能预约自己发布的场馆");
        }

        // 2. 计算时长 (分钟转小时，向上取整)
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes <= 0) throw new RuntimeException("结束时间必须晚于开始时间");

        double hours = Math.ceil(minutes / 60.0);
        if(hours < 1) hours = 1;

        // 3. 计算费用
        BigDecimal cost = venue.getPrice().multiply(BigDecimal.valueOf(hours));

        // 4. 检查余额
        User renter = userMapper.selectById(userId);
        if (renter.getBalance().compareTo(cost) < 0) {
            throw new RuntimeException("余额不足！当前余额: " + renter.getBalance() + "，需要: " + cost);
        }

        // 5. 扣款 (租客)
        renter.setBalance(renter.getBalance().subtract(cost));
        userMapper.updateById(renter);

        // 6. 加钱 (房东)
        User owner = userMapper.selectById(venue.getOwnerId());
        owner.setBalance(owner.getBalance().add(cost));
        userMapper.updateById(owner);

        // 7. 保存订单
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setVenueId(venueId);
        booking.setOwnerId(venue.getOwnerId());
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setTotalCost(cost);
        booking.setStatus(1);
        booking.setCreateTime(LocalDateTime.now());
        save(booking);
    }

    // 取消预约逻辑
    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = getById(bookingId);
        if(booking == null || booking.getStatus() != 1) throw new RuntimeException("订单无法取消");

        // 只有本人能取消
        if(!booking.getUserId().equals(userId)) throw new RuntimeException("无权操作");

        User renter = userMapper.selectById(booking.getUserId());
        User owner = userMapper.selectById(booking.getOwnerId());

        // 退款
        owner.setBalance(owner.getBalance().subtract(booking.getTotalCost()));
        renter.setBalance(renter.getBalance().add(booking.getTotalCost()));

        userMapper.updateById(owner);
        userMapper.updateById(renter);

        booking.setStatus(2);
        updateById(booking);
    }
}