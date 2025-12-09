package com.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class BookingOrder {
    private Integer id;
    private Integer userId;
    private Integer venueId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createTime;
    private String venueName;
    private String venueImageUrl;
}