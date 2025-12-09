package com.service;

import com.entity.Venue;
import java.util.List;

public interface VenueService {
    List<Venue> listAllVenues(String name);
    List<Venue> listVenuesByOwner(Integer ownerId); // 新增
    Venue getVenueById(Integer id);
    Venue addNewVenue(Venue venue);
    Venue updateVenue(Venue venue, Integer operatorId); // 增加操作人ID鉴权
    void deleteVenueById(Integer id, Integer operatorId); // 增加操作人ID鉴权
}