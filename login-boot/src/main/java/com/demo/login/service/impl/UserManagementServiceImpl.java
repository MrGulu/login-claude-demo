package com.demo.login.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.common.utils.PasswordUtil;
import com.demo.login.dto.CreateUserDTO;
import com.demo.login.dto.UpdateUserDTO;
import com.demo.login.dto.UserQueryDTO;
import com.demo.login.entity.Role;
import com.demo.login.entity.User;
import com.demo.login.entity.UserRole;
import com.demo.login.mapper.RoleMapper;
import com.demo.login.mapper.UserMapper;
import com.demo.login.mapper.UserRoleMapper;
import com.demo.login.service.IUserManagementService;
import com.demo.login.vo.PageResult;
import com.demo.login.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 *
 * @author Claude
 * @since 2024-03-04
 */
@Slf4j
@Service
public class UserManagementServiceImpl implements IUserManagementService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<UserVO> getUserList(UserQueryDTO dto) {
        // 构建分页对象
        Page<User> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getUsername()), User::getUsername, dto.getUsername())
               .like(StrUtil.isNotBlank(dto.getPhone()), User::getPhone, dto.getPhone())
               .eq(dto.getStatus() != null, User::getStatus, dto.getStatus())
               .orderByDesc(User::getCreateTime);

        // 执行查询
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为VO
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(userPage.getTotal(), voList);
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(CreateUserDTO dto) {
        // 校验用户名唯一性
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 校验邮箱唯一性
        if (StrUtil.isNotBlank(dto.getEmail())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, dto.getEmail());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        // 校验手机号唯一性
        if (StrUtil.isNotBlank(dto.getPhone())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, dto.getPhone());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("手机号已被使用");
            }
        }

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UpdateUserDTO dto) {
        // 校验用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验邮箱唯一性（排除当前用户）
        if (StrUtil.isNotBlank(dto.getEmail())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, dto.getEmail())
                   .ne(User::getId, id);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        // 校验手机号唯一性（排除当前用户）
        if (StrUtil.isNotBlank(dto.getPhone())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, dto.getPhone())
                   .ne(User::getId, id);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("手机号已被使用");
            }
        }

        // 更新用户信息
        BeanUtils.copyProperties(dto, user);

        // 如果密码不为空，则加密后更新
        if (StrUtil.isNotBlank(dto.getPassword())) {
            user.setPassword(PasswordUtil.encode(dto.getPassword()));
        }

        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 校验用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否是 admin 用户（username = 'admin'）
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除系统管理员账户");
        }

        // 检查是否拥有 root 角色
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, id)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());

        if (!roleIds.isEmpty()) {
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            boolean hasRootRole = roles.stream()
                    .anyMatch(role -> "root".equals(role.getRoleKey()));
            if (hasRootRole) {
                throw new BusinessException("不能删除拥有超级管理员角色的用户");
            }
        }

        // 逻辑删除
        userMapper.deleteById(id);

        // 删除用户角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        userRoleMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        // 校验用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否是 admin 用户（username = 'admin'）
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能修改系统管理员账户状态");
        }

        // 检查是否拥有 root 角色
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, id)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());

        if (!roleIds.isEmpty()) {
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            boolean hasRootRole = roles.stream()
                    .anyMatch(role -> "root".equals(role.getRoleKey()));
            if (hasRootRole) {
                throw new BusinessException("不能修改拥有超级管理员角色的用户状态");
            }
        }

        // 更新状态
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 校验用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否是 admin 用户（username = 'admin'）
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能修改系统管理员账户的角色");
        }

        // 检查是否拥有 root 角色
        List<Long> currentRoleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());

        if (!currentRoleIds.isEmpty()) {
            List<Role> roles = roleMapper.selectBatchIds(currentRoleIds);
            boolean hasRootRole = roles.stream()
                    .anyMatch(role -> "root".equals(role.getRoleKey()));
            if (hasRootRole) {
                throw new BusinessException("不能修改拥有超级管理员角色的用户的角色");
            }
        }

        // 删除旧的角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 批量插入新的角色关联
        roleIds.forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        });

        log.info("分配角色成功，用户ID: {}, 角色数量: {}", userId, roleIds.size());
    }

    /**
     * 转换为VO对象
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 格式化时间
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(DATE_TIME_FORMATTER));
        }
        if (user.getUpdateTime() != null) {
            vo.setUpdateTime(user.getUpdateTime().format(DATE_TIME_FORMATTER));
        }

        return vo;
    }
}
