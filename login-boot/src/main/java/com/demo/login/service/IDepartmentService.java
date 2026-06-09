package com.demo.login.service;

import com.demo.login.dto.DepartmentDTO;
import com.demo.login.dto.DepartmentQueryDTO;
import com.demo.login.entity.Department;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author Claude
 * @since 2026-06-09
 */
public interface IDepartmentService {

    /**
     * 获取部门列表
     *
     * @param queryDTO 查询参数
     * @return 部门列表
     */
    List<Department> getDepartmentList(DepartmentQueryDTO queryDTO);

    /**
     * 根据ID获取部门
     *
     * @param id 部门ID
     * @return 部门实体
     */
    Department getDepartmentById(Long id);

    /**
     * 创建部门
     *
     * @param departmentDTO 部门参数
     * @return 部门ID
     */
    Long createDepartment(DepartmentDTO departmentDTO);

    /**
     * 更新部门
     *
     * @param id 部门ID
     * @param departmentDTO 部门参数
     */
    void updateDepartment(Long id, DepartmentDTO departmentDTO);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDepartment(Long id);

    /**
     * 更新部门状态
     *
     * @param id 部门ID
     * @param status 状态
     */
    void updateDepartmentStatus(Long id, Integer status);
}
