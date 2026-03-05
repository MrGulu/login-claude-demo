package com.demo.login.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.common.utils.PasswordUtil;
import com.demo.login.common.utils.ValidationUtil;
import com.demo.login.common.utils.VerificationCodeUtil;
import com.demo.login.dto.ResetPasswordDTO;
import com.demo.login.dto.SendVerificationCodeDTO;
import com.demo.login.dto.VerifyCodeDTO;
import com.demo.login.entity.PasswordResetLog;
import com.demo.login.entity.User;
import com.demo.login.mapper.PasswordResetLogMapper;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.IEmailService;
import com.demo.login.service.IPasswordResetService;
import com.demo.login.service.ISmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 密码重置服务实现
 */
@Slf4j
@Service
public class PasswordResetServiceImpl implements IPasswordResetService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordResetLogMapper resetLogMapper;

    @Autowired
    private ISmsService smsService;

    @Autowired
    private IEmailService emailService;

    // 验证码存储（实际项目建议用Redis）
    private static final Map<String, VerificationCodeInfo> VERIFICATION_CODE_STORE = new ConcurrentHashMap<>();

    // 频率限制存储
    private static final Map<String, RateLimitInfo> RATE_LIMIT_STORE = new ConcurrentHashMap<>();

    // 重置令牌存储
    private static final Map<String, ResetTokenInfo> RESET_TOKEN_STORE = new ConcurrentHashMap<>();

    /**
     * 验证码信息
     */
    private static class VerificationCodeInfo {
      String code;           // 6位数字验证码
        String account;     // 手机号或邮箱
        long expireTime;       // 过期时间（5分钟）
    }

    /**
     * 频率限制信息
     */
    private static class RateLimitInfo {
        long lastSendTime;     // 上次发送时间
     int sendCount;         // 发送次数
        long blockUntil;       // 限制到期时间
    }

    /**
     * 重置令牌信息
     */
    private static class ResetTokenInfo {
     Long userId;
        long expireTime;
    }

    @Override
    public void sendVerificationCode(SendVerificationCodeDTO dto) {
        String account = dto.getAccount().trim();

        // 1. 验证账号格式
        String accountType = ValidationUtil.getAccountType(account);
        if (accountType == null) {
         throw new BusinessException(4000, "请输入正确的手机号或邮箱格式");
        }

      // 2. 查询数据库确认账号存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if ("phone".equals(accountType)) {
            wrapper.eq(User::getPhone, account);
        } else {
          wrapper.eq(User::getEmail, account);
        }
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
          throw new BusinessException(4006, "该手机号/邮箱未注册");
        }

        // 3. 检查频率限制
        checkRateLimit(account);

        // 4. 生成6位数字验证码
        String code = VerificationCodeUtil.generateNumericCode();

        // 5. 发送验证码
        try {
         if ("phone".equals(accountType)) {
          smsService.sendVerificationCode(account, code);
            } else {
                emailService.sendVerificationCode(account, code);
          }
        } catch (Exception e) {
            log.error("发送验证码失败: {}", e.getMessage(), e);
            throw new BusinessException(5000, "验证码发送失败，请稍后重试");
        }

        // 6. 存储验证码（5分钟有效期）
    VerificationCodeInfo codeInfo = new VerificationCodeInfo();
     codeInfo.code = code;
        codeInfo.account = account;
        codeInfo.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        VERIFICATION_CODE_STORE.put(account, codeInfo);

        // 7. 更新频率限制信息
        updateRateLimit(account);

        // 8. 记录日志
      saveResetLog(user.getId(), user.getUsername(), account, accountType + "_code", 1, "验证码发送成功");

        log.info("验证码已发送至 {} ({})", account, accountType);
    }

    @Override
    public String verifyCodeAndGetToken(VerifyCodeDTO dto) {
        String account = dto.getAccount().trim();
        String inputCode = dto.getVerificationCode().trim();

        // TODO: 临时逻辑 - 现阶段只要是6位数字就放行，后续接入真实短信服务后需要改回严格校验
        // 1. 验证验证码格式（必须是6位数字）
        if (!inputCode.matches("^\\d{6}$")) {
            throw new BusinessException(4002, "验证码格式错误，请输入6位数字");
        }

        log.info("【临时逻辑】验证码格式校验通过: {}", inputCode);

        // 2. 根据account查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (ValidationUtil.isValidPhone(account)) {
            wrapper.eq(User::getPhone, account);
        } else {
            wrapper.eq(User::getEmail, account);
        }
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
          throw new BusinessException(2001, "用户不存在");
     }

      // 3. 生成重置令牌（15分钟有效期）
      String resetToken = IdUtil.simpleUUID();
        ResetTokenInfo tokenInfo = new ResetTokenInfo();
      tokenInfo.userId = user.getId();
        tokenInfo.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15);
        RESET_TOKEN_STORE.put(resetToken, tokenInfo);

        // 4. 清除已使用的验证码（如果存在）
     VERIFICATION_CODE_STORE.remove(account);

        // 5. 记录日志
        String accountType = ValidationUtil.isValidPhone(account) ? "phone" : "email";
        saveResetLog(user.getId(), user.getUsername(), account, accountType + "_code", 1, "验证码验证通过（临时逻辑）");

        log.info("用户 {} 验证码验证成功（临时逻辑 - 6位数字放行）", user.getUsername());

        return resetToken;
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        // 1. 验证两次密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(4004, "两次密码输入不一致");
        }

        // 2. 验证密码长度
        if (!ValidationUtil.isValidPassword(dto.getNewPassword())) {
            throw new BusinessException(4009, "密码长度必须在6-20个字符之间");
    }

      // 3. 验证重置令牌
        ResetTokenInfo tokenInfo = RESET_TOKEN_STORE.get(dto.getResetToken());
        if (tokenInfo == null || tokenInfo.expireTime < System.currentTimeMillis()) {
          throw new BusinessException(4005, "重置令牌已过期，请重新操作");
        }

        // 4. 更新密码
        User user = userMapper.selectById(tokenInfo.userId);
        if (user == null) {
            throw new BusinessException(2001, "用户不存在");
        }

        user.setPassword(PasswordUtil.encode(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 5. 清除令牌
        RESET_TOKEN_STORE.remove(dto.getResetToken());

        // 6. 记录日志
        String account = user.getPhone() != null ? user.getPhone() : user.getEmail();
        saveResetLog(user.getId(), user.getUsername(), account, "password_reset", 1, "密码重置成功");

        log.info("用户 {} 密码重置成功", user.getUsername());
    }

    /**
     * 检查频率限制
     */
    private void checkRateLimit(String account) {
        RateLimitInfo limitInfo = RATE_LIMIT_STORE.get(account);
    long currentTime = System.currentTimeMillis();

        if (limitInfo != null) {
       // 检查是否在10分钟限制期内
        if (limitInfo.blockUntil > currentTime) {
           long remainSeconds = (limitInfo.blockUntil - currentTime) / 1000;
                throw new BusinessException(4008, "操作过于频繁，请" + remainSeconds + "秒后重试");
       }

            // 检查60秒频率限制
            if (currentTime - limitInfo.lastSendTime < TimeUnit.SECONDS.toMillis(60)) {
                long remainSeconds = 60 - (currentTime - limitInfo.lastSendTime) / 1000;
                throw new BusinessException(4007, "操作过于频繁，请" + remainSeconds + "秒后重试");
            }

            // 检查10分钟内发送次数
     if (currentTime - limitInfo.lastSendTime < TimeUnit.MINUTES.toMillis(10)) {
                if (limitInfo.sendCount >= 3) {
                  limitInfo.blockUntil = currentTime + TimeUnit.MINUTES.toMillis(10);
                throw new BusinessException(4008, "操作过于频繁，请10分钟后重试");
                }
            } else {
                // 超过10分钟，重置计数
         limitInfo.sendCount = 0;
            }
        }
    }

    /**
     * 更新频率限制信息
     */
    private void updateRateLimit(String account) {
        long currentTime = System.currentTimeMillis();
        RateLimitInfo limitInfo = RATE_LIMIT_STORE.get(account);

      if (limitInfo == null) {
         limitInfo = new RateLimitInfo();
            limitInfo.sendCount = 1;
            limitInfo.lastSendTime = currentTime;
            limitInfo.blockUntil = 0;
        RATE_LIMIT_STORE.put(account, limitInfo);
      } else {
         limitInfo.sendCount++;
            limitInfo.lastSendTime = currentTime;
        }
    }

    /**
     * 保存重置日志
     */
    private void saveResetLog(Long userId, String username, String account, String resetType, Integer status, String message) {
        PasswordResetLog log = new PasswordResetLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setResetType(resetType);
        log.setStatus(status);
        log.setMessage(message);
        log.setIpAddress("127.0.0.1"); // TODO: 获取真实IP
        log.setResetTime(LocalDateTime.now());
        resetLogMapper.insert(log);
  }
}
