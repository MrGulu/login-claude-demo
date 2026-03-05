package com.demo.login.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.common.utils.JwtUtil;
import com.demo.login.common.utils.PasswordUtil;
import com.demo.login.dto.LoginDTO;
import com.demo.login.entity.LoginLog;
import com.demo.login.entity.User;
import com.demo.login.mapper.LoginLogMapper;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.IAuthService;
import com.demo.login.vo.LoginVO;
import com.demo.login.vo.UserInfoVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.StringRedisTemplate; // 注释掉Redis依赖
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.TimeUnit; // 注释掉，不再需要

/**
 * 认证服务实现类
 *
 * @author Claude
 * @since 2024-03-02
 */
@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // @Autowired // 注释掉Redis依赖
    // private StringRedisTemplate redisTemplate;

    // private static final String TOKEN_PREFIX = "login:token:"; // 注释掉，不再需要
    private static final long TOKEN_EXPIRE = 7200L; // 2小时

    // 使用ConcurrentHashMap替代Redis存储Token
    public static final Map<String, TokenInfo> TOKEN_STORE = new ConcurrentHashMap<>();

    /**
     * Token信息内部类
     */
    @Data
    public static class TokenInfo {
        private String userId;
        private long expireTime;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        // 用户不存在
        if (user == null) {
            saveLoginLog(null, loginDTO.getUsername(), 0, "用户不存在");
            throw new BusinessException(2001, "用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            saveLoginLog(user.getId(), loginDTO.getUsername(), 0, "密码错误");
            throw new BusinessException(2002, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            saveLoginLog(user.getId(), loginDTO.getUsername(), 0, "用户已被禁用");
            throw new BusinessException(2003, "用户已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        if (token == null) {
            throw new BusinessException(3004, "Token生成失败");
        }

        // 存储Token到内存Map (替代Redis)
        long expireTime = loginDTO.getRemember() != null && loginDTO.getRemember()
            ? TOKEN_EXPIRE * 7 : TOKEN_EXPIRE; // 记住我：14天，否则2小时
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(user.getId().toString());
        tokenInfo.setExpireTime(System.currentTimeMillis() + expireTime * 1000);
        TOKEN_STORE.put(token, tokenInfo);

        // 记录登录日志
        saveLoginLog(user.getId(), loginDTO.getUsername(), 1, "登录成功");

        // 构造返回数据
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(user, userInfoVO);
        loginVO.setUserInfo(userInfoVO);

        log.info("用户登录成功: {}", user.getUsername());
        return loginVO;
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException(3001, "Token不能为空");
        }

        // 从内存Map中删除Token (替代Redis)
        TokenInfo removed = TOKEN_STORE.remove(token);
        if (removed == null) {
            log.warn("Token不存在或已过期: {}", token);
        }

        log.info("用户登出成功");
    }

    @Override
    public UserInfoVO getUserInfo(String token) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException(3001, "Token不能为空");
        }

        // 验证Token
        if (!jwtUtil.verifyToken(token)) {
            throw new BusinessException(3002, "Token无效");
        }

        // 检查内存Map中是否存在 (替代Redis)
        TokenInfo tokenInfo = TOKEN_STORE.get(token);
        if (tokenInfo == null || tokenInfo.getExpireTime() < System.currentTimeMillis()) {
            throw new BusinessException(3003, "Token已过期");
        }
        String userId = tokenInfo.getUserId();

        // 查询用户信息
        User user = userMapper.selectById(Long.parseLong(userId));
        if (user == null) {
            throw new BusinessException(2001, "用户不存在");
        }

        // 转换为VO
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(user, userInfoVO);

        return userInfoVO;
    }

    @Override
    public String refreshToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException(3001, "Token不能为空");
        }

        // 验证旧Token
        if (!jwtUtil.verifyToken(token)) {
            throw new BusinessException(3002, "Token无效");
        }

        // 获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);

        if (userId == null || username == null) {
            throw new BusinessException(3002, "Token解析失败");
        }

        // 生成新Token
        String newToken = jwtUtil.generateToken(userId, username);
        if (newToken == null) {
            throw new BusinessException(3004, "Token生成失败");
        }

        // 删除旧Token (替代Redis)
        TOKEN_STORE.remove(token);

        // 存储新Token (替代Redis)
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(userId.toString());
        tokenInfo.setExpireTime(System.currentTimeMillis() + TOKEN_EXPIRE * 1000);
        TOKEN_STORE.put(newToken, tokenInfo);

        log.info("Token刷新成功: {}", username);
        return newToken;
    }

    /**
     * 保存登录日志
     */
    private void saveLoginLog(Long userId, String username, Integer status, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setId(System.currentTimeMillis()); // 使用时间戳作为ID
        loginLog.setUserId(userId);
        loginLog.setUsername(username);
        loginLog.setStatus(status);
        loginLog.setMessage(message);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setIpAddress("127.0.0.1"); // 实际项目中应该获取真实IP
        loginLog.setBrowser("Unknown");
        loginLog.setOs("Unknown");
        loginLogMapper.insert(loginLog);
    }
}
