package com.mapper;

import com.entity.BookingOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface BookingOrderMapper {

    /**
     * 新增订单
     * @param bookingOrder 订单对象
     * @return 影响的行数
     */
    int insert(BookingOrder bookingOrder);

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<BookingOrder> findByUserId(Integer userId);

    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单对象
     */
    BookingOrder findById(Integer id);

    /**
     * ======== 新增方法 ========
     * 检查在指定时间段内是否存在已成功的订单
     * @param venueId 场馆ID
     * @param bookingDate 预约日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 冲突订单的数量
     */
    int countOverlappingBookings(@Param("venueId") Integer venueId,
                                 @Param("bookingDate") LocalDate bookingDate,
                                 @Param("startTime") LocalTime startTime,
                                 @Param("endTime") LocalTime endTime);



    /**
     * ======== 新增方法 ========
     * 查询指定场馆在特定日期的所有成功预约
     */
    List<BookingOrder> findBookingsByVenueAndDate(@Param("venueId") Integer venueId,
                                                  @Param("bookingDate") LocalDate bookingDate);



        /** ======== 新增方法 ======== */
        int updateStatus(@Param("orderId") Integer orderId, @Param("userId") Integer userId, @Param("status") Integer status);



}