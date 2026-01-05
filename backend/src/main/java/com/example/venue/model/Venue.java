package com.example.venue.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("venue")
public class Venue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private String name;
    private String type;

    // ★★★ 必须加上这一行，否则后端无法读写地区数据！ ★★★
    private String region;

    private BigDecimal price;
    private Integer capacity;
    private String description;
    private String cover;
    private Integer status;
}