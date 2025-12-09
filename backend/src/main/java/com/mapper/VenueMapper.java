package com.mapper;

import com.entity.Venue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// @Mapper 注解告诉Spring Boot这是一个MyBatis的Mapper接口
@Mapper
public interface VenueMapper {

    /**
     * 根据ID查询场馆信息
     * @param id 场馆ID
     * @return 场馆对象
     */
    Venue findById(Integer id);

    /**
     * 查询所有场馆列表
     * @return 场馆列表
     */
    List<Venue> findAll(@Param("name") String name);

    /**
     * 新增场馆
     * @param venue 场馆对象
     * @return 影响的行数
     */
    int insert(Venue venue);

    /**
     * 更新场馆信息
     * @param venue 场馆对象
     * @return 影响的行数
     */
    int update(Venue venue);

    /**
     * 根据ID删除场馆 (物理删除，课程设计中可以简化)
     * @param id 场馆ID
     * @return 影响的行数
     */
    int deleteById(Integer id);

    List<Venue> findByOwnerId(Integer ownerId);
}