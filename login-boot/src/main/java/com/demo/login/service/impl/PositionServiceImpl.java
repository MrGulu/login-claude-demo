package com.demo.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.dto.PositionDTO;
import com.demo.login.dto.PositionQueryDTO;
import com.demo.login.entity.Position;
import com.demo.login.mapper.PositionMapper;
import com.demo.login.service.IPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 岗位服务实现类
 *
 * @author Claude
 * @since 2026-03-13
 */
@Slf4j
@Service
public class PositionServiceImpl implements IPositionService {

    @Autowired
    private PositionMapper positionMapper;

    @Override
    public Page<Position> getPositionList(PositionQueryDTO queryDTO) {
        Page<Position> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getPositionName())) {
            wrapper.like(Position::getPositionName, queryDTO.getPositionName());
        }
        if (StringUtils.hasText(queryDTO.getPositionCode())) {
            wrapper.like(Position::getPositionCode, queryDTO.getPositionCode());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Position::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(Position::getSort);
        return positionMapper.selectPage(page, wrapper);
    }

    @Override
    public Position getPositionById(Long id) {
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new BusinessException("岗位不存在");
        }
        return position;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPosition(PositionDTO positionDTO) {
        // 检查岗位编码是否重复
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getPositionCode, positionDTO.getPositionCode());
        if (positionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("岗位编码已存在");
        }

        Position position = new Position();
        BeanUtils.copyProperties(positionDTO, position);
        positionMapper.insert(position);

        log.info("创建岗位成功，岗位ID: {}", position.getId());
        return position.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(Long id, PositionDTO positionDTO) {
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new BusinessException("岗位不存在");
        }

        // 检查岗位编码是否重复
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getPositionCode, positionDTO.getPositionCode())
                .ne(Position::getId, id);
        if (positionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("岗位编码已存在");
        }

        BeanUtils.copyProperties(positionDTO, position);
        positionMapper.updateById(position);

        log.info("更新岗位成功，岗位ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(Long id) {
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new BusinessException("岗位不存在");
        }

        positionMapper.deleteById(id);
        log.info("删除岗位成功，岗位ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePositionStatus(Long id, Integer status) {
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new BusinessException("岗位不存在");
        }

        position.setStatus(status);
        positionMapper.updateById(position);

        log.info("更新岗位状态成功，岗位ID: {}, 状态: {}", id, status);
    }
}
