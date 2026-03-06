package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-角色关联Mapper接口
 *
 * @author Claude
 * @since 2024-03-06
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
