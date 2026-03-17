package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.UserPosition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-岗位关联Mapper接口
 *
 * @author Claude
 * @since 2024-03-16
 */
@Mapper
public interface UserPositionMapper extends BaseMapper<UserPosition> {
}
