package com.demo.login.controller;

import com.demo.login.annotation.RequirePermission;
import com.demo.login.common.result.Result;
import com.demo.login.dto.DepartmentDTO;
import com.demo.login.dto.DepartmentQueryDTO;
import com.demo.login.entity.Department;
import com.demo.login.service.IDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 部门控制器
 *
 * @author Claude
 * @since 2026-06-09
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/departments")
public class DepartmentController {

    @Autowired
    private IDepartmentService departmentService;

    /**
     * 查询部门列表（不分页，支持过滤）
     */
    @GetMapping
    @RequirePermission("system:dept:query")
    public Result<List<Department>> getDepartmentList(DepartmentQueryDTO queryDTO) {
        List<Department> list = departmentService.getDepartmentList(queryDTO);
        return Result.success(list);
    }

    /**
     * 查询部门详情
     */
    @GetMapping("/{id}")
    @RequirePermission("system:dept:query")
    public Result<Department> getDepartmentById(@PathVariable Long id) {
        Department dept = departmentService.getDepartmentById(id);
        return Result.success(dept);
    }

    /**
     * 创建部门
     */
    @PostMapping
    @RequirePermission("system:dept:add")
    public Result<Long> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        Long deptId = departmentService.createDepartment(departmentDTO);
        return Result.success(deptId);
    }

    /**
     * 更新部门
     */
    @PutMapping("/{id}")
    @RequirePermission("system:dept:edit")
    public Result<Void> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDTO departmentDTO) {
        departmentService.updateDepartment(id, departmentDTO);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:dept:delete")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }

    /**
     * 更新部门状态
     */
    @PutMapping("/{id}/status")
    @RequirePermission("system:dept:edit")
    public Result<Void> updateDepartmentStatus(@PathVariable Long id, @RequestBody Map<String, Integer> statusMap) {
        Integer status = statusMap.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        departmentService.updateDepartmentStatus(id, status);
        return Result.success();
    }
}
