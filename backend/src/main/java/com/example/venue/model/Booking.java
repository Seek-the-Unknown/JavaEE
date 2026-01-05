package com.example.venue.model;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("booking")
public class Booking {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long venueId;
    private Long userId;
    private Long ownerId;

    // 改动：使用 start和end
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BigDecimal totalCost;
    private Integer status; // 1已预约 2已取消
    private LocalDateTime createTime;
}