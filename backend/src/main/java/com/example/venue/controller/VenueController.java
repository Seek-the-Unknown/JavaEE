package com.example.venue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.venue.controller.Result;
import com.example.venue.model.User;
import com.example.venue.model.Venue;
import com.example.venue.service.UserService;
import com.example.venue.service.VenueService;
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

    // --- 公开接口 (不需要 token) ---

    // 1. 场馆列表查询 (支持模糊搜索)
    @GetMapping("/venues")
    public Result list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String region,
                       @RequestParam(required = false) String type) {
        QueryWrapper<Venue> qw = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) qw.like("name", name);
        if (region != null && !region.isEmpty()) qw.likeRight("region", region); // likeRight 是 'xxx%'
        if (type != null && !type.isEmpty() && !"全部".equals(type)) qw.eq("type", type);

        // 只展示状态正常的场馆 (假设 1 为上架状态)
        qw.eq("status", 1);
        // 按 ID 倒序，新的在前面
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

        // 返回房东信息 (脱敏处理，只返回 ID 和 用户名)
        if(owner != null) {
            Map<String, Object> ownerInfo = new HashMap<>();
            ownerInfo.put("id", owner.getId());
            ownerInfo.put("username", owner.getUsername());
            map.put("owner", ownerInfo);
        }
        return Result.success(map);
    }

    // --- 需要登录的接口 (userId 由拦截器注入) ---

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
        venue.setOwnerId(userId); // 强制绑定当前登录用户为房东
        venue.setStatus(1);       // 默认上架
        venueService.save(venue);
        return Result.success("发布成功");
    }

    // 5. 修改场馆 (补齐逻辑)
    @PostMapping("/venue/update")
    public Result update(@RequestBody Venue venue, @RequestAttribute("currentUserId") Long userId) {
        // 先查询数据库里的旧数据
        Venue dbVenue = venueService.getById(venue.getId());

        if (dbVenue == null) {
            return Result.error("场馆不存在");
        }

        // ★★★ 权限校验：必须是房东本人才能修改
        if (!dbVenue.getOwnerId().equals(userId)) {
            return Result.error("无权修改此场馆");
        }

        // 防止前端恶意修改 ownerId，强制设回原房东 (或者不处理 ownerId 字段)
        venue.setOwnerId(userId);

        // 执行更新 (MyBatis-Plus 会根据 id 更新非空字段)
        venueService.updateById(venue);
        return Result.success("修改成功");
    }

    // 6. 删除场馆 (补齐逻辑)
    @DeleteMapping("/venue/{id}")
    public Result delete(@PathVariable Long id, @RequestAttribute("currentUserId") Long userId) {
        Venue venue = venueService.getById(id);

        if (venue == null) {
            return Result.error("场馆不存在");
        }

        // ★★★ 权限校验：必须是房东本人才能删除
        if (!venue.getOwnerId().equals(userId)) {
            return Result.error("无权删除此场馆");
        }

        venueService.removeById(id);
        return Result.success("删除成功");
    }
}