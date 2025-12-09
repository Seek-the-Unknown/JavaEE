package com.service.impl;

import com.entity.BookingOrder;
import com.entity.Venue;
import com.mapper.BookingOrderMapper;
import com.mapper.VenueMapper;
import com.service.BookingOrderService;
import com.utils.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingOrderServiceImpl implements BookingOrderService {

    @Autowired
    private BookingOrderMapper bookingOrderMapper;

    @Autowired
    private VenueMapper venueMapper;

    @Autowired
    private RedisLock redisLock;

    @Transactional
    @Override
    public BookingOrder createBooking(BookingOrder order) {
        String lockKey = String.format("lock:venue:%d:%s:%s-%s",
                order.getVenueId(),
                order.getBookingDate().toString(),
                order.getStartTime().toString(),
                order.getEndTime().toString());

        String requestId = UUID.randomUUID().toString();

        if (!redisLock.tryLock(lockKey, requestId, 10)) {
            throw new RuntimeException("操作频繁，请稍后再试！");
        }

        try {
            // ============ 核心修改：数据库级别的双重检查 ============
            int conflictCount = bookingOrderMapper.countOverlappingBookings(
                    order.getVenueId(),
                    order.getBookingDate(),
                    order.getStartTime(),
                    order.getEndTime());

            if (conflictCount > 0) {
                // 如果数据库中已存在冲突订单，则直接抛出异常
                throw new RuntimeException("预订失败：该时间段已被占用。");
            }
            // ======================================================

            Venue venue = venueMapper.findById(order.getVenueId());
            if (venue == null) {
                throw new RuntimeException("预订失败：场馆不存在！");
            }

            long hours = ChronoUnit.HOURS.between(order.getStartTime(), order.getEndTime());
            if (hours <= 0) {
                throw new RuntimeException("预订失败：时间不合法！");
            }
            BigDecimal totalAmount = venue.getPricePerHour().multiply(new BigDecimal(hours));

            order.setTotalAmount(totalAmount);
            order.setStatus(1); // 预约成功
            bookingOrderMapper.insert(order);

            return order;
        } finally {
            redisLock.releaseLock(lockKey, requestId);
        }
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
        // 1. 先查询订单，确保它存在且处于可取消的状态
        BookingOrder order = bookingOrderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            // 订单不存在或不属于该用户
            return false;
        }

        // 2. 只有 "预约成功"(1) 或 "待支付"(0) 的订单可以被取消
        if (order.getStatus() == 1 || order.getStatus() == 0) {
            int updatedRows = bookingOrderMapper.updateStatus(orderId, userId, 3); // 3 代表 "已取消"

            if (updatedRows > 0) {
                // 如果取消的是一个已经成功的预约，需要释放Redis锁，让别人可以重新预订
                // 注意：这是一个简化的逻辑。更严谨的系统会在订单支付成功后才持久化Redis锁，并在取消时删除。
                // 我们目前的锁是临时的，所以这里主要是为了演示逻辑。
                String lockKey = String.format("lock:venue:%d:%s:%s-%s",
                        order.getVenueId(),
                        order.getBookingDate().toString(),
                        order.getStartTime().toString(),
                        order.getEndTime().toString());
                // 这里我们假设 requestID 不再重要，直接删除 key
                // redisLock.releaseLock(lockKey, "any_id_since_we_force_delete"); // 假设有这样的方法
                // 更简单的方式是直接操作 stringRedisTemplate
                // stringRedisTemplate.delete(lockKey); // 需要注入 stringRedisTemplate

                return true;
            }
        }

        // 订单状态不可取消
        return false;
    }
}