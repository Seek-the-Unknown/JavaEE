package com.service;

import com.entity.Venue;
import java.util.List;

public interface VenueService {

    /**
     * 获取所有场馆的列表
     * @return 场馆列表
     */
    List<Venue> listAllVenues(String name);

    /**
     * 根据ID获取场馆详情
     * @param id 场馆ID
     * @return 场馆对象
     */
    Venue getVenueById(Integer id);

    /**
     * (管理员) 新增一个场馆
     * @param venue 场馆对象
     * @return 新增的场馆对象 (包含生成的主键ID)
     */
    Venue addNewVenue(Venue venue);

    /**
     * (管理员) 更新场馆信息
     * @param venue 场馆对象
     * @return 更新后的场馆对象
     */
    Venue updateVenue(Venue venue);

    /**
     * (管理员) 删除一个场馆
     * @param id 场馆ID
     */
    void deleteVenueById(Integer id);
}