package com.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Venue {
    private Integer id;
    private String name;
    private String type;
    private String description;
    private Integer capacity;
    private BigDecimal pricePerHour; // 使用BigDecimal处理金额，更精确
    private String imageUrl;
    private Integer isActive;
    private LocalDateTime createTime;
}


