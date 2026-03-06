package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 *
 * @author Claude
 * @since 2024-03-06
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
