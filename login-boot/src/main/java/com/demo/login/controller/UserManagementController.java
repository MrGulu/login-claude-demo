package com.demo.login.controller;

import com.demo.login.annotation.RequirePermission;
import com.demo.login.common.result.Result;
import com.demo.login.dto.AssignRoleDTO;
import com.demo.login.dto.CreateUserDTO;
import com.demo.login.dto.UpdateUserDTO;
import com.demo.login.dto.UpdateUserStatusDTO;
import com.demo.login.dto.UserQueryDTO;
import com.demo.login.service.IUserManagementService;
import com.demo.login.vo.PageResult;
import com.demo.login.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 *
 * @author Claude
 * @since 2024-03-04
 */
@RestController
@RequestMapping("/api/admin/users")
@Validated
public class UserManagementController {

    @Autowired
    private IUserManagementService userManagementService;

    /**
     * 分页查询用户列表
     * GET /api/admin/users?pageNum=1&pageSize=10&username=admin&phone=138&status=1
     */
    @GetMapping
    @RequirePermission("system:user:query")
    public Result<PageResult<UserVO>> getUserList(@Valid UserQueryDTO dto) {
        PageResult<UserVO> result = userManagementService.getUserList(dto);
        return Result.success(result);
    }

    /**
     * 查询用户详情
     * GET /api/admin/users/{id}
     */
    @GetMapping("/{id}")
    @RequirePermission("system:user:query")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO user = userManagementService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 创建用户
     * POST /api/admin/users
     */
    @PostMapping
    @RequirePermission("system:user:add")
    public Result<Void> createUser(@Valid @RequestBody CreateUserDTO dto) {
        userManagementService.createUser(dto);
        return Result.success();
    }

    /**
     * 更新用户信息
     * PUT /api/admin/users/{id}
     */
    @PutMapping("/{id}")
    @RequirePermission("system:user:edit")
    public Result<Void> updateUser(@PathVariable Long id,
                                    @Valid @RequestBody UpdateUserDTO dto) {
        userManagementService.updateUser(id, dto);
        return Result.success();
    }

    /**
     * 删除用户
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return Result.success();
    }

    /**
     * 更新用户状态
     * PUT /api/admin/users/{id}/status
     */
    @PutMapping("/{id}/status")
    @RequirePermission("system:user:status")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                          @Valid @RequestBody UpdateUserStatusDTO dto) {
        userManagementService.updateUserStatus(id, dto.getStatus());
        return Result.success();
    }

    /**
     * 获取用户的角色列表
     * GET /api/admin/users/{id}/roles
     */
    @GetMapping("/{id}/roles")
    @RequirePermission("system:user:query")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = userManagementService.getUserRoles(id);
        return Result.success(roleIds);
    }

    /**
     * 分配角色给用户
     * PUT /api/admin/users/{id}/roles
     */
    @PutMapping("/{id}/roles")
    @RequirePermission("system:user:role")
    public Result<Void> assignRoles(@PathVariable Long id,
                                     @Valid @RequestBody AssignRoleDTO dto) {
        dto.setUserId(id);
        userManagementService.assignRoles(dto.getUserId(), dto.getRoleIds());
        return Result.success();
    }
}
