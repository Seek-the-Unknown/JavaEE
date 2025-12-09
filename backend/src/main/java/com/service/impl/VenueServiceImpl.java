package com.service.impl;

import com.entity.Venue;
import com.mapper.VenueMapper;
import com.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service 注解，告诉Spring这是一个服务类，需要被Spring容器管理
@Service
public class VenueServiceImpl implements VenueService {

    // 使用 @Autowired 进行依赖注入，Spring会自动将VenueMapper的实例注入进来
    @Autowired
    private VenueMapper venueMapper;

    @Override
    public List<Venue> listAllVenues(String name) {
        // 直接调用Mapper的方法
        return venueMapper.findAll(name);
    }

    @Override
    public Venue getVenueById(Integer id) {
        return venueMapper.findById(id);
    }

    // @Transactional 注解表示这个方法是一个事务。如果方法执行过程中出现异常，所有数据库操作都会回滚。
    @Transactional
    @Override
    public Venue addNewVenue(Venue venue) {
        venueMapper.insert(venue);
        // 因为Mapper的insert方法配置了useGeneratedKeys，此时venue对象中的id已经被赋值
        return venue;
    }

    @Transactional
    @Override
    public Venue updateVenue(Venue venue) {
        venueMapper.update(venue);
        return venue;
    }

    @Transactional
    @Override
    public void deleteVenueById(Integer id) {
        venueMapper.deleteById(id);
    }
}