package com.demo.login.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.dto.PositionDTO;
import com.demo.login.dto.PositionQueryDTO;
import com.demo.login.entity.Position;

/**
 * 岗位服务接口
 *
 * @author Claude
 * @since 2026-03-13
 */
public interface IPositionService {

    /**
     * 分页查询岗位列表
     *
     * @param queryDTO 查询条件
     * @return 岗位分页数据
     */
    Page<Position> getPositionList(PositionQueryDTO queryDTO);

    /**
     * 根据ID查询岗位详情
     *
     * @param id 岗位ID
     * @return 岗位详情
     */
    Position getPositionById(Long id);

    /**
     * 创建岗位
     *
     * @param positionDTO 岗位信息
     * @return 岗位ID
     */
    Long createPosition(PositionDTO positionDTO);

    /**
     * 更新岗位
     *
     * @param id 岗位ID
     * @param positionDTO 岗位信息
     */
    void updatePosition(Long id, PositionDTO positionDTO);

    /**
     * 删除岗位
     *
     * @param id 岗位ID
     */
    void deletePosition(Long id);

    /**
     * 更新岗位状态
     *
     * @param id 岗位ID
     * @param status 状态
     */
    void updatePositionStatus(Long id, Integer status);
}
