package com.demo.login.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.common.utils.PasswordUtil;
import com.demo.login.dto.ChangePasswordDTO;
import com.demo.login.dto.UpdateProfileDTO;
import com.demo.login.entity.User;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.IUserProfileService;
import com.demo.login.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

/**
 * 用户资料服务实现类
 *
 * @author Claude
 * @since 2026-03-04
 */
@Slf4j
@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Autowired
    private UserMapper userMapper;

    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024; // 2MB

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateProfile(Long userId, UpdateProfileDTO dto) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(2001, "用户不存在");
        }

        // 检查邮箱唯一性（排除当前用户）
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, dto.getEmail())
                    .ne(User::getId, userId);
            Long emailCount = userMapper.selectCount(emailWrapper);
            if (emailCount > 0) {
                throw new BusinessException(2004, "邮箱已被使用");
            }
        }

        // 检查手机号唯一性（排除当前用户）
      if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, dto.getPhone())
                    .ne(User::getId, userId);
          Long phoneCount = userMapper.selectCount(phoneWrapper);
            if (phoneCount > 0) {
                throw new BusinessException(2005, "手机号已被使用");
            }
        }

      // 更新用户信息
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        user.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        int result = userMapper.updateById(user);
        if (result == 0) {
       throw new BusinessException(5001, "更新用户信息失败");
        }

        // 转换为VO返回
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(user, userInfoVO);

        log.info("用户资料更新成功: userId={}", userId);
        return userInfoVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(Long userId, String base64Avatar) {
        // 验证用户存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(2001, "用户不存在");
        }

        // 验证Base64格式
        if (base64Avatar == null || base64Avatar.isEmpty()) {
            throw new BusinessException(4001, "头像数据不能为空");
        }

        // 验证Base64头部格式
        if (!base64Avatar.startsWith("data:image/")) {
            throw new BusinessException(4002, "头像格式不正确");
        }

        // 验证文件大小（Base64编码后大小约为原文件的4/3）
    int base64Length = base64Avatar.length();
        long estimatedSize = (long) (base64Length * 0.75);
        if (estimatedSize > MAX_AVATAR_SIZE) {
          throw new BusinessException(4003, "头像大小不能超过2MB");
        }

        // 更新头像
        user.setAvatar(base64Avatar);
        user.setUpdateTime(LocalDateTime.now());

        int result = userMapper.updateById(user);
        if (result == 0) {
            throw new BusinessException(5002, "上传头像失败");
        }

        log.info("用户头像上传成功: userId={}", userId);
        return base64Avatar;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(2001, "用户不存在");
        }

        // 验证原密码
        if (!PasswordUtil.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(2006, "原密码不正确");
        }

     // 验证新密码与确认密码一致
     if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(2007, "两次输入的密码不一致");
        }

        // 验证新密码不同于旧密码
        if (PasswordUtil.matches(dto.getNewPassword(), user.getPassword())) {
            throw new BusinessException(2008, "新密码不能与原密码相同");
        }

        // 加密新密码
        String encryptedPassword = PasswordUtil.encode(dto.getNewPassword());
    user.setPassword(encryptedPassword);
        user.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        int result = userMapper.updateById(user);
        if (result == 0) {
       throw new BusinessException(5003, "修改密码失败");
        }

     log.info("用户密码修改成功: userId={}", userId);
    }
}
