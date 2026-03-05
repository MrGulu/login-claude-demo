package com.demo.login.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.common.utils.PasswordUtil;
import com.demo.login.dto.CreateUserDTO;
import com.demo.login.dto.UpdateUserDTO;
import com.demo.login.dto.UserQueryDTO;
import com.demo.login.entity.User;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.IUserManagementService;
import com.demo.login.vo.PageResult;
import com.demo.login.vo.UserVO;
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
@Service
public class UserManagementServiceImpl implements IUserManagementService {

    @Autowired
    private UserMapper userMapper;

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

        // 逻辑删除
        userMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        // 校验用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新状态
        user.setStatus(status);
        userMapper.updateById(user);
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
