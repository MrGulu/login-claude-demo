package com.demo.login.service;

import com.demo.login.dto.CreateUserDTO;
import com.demo.login.dto.UpdateUserDTO;
import com.demo.login.dto.UserQueryDTO;
import com.demo.login.vo.PageResult;
import com.demo.login.vo.UserVO;

/**
 * 用户管理服务接口
 *
 * @author Claude
 * @since 2024-03-04
 */
public interface IUserManagementService {

    /**
     * 分页查询用户列表
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    PageResult<UserVO> getUserList(UserQueryDTO dto);

    /**
     * 根据ID查询用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 创建用户
     *
     * @param dto 创建参数
     */
    void createUser(CreateUserDTO dto);

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param dto 更新参数
     */
    void updateUser(Long id, UpdateUserDTO dto);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态：0-禁用，1-正常
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    java.util.List<Long> getUserRoles(Long userId);

    /**
     * 分配角色给用户（覆盖式更新）
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, java.util.List<Long> roleIds);
}
