package com.example.venue.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.venue.model.Venue;
import com.example.venue.mapper.VenueMapper;
import org.springframework.stereotype.Service;

@Service
public class VenueService extends ServiceImpl<VenueMapper, Venue> {
}