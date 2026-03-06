package com.demo.login.service;

import com.demo.login.vo.MenuTreeVO;
import com.demo.login.vo.UserMenuVO;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author Claude
 * @since 2024-03-06
 */
public interface IMenuService {

    /**
     * 获取完整菜单树（用于权限分配）
     *
     * @return 菜单树列表
     */
    List<MenuTreeVO> getMenuTree();

    /**
     * 获取用户菜单（用于前端路由）
     *
     * @param userId 用户ID
     * @return 用户菜单列表
     */
    List<UserMenuVO> getUserMenus(Long userId);

    /**
     * 获取角色的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 清除菜单缓存
     */
    void clearCache();

    /**
     * 获取用户权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> getUserPermissions(Long userId);
}
