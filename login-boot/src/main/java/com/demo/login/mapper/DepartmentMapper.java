package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门 Mapper 接口
 *
 * @author Claude
 * @since 2026-06-09
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
