package com.demo.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.dto.AssignMenuDTO;
import com.demo.login.dto.RoleDTO;
import com.demo.login.dto.RoleQueryDTO;
import com.demo.login.entity.Role;
import com.demo.login.entity.RoleMenu;
import com.demo.login.entity.UserRole;
import com.demo.login.mapper.RoleMapper;
import com.demo.login.mapper.RoleMenuMapper;
import com.demo.login.mapper.UserRoleMapper;
import com.demo.login.service.IMenuService;
import com.demo.login.service.IRoleService;
import com.demo.login.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author Claude
 * @since 2024-03-06
 */
@Slf4j
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private IMenuService menuService;

    @Override
    public Page<RoleVO> getRoleList(RoleQueryDTO queryDTO) {
        Page<Role> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getRoleName())) {
            wrapper.like(Role::getRoleName, queryDTO.getRoleName());
        }
        if (StringUtils.hasText(queryDTO.getRoleKey())) {
            wrapper.like(Role::getRoleKey, queryDTO.getRoleKey());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Role::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(Role::getSort);
        Page<Role> rolePage = roleMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<RoleVO> voPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        List<RoleVO> voList = rolePage.getRecords().stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public RoleVO getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleDTO roleDTO) {
        // 检查角色标识是否重复
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, roleDTO.getRoleKey());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色标识已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        role.setIsSystem(0); // 非系统角色
        roleMapper.insert(role);

        log.info("创建角色成功，角色ID: {}", role.getId());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, RoleDTO roleDTO) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否系统角色
        if (role.getIsSystem() == 1) {
            throw new BusinessException("系统角色不允许修改");
        }

        // 检查角色标识是否重复
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, roleDTO.getRoleKey())
                .ne(Role::getId, id);
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色标识已存在");
        }

        BeanUtils.copyProperties(roleDTO, role);
        roleMapper.updateById(role);

        log.info("更新角色成功，角色ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否系统角色
        if (role.getIsSystem() == 1) {
            throw new BusinessException("系统角色不允许删除");
        }

        // 检查是否有用户在使用
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleId, id);
        if (userRoleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该角色已分配给用户，无法删除");
        }

        // 删除角色
        roleMapper.deleteById(id);

        // 删除角色-菜单关联
        LambdaQueryWrapper<RoleMenu> rmWrapper = new LambdaQueryWrapper<>();
        rmWrapper.eq(RoleMenu::getRoleId, id);
        roleMenuMapper.delete(rmWrapper);

        log.info("删除角色成功，角色ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(AssignMenuDTO assignMenuDTO) {
        Role role = roleMapper.selectById(assignMenuDTO.getRoleId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否系统角色
        if (role.getIsSystem() == 1) {
            throw new BusinessException("系统角色不允许修改权限");
        }

        // 删除旧的关联
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, assignMenuDTO.getRoleId());
        roleMenuMapper.delete(wrapper);

        // 批量插入新的关联
        List<RoleMenu> roleMenus = assignMenuDTO.getMenuIds().stream().map(menuId -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(assignMenuDTO.getRoleId());
            roleMenu.setMenuId(menuId);
            return roleMenu;
        }).collect(Collectors.toList());

        roleMenus.forEach(roleMenuMapper::insert);

        // 清除菜单缓存
        menuService.clearCache();

        log.info("分配菜单权限成功，角色ID: {}, 菜单数量: {}",
                assignMenuDTO.getRoleId(), assignMenuDTO.getMenuIds().size());
    }
}
