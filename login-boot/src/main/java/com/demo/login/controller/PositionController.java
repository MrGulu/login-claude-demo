package com.demo.login.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.annotation.RequirePermission;
import com.demo.login.common.result.Result;
import com.demo.login.dto.PositionDTO;
import com.demo.login.dto.PositionQueryDTO;
import com.demo.login.entity.Position;
import com.demo.login.service.IPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 岗位控制器
 *
 * @author Claude
 * @since 2026-03-13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/positions")
public class PositionController {

    @Autowired
    private IPositionService positionService;

    /**
     * 分页查询岗位列表
     */
    @GetMapping
    @RequirePermission("system:position:query")
    public Result<Page<Position>> getPositionList(PositionQueryDTO queryDTO) {
        Page<Position> page = positionService.getPositionList(queryDTO);
        return Result.success(page);
    }

    /**
     * 查询岗位详情
     */
    @GetMapping("/{id}")
    @RequirePermission("system:position:query")
    public Result<Position> getPositionById(@PathVariable Long id) {
        Position position = positionService.getPositionById(id);
        return Result.success(position);
    }

    /**
     * 创建岗位
     */
    @PostMapping
    @RequirePermission("system:position:add")
    public Result<Long> createPosition(@Valid @RequestBody PositionDTO positionDTO) {
        Long positionId = positionService.createPosition(positionDTO);
        return Result.success(positionId);
    }

    /**
     * 更新岗位
     */
    @PutMapping("/{id}")
    @RequirePermission("system:position:edit")
    public Result<Void> updatePosition(@PathVariable Long id, @Valid @RequestBody PositionDTO positionDTO) {
        positionService.updatePosition(id, positionDTO);
        return Result.success();
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:position:delete")
    public Result<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return Result.success();
    }

    /**
     * 更新岗位状态
     */
    @PutMapping("/{id}/status")
    @RequirePermission("system:position:edit")
    public Result<Void> updatePositionStatus(@PathVariable Long id, @RequestBody Map<String, Integer> statusMap) {
        Integer status = statusMap.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        positionService.updatePositionStatus(id, status);
        return Result.success();
    }
}
