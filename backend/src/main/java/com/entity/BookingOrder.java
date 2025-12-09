package com.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat (pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate bookingDate;
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private LocalTime endTime;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createTime;
    private String venueName;
    private String venueImageUrl;
}