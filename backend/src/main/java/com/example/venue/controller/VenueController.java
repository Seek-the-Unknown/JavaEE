package com.example.venue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
@CrossOrigin(origins = "*") // 允许跨域
public class VenueController {

    @Autowired
    private VenueService venueService;

    @Autowired
    private UserService userService;

    // ================== 公开接口 (无需登录) ==================

    /**
     * 1. 场馆列表查询 (支持模糊搜索、区域筛选、类型筛选)
     */
    @GetMapping("/venues")
    public Result list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String region,
                       @RequestParam(required = false) String type) {
        QueryWrapper<Venue> qw = new QueryWrapper<>();

        // 动态拼接查询条件
        if (name != null && !name.isEmpty()) {
            qw.like("name", name);
        }
        if (region != null && !region.isEmpty()) {
            // likeRight 相当于 'xxx%', 匹配 "广东省 / 广州市%"
            qw.likeRight("region", region);
        }
        if (type != null && !type.isEmpty() && !"全部".equals(type)) {
            qw.eq("type", type);
        }

        // 只显示上架状态 (status=1)
        qw.eq("status", 1);
        // 按 ID 倒序，新发布的在前
        qw.orderByDesc("id");

        return Result.success(venueService.list(qw));
    }

    /**
     * 2. 场馆详情查询
     */
    @GetMapping("/venue/{id}")
    public Result getDetail(@PathVariable Long id) {
        Venue venue = venueService.getById(id);
        if (venue == null) {
            return Result.error("场馆不存在");
        }

        // 查询房东信息
        User owner = userService.getById(venue.getOwnerId());

        Map<String, Object> map = new HashMap<>();
        map.put("venue", venue);

        // 房东信息脱敏，只返回 ID 和 用户名
        if (owner != null) {
            Map<String, Object> ownerInfo = new HashMap<>();
            ownerInfo.put("id", owner.getId());
            ownerInfo.put("username", owner.getUsername());
            map.put("owner", ownerInfo);
        }

        return Result.success(map);
    }

    // ================== 需要登录的接口 ==================

    /**
     * 3. 查询“我发布的”场馆
     */
    @GetMapping("/my-venues")
    public Result myVenues(@RequestAttribute("currentUserId") Long userId) {
        QueryWrapper<Venue> qw = new QueryWrapper<>();
        qw.eq("owner_id", userId);
        qw.orderByDesc("id");
        return Result.success(venueService.list(qw));
    }

    /**
     * 4. 发布场馆
     */
    @PostMapping("/venue/add")
    public Result add(@RequestBody Venue venue, @RequestAttribute("currentUserId") Long userId) {
        venue.setOwnerId(userId); // 绑定当前登录用户
        venue.setStatus(1);       // 默认状态：上架
        venueService.save(venue);
        return Result.success("发布成功");
    }

    /**
     * 5. 修改场馆 (支持 管理员 或 房东本人)
     */
    @PostMapping("/venue/update")
    public Result update(@RequestBody Venue venue, @RequestAttribute("currentUserId") Long userId) {
        // 先查旧数据
        Venue dbVenue = venueService.getById(venue.getId());
        if (dbVenue == null) {
            return Result.error("场馆不存在");
        }

        // 权限校验
        User currentUser = userService.getById(userId);
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());

        // 如果不是管理员 且 不是房东本人 -> 拒绝
        if (!isAdmin && !dbVenue.getOwnerId().equals(userId)) {
            return Result.error("无权修改此场馆");
        }

        // 保护 ownerId 字段
        if (!isAdmin) {
            // 普通用户修改：强制保持原 ownerId
            venue.setOwnerId(userId);
        } else {
            // 管理员修改：保持数据库里的 ownerId (避免不小心把场馆归属改成管理员自己)
            venue.setOwnerId(dbVenue.getOwnerId());
        }

        venueService.updateById(venue);
        return Result.success("修改成功");
    }

    /**
     * 6. 删除场馆 (支持 管理员 或 房东本人)
     */
    @DeleteMapping("/venue/{id}")
    public Result delete(@PathVariable Long id, @RequestAttribute("currentUserId") Long userId) {
        Venue venue = venueService.getById(id);
        if (venue == null) {
            return Result.error("场馆不存在");
        }

        // 权限校验
        User currentUser = userService.getById(userId);
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());

        // 如果不是管理员 且 不是房东本人 -> 拒绝
        if (!isAdmin && !venue.getOwnerId().equals(userId)) {
            return Result.error("无权删除此场馆");
        }

        venueService.removeById(id);
        return Result.success("删除成功");
    }
}