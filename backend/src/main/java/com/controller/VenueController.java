package com.controller;

import com.entity.Venue;
import com.service.VenueService;
import com.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    private Integer getUserId() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uid = req.getHeader("userId");
        return uid != null ? Integer.parseInt(uid) : null;
    }

    @GetMapping
    public Result<List<Venue>> list(@RequestParam(required = false) String name) {
        return Result.success(venueService.listAllVenues(name));
    }

    // 新增：查看我发布的
    @GetMapping("/my")
    public Result<List<Venue>> listMy() {
        Integer uid = getUserId();
        if (uid == null) return Result.error(401, "未登录");
        return Result.success(venueService.listVenuesByOwner(uid));
    }

    @GetMapping("/{id}")
    public Result<Venue> get(@PathVariable Integer id) {
        return Result.success(venueService.getVenueById(id));
    }

    @PostMapping
    public Result<Venue> add(@RequestBody Venue venue) {
        Integer uid = getUserId();
        if (uid == null) return Result.error(401, "未登录");
        venue.setOwnerId(uid); // 设置主人
        return Result.success(venueService.addNewVenue(venue));
    }

    @PutMapping
    public Result<Venue> update(@RequestBody Venue venue) {
        try {
            return Result.success(venueService.updateVenue(venue, getUserId()));
        } catch (RuntimeException e) {
            return Result.error(403, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        try {
            venueService.deleteVenueById(id, getUserId());
            return Result.success("已删除", null);
        } catch (RuntimeException e) {
            return Result.error(403, e.getMessage());
        }
    }
}