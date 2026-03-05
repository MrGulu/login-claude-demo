package com.demo.login.service;

import com.demo.login.dto.LoginDTO;
import com.demo.login.entity.User;
import com.demo.login.mapper.LoginLogMapper;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.impl.AuthServiceImpl;
import com.demo.login.vo.LoginVO;
import com.demo.login.vo.UserInfoVO;
import com.demo.login.common.utils.JwtUtil;
import com.demo.login.common.utils.PasswordUtil;
import com.demo.login.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.redis.core.StringRedisTemplate; // 注释掉Redis依赖
// import org.springframework.data.redis.core.ValueOperations; // 注释掉Redis依赖

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 认证服务测试类
 *
 * @author Claude
 * @since 2024-03-02
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private LoginLogMapper loginLogMapper;

    @Mock
    private JwtUtil jwtUtil;

    // @Mock // 注释掉Redis相关Mock
    // private StringRedisTemplate redisTemplate;

    // @Mock // 注释掉Redis相关Mock
    // private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword(PasswordUtil.encode("123456"));
        testUser.setNickname("管理员");
        testUser.setStatus(1);

        loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("123456");
        loginDTO.setRemember(false);

        // Mock Redis操作 (注释掉，不再需要)
        // when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testLoginSuccess() {
        // Mock数据库查询
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // Mock JWT生成
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("test-token");

        // Mock Redis存储 (注释掉，不再需要)
        // doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any());

        // Mock日志插入
        when(loginLogMapper.insert(any())).thenReturn(1);

        // 执行登录
        LoginVO result = authService.login(loginDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        assertNotNull(result.getUserInfo());
        assertEquals("admin", result.getUserInfo().getUsername());

        // 验证方法调用
        verify(userMapper, times(1)).selectOne(any());
        verify(jwtUtil, times(1)).generateToken(anyLong(), anyString());
        verify(loginLogMapper, times(1)).insert(any());
    }

    @Test
    void testLoginWithWrongPassword() {
        // Mock数据库查询
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // Mock日志插入
        when(loginLogMapper.insert(any())).thenReturn(1);

        // 修改密码为错误密码
        loginDTO.setPassword("wrong-password");

        // 执行登录并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals(2002, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void testLoginWithNonExistUser() {
        // Mock数据库查询返回null
        when(userMapper.selectOne(any())).thenReturn(null);

        // Mock日志插入
        when(loginLogMapper.insert(any())).thenReturn(1);

        // 执行登录并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals(2001, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void testLoginWithDisabledUser() {
        // 设置用户为禁用状态
        testUser.setStatus(0);

        // Mock数据库查询
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // Mock日志插入
        when(loginLogMapper.insert(any())).thenReturn(1);

        // 执行登录并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals(2003, exception.getCode());
        assertEquals("用户已被禁用", exception.getMessage());
    }

    @Test
    void testLogout() {
        String token = "test-token";

        // Mock Redis删除 (注释掉，不再需要)
        // when(redisTemplate.delete(anyString())).thenReturn(true);

        // 执行登出
        assertDoesNotThrow(() -> authService.logout(token));

        // 验证方法调用 (注释掉，不再需要)
        // verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    void testGetUserInfoWithValidToken() {
        // 先执行登录获取真实Token
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("test-token");
        when(loginLogMapper.insert(any())).thenReturn(1);

        LoginVO loginResult = authService.login(loginDTO);
        String token = loginResult.getToken();

        // Mock Token验证和解析
        when(jwtUtil.verifyToken(token)).thenReturn(true);
        when(userMapper.selectById(1L)).thenReturn(testUser);

     // 执行获取用户信息
        UserInfoVO userInfo = authService.getUserInfo(token);

        // 验证结果
        assertNotNull(userInfo);
        assertEquals("admin", userInfo.getUsername());
        assertEquals("管理员", userInfo.getNickname());
    }

    @Test
    void testGetUserInfoWithInvalidToken() {
        String token = "invalid-token";

        // Mock Token验证失败
        when(jwtUtil.verifyToken(token)).thenReturn(false);

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.getUserInfo(token);
        });

        assertEquals(3002, exception.getCode());
        assertEquals("Token无效", exception.getMessage());
    }

    @Test
    void testGetUserInfoWithExpiredToken() {
        String token = "expired-token";

        // Mock Token验证通过但已过期（不在内存中）
        when(jwtUtil.verifyToken(token)).thenReturn(true);

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.getUserInfo(token);
        });

        assertEquals(3003, exception.getCode());
        assertEquals("Token已过期", exception.getMessage());
    }

    @Test
    void testRefreshTokenSuccess() {
        // 先执行登录获取真实Token
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("old-token");
        when(loginLogMapper.insert(any())).thenReturn(1);

        LoginVO loginResult = authService.login(loginDTO);
        String oldToken = loginResult.getToken();

        // Mock Token验证和解析
        when(jwtUtil.verifyToken(oldToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(oldToken)).thenReturn(1L);
        when(jwtUtil.getUsernameFromToken(oldToken)).thenReturn("admin");
        when(jwtUtil.generateToken(1L, "admin")).thenReturn("new-token");

        // 执行刷新Token
        String newToken = authService.refreshToken(oldToken);

        // 验证结果
        assertNotNull(newToken);
        assertEquals("new-token", newToken);

        // 验证旧Token已被删除（再次使用会失败）
        when(jwtUtil.verifyToken(oldToken)).thenReturn(true);
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.getUserInfo(oldToken);
        });
        assertEquals(3003, exception.getCode());
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        String token = "invalid-token";

        // Mock Token验证失败
        when(jwtUtil.verifyToken(token)).thenReturn(false);

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.refreshToken(token);
        });

        assertEquals(3002, exception.getCode());
        assertEquals("Token无效", exception.getMessage());
    }

    @Test
    void testLoginWithRememberMe() {
        // 设置记住我
        loginDTO.setRemember(true);

        // Mock数据库查询
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("test-token");
        when(loginLogMapper.insert(any())).thenReturn(1);

        // 执行登录
        LoginVO result = authService.login(loginDTO);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getToken());

        // 验证Token可以正常使用
        when(jwtUtil.verifyToken("test-token")).thenReturn(true);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        UserInfoVO userInfo = authService.getUserInfo("test-token");
        assertNotNull(userInfo);
     assertEquals("admin", userInfo.getUsername());
    }
}
