package com.controller;

import com.config.AdminRequired;
import com.entity.Venue;
import com.service.VenueService;
import com.utils.Result;
// 导入Swagger注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
// @Tag 注解用于给Controller分组，会显示在文档的标题上
@Tag(name = "场馆管理", description = "用于管理场馆信息的API")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @Operation(summary = "获取所有场馆列表或根据名称搜索")
    @GetMapping
    public Result<List<Venue>> getAllVenues(
            // @RequestParam 表示这是一个URL查询参数, 如 /api/venues?name=篮球
            // required = false 表示这个参数是可选的
            @Parameter(description = "场馆名称，用于模糊搜索") @RequestParam(required = false) String name
    ) {
        List<Venue> venues = venueService.listAllVenues(name);
        return Result.success("查询场馆列表成功", venues);
    }

    @Operation(summary = "根据ID获取场馆详情")
    // @Parameter 注解用于描述参数
    @GetMapping("/{id}")
    public Result<Venue> getVenueById(@Parameter(description = "场馆的唯一ID") @PathVariable Integer id) {
        Venue venue = venueService.getVenueById(id);
        if (venue != null) {
            return Result.success("查询场馆详情成功", venue);
        } else {
            return Result.error(404, "场馆不存在");
        }
    }

    @Operation(summary = "(管理员) 新增场馆")
    @PostMapping
    @AdminRequired
    public Result<Venue> createVenue(@RequestBody Venue venue) {
        Venue newVenue = venueService.addNewVenue(venue);
        return Result.success("新增场馆成功", newVenue);
    }

    // ... 其他方法也可以添加类似注解 ...
    @Operation(summary = "(管理员) 更新场馆信息")
    @PutMapping("/{id}")
    @AdminRequired
    public Result<Venue> updateVenue(@Parameter(description = "要更新的场馆ID") @PathVariable Integer id, @RequestBody Venue venue) {
        venue.setId(id);
        Venue updatedVenue = venueService.updateVenue(venue);
        return Result.success("更新场馆成功", updatedVenue);
    }

    @Operation(summary = "(管理员) 删除场馆")
    @DeleteMapping("/{id}")
    @AdminRequired
    public Result<?> deleteVenue(@Parameter(description = "要删除的场馆ID") @PathVariable Integer id) {
        venueService.deleteVenueById(id);
        return Result.success("删除场馆成功", null);
    }
}