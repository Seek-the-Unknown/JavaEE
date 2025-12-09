package com.service.impl;

import com.entity.User;
import com.entity.Venue;
import com.mapper.UserMapper;
import com.mapper.VenueMapper;
import com.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    @Autowired
    private VenueMapper venueMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Venue> listAllVenues(String name) {
        return venueMapper.findAll(name);
    }

    @Override
    public List<Venue> listVenuesByOwner(Integer ownerId) {
        return venueMapper.findByOwnerId(ownerId);
    }

    @Override
    public Venue getVenueById(Integer id) {
        return venueMapper.findById(id);
    }

    @Transactional
    @Override
    public Venue addNewVenue(Venue venue) {
        venue.setIsActive(1);
        venueMapper.insert(venue);
        return venue;
    }

    @Transactional
    @Override
    public Venue updateVenue(Venue venue, Integer operatorId) {
        Venue dbVenue = venueMapper.findById(venue.getId());
        if (dbVenue == null) throw new RuntimeException("场馆不存在");

        checkPermission(dbVenue, operatorId); // 鉴权

        venue.setOwnerId(dbVenue.getOwnerId()); // 防止篡改owner
        venueMapper.update(venue);
        return venue;
    }

    @Transactional
    @Override
    public void deleteVenueById(Integer id, Integer operatorId) {
        Venue dbVenue = venueMapper.findById(id);
        if (dbVenue != null) {
            checkPermission(dbVenue, operatorId); // 鉴权
            venueMapper.deleteById(id);
        }
    }

    private void checkPermission(Venue venue, Integer operatorId) {
        User user = userMapper.findById(operatorId);
        boolean isAdmin = user.getIsAdmin() == 1;
        boolean isOwner = venue.getOwnerId() != null && venue.getOwnerId().equals(operatorId);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("无权操作此场馆");
        }
    }
}