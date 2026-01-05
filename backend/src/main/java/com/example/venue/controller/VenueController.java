package com.example.venue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.venue.model.Result;
import com.example.venue.model.User; //
import com.example.venue.model.Venue; //
import com.example.venue.service.UserService; //
import com.example.venue.service.VenueService; //
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class VenueController {

    @Autowired private VenueService venueService;
    @Autowired private UserService userService;

    // ... (list 和 getDetail 方法保持不变，此处省略以节省篇幅) ...
    // 1. 场馆列表查询
    @GetMapping("/venues")
    public Result list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String region,
                       @RequestParam(required = false) String type) {
        QueryWrapper<Venue> qw = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) qw.like("name", name);
        if (region != null && !region.isEmpty()) qw.likeRight("region", region);
        if (type != null && !type.isEmpty() && !"全部".equals(type)) qw.eq("type", type);
        qw.eq("status", 1);
        qw.orderByDesc("id");
        return Result.success(venueService.list(qw));
    }

    // 2. 场馆详情
    @GetMapping("/venue/{id}")
    public Result getDetail(@PathVariable Long id) {
        Venue venue = venueService.getById(id);
        if (venue == null) return Result.error("场馆不存在");
        User owner = userService.getById(venue.getOwnerId());
        Map<String, Object> map = new HashMap<>();
        map.put("venue", venue);
        if(owner != null) {
            Map<String, Object> ownerInfo = new HashMap<>();
            ownerInfo.put("id", owner.getId());
            ownerInfo.put("username", owner.getUsername());
            map.put("owner", ownerInfo);
        }
        return Result.success(map);
    }

    // --- 需要登录的接口 ---

    // 3. 我发布的场馆
    @GetMapping("/my-venues")
    public Result myVenues(@RequestAttribute("currentUserId") Long userId) {
        QueryWrapper<Venue> qw = new QueryWrapper<>();
        qw.eq("owner_id", userId);
        qw.orderByDesc("id");
        return Result.success(venueService.list(qw));
    }

    // 4. 发布场馆
    @PostMapping("/venue/add")
    public Result add(@RequestBody Venue venue, @RequestAttribute("currentUserId") Long userId) {
        venue.setOwnerId(userId);
        venue.setStatus(1);
        venueService.save(venue);
        return Result.success("发布成功");
    }

    // 5. 修改场馆
    @PostMapping("/venue/update")
    public Result update(@RequestBody Venue venue, @RequestAttribute("currentUserId") Long userId) {
        Venue dbVenue = venueService.getById(venue.getId());
        if (dbVenue == null) return Result.error("场馆不存在");

        // 这里的修改逻辑保持不变：通常只允许房东修改内容
        // 如果你也希望管理员能修改场馆信息，可以参考下面的 delete 逻辑加上管理员判断
        if (!dbVenue.getOwnerId().equals(userId)) {
            return Result.error("无权修改此场馆");
        }

        venue.setOwnerId(userId);
        venueService.updateById(venue);
        return Result.success("修改成功");
    }

    // 6. 删除场馆 (★★★ 修复了这里 ★★★)
    @DeleteMapping("/venue/{id}")
    public Result delete(@PathVariable Long id, @RequestAttribute("currentUserId") Long userId) {
        Venue venue = venueService.getById(id);
        if (venue == null) return Result.error("场馆不存在");

        // 获取当前操作用户的信息，查看其角色
        User currentUser = userService.getById(userId);

        // 权限校验：
        // 允许删除的条件：(是房东本人) OR (是管理员)
        boolean isOwner = venue.getOwnerId().equals(userId);
        boolean isAdmin = currentUser != null && "admin".equals(currentUser.getRole());

        if (!isOwner && !isAdmin) {
            return Result.error("无权删除此场馆");
        }

        venueService.removeById(id);
        return Result.success("删除成功");
    }
}