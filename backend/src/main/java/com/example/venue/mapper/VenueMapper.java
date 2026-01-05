package com.example.venue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.venue.model.Venue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VenueMapper extends BaseMapper<Venue> {
}