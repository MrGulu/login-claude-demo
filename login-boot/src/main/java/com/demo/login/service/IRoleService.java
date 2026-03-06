package com.demo.login.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.dto.AssignMenuDTO;
import com.demo.login.dto.RoleDTO;
import com.demo.login.dto.RoleQueryDTO;
import com.demo.login.vo.RoleVO;

/**
 * 角色服务接口
 *
 * @author Claude
 * @since 2024-03-06
 */
public interface IRoleService {

    /**
     * 分页查询角色列表
     *
     * @param queryDTO 查询条件
     * @return 角色分页数据
     */
    Page<RoleVO> getRoleList(RoleQueryDTO queryDTO);

    /**
     * 根据ID查询角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    RoleVO getRoleById(Long id);

    /**
     * 创建角色
     *
     * @param roleDTO 角色信息
     * @return 角色ID
     */
    Long createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     *
     * @param id 角色ID
     * @param roleDTO 角色信息
     */
    void updateRole(Long id, RoleDTO roleDTO);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 分配菜单权限
     *
     * @param assignMenuDTO 分配信息
     */
    void assignMenus(AssignMenuDTO assignMenuDTO);
}
