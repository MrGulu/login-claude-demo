package com.demo.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.login.entity.Menu;
import com.demo.login.entity.Role;
import com.demo.login.entity.RoleMenu;
import com.demo.login.entity.UserRole;
import com.demo.login.mapper.MenuMapper;
import com.demo.login.mapper.RoleMapper;
import com.demo.login.mapper.RoleMenuMapper;
import com.demo.login.mapper.UserRoleMapper;
import com.demo.login.service.IMenuService;
import com.demo.login.vo.MenuTreeVO;
import com.demo.login.vo.UserMenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author Claude
 * @since 2024-03-06
 */
@Slf4j
@Service
public class MenuServiceImpl implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 菜单树缓存
     */
    private volatile List<MenuTreeVO> menuTreeCache;

    @Override
    public List<MenuTreeVO> getMenuTree() {
        // 双重检查锁实现缓存
        if (menuTreeCache == null) {
            synchronized (this) {
                if (menuTreeCache == null) {
                    // 查询所有菜单
                    LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Menu::getStatus, 1)
                            .orderByAsc(Menu::getSort);
                    List<Menu> allMenus = menuMapper.selectList(wrapper);

                    // 构建菜单树
                    menuTreeCache = buildMenuTree(allMenus, 0L);
                }
            }
        }
        return menuTreeCache;
    }

    @Override
    public List<UserMenuVO> getUserMenus(Long userId) {
        // 查询用户的角色
        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(urWrapper);

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // 检查是否有 root 角色
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getId, roleIds)
                .eq(Role::getRoleKey, "root")
                .eq(Role::getStatus, 1);
        Role rootRole = roleMapper.selectOne(roleWrapper);

        List<Menu> menus;
        if (rootRole != null) {
            // root 角色直接返回所有菜单
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.eq(Menu::getStatus, 1)
                    .eq(Menu::getVisible, 1)
                    .in(Menu::getMenuType, "M", "C")
                    .orderByAsc(Menu::getSort);
            menus = menuMapper.selectList(menuWrapper);
        } else {
            // 根据角色权限查询菜单
            menus = menuMapper.selectMenusByRoleIds(roleIds);
            // 过滤只保留目录和菜单
            menus = menus.stream()
                    .filter(m -> "M".equals(m.getMenuType()) || "C".equals(m.getMenuType()))
                    .filter(m -> m.getVisible() == 1)
                    .collect(Collectors.toList());
        }

        // 构建用户菜单树
        return buildUserMenuTree(menus, 0L);
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public void clearCache() {
        menuTreeCache = null;
        log.info("菜单缓存已清除");
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        // 查询用户角色
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 检查是否有 root 角色
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        boolean hasRootRole = roles.stream()
                .anyMatch(role -> "root".equals(role.getRoleKey()));

        if (hasRootRole) {
            // root 角色拥有所有权限
            return menuMapper.selectList(
                    new LambdaQueryWrapper<Menu>()
                            .eq(Menu::getStatus, 1)
                            .isNotNull(Menu::getPerms)
            ).stream()
                    .map(Menu::getPerms)
                    .filter(perms -> perms != null && !perms.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 查询角色的菜单权限
        List<Long> menuIds = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .in(RoleMenu::getRoleId, roleIds)
        ).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询菜单的权限标识
        return menuMapper.selectBatchIds(menuIds).stream()
                .filter(menu -> menu.getPerms() != null && !menu.getPerms().isEmpty())
                .map(Menu::getPerms)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 构建菜单树
     */
    private List<MenuTreeVO> buildMenuTree(List<Menu> menus, Long parentId) {
        List<MenuTreeVO> tree = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                MenuTreeVO vo = new MenuTreeVO();
                BeanUtils.copyProperties(menu, vo);
                vo.setChildren(buildMenuTree(menus, menu.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }

    /**
     * 构建用户菜单树
     */
    private List<UserMenuVO> buildUserMenuTree(List<Menu> menus, Long parentId) {
        List<UserMenuVO> tree = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                UserMenuVO vo = new UserMenuVO();
                BeanUtils.copyProperties(menu, vo);
                vo.setChildren(buildUserMenuTree(menus, menu.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }
}
