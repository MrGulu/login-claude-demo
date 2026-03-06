package com.demo.login.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.annotation.RequirePermission;
import com.demo.login.common.result.Result;
import com.demo.login.dto.AssignMenuDTO;
import com.demo.login.dto.RoleDTO;
import com.demo.login.dto.RoleQueryDTO;
import com.demo.login.service.IMenuService;
import com.demo.login.service.IRoleService;
import com.demo.login.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色控制器
 *
 * @author Claude
 * @since 2024-03-06
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IMenuService menuService;

    /**
     * 分页查询角色列表
     */
    @GetMapping
    @RequirePermission("system:role:query")
    public Result<Page<RoleVO>> getRoleList(RoleQueryDTO queryDTO) {
        Page<RoleVO> page = roleService.getRoleList(queryDTO);
        return Result.success(page);
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/{id}")
    @RequirePermission("system:role:query")
    public Result<RoleVO> getRoleById(@PathVariable Long id) {
        RoleVO role = roleService.getRoleById(id);
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    @RequirePermission("system:role:add")
    public Result<Long> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Long roleId = roleService.createRole(roleDTO);
        return Result.success(roleId);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @RequirePermission("system:role:edit")
    public Result<Void> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(id, roleDTO);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:role:delete")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色的菜单权限
     */
    @GetMapping("/{id}/menus")
    @RequirePermission("system:role:query")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        List<Long> menuIds = menuService.getRoleMenuIds(id);
        return Result.success(menuIds);
    }

    /**
     * 分配菜单权限
     */
    @PutMapping("/{id}/menus")
    @RequirePermission("system:role:assign")
    public Result<Void> assignMenus(@PathVariable Long id, @Valid @RequestBody AssignMenuDTO assignMenuDTO) {
        assignMenuDTO.setRoleId(id);
        roleService.assignMenus(assignMenuDTO);
        return Result.success();
    }
}
