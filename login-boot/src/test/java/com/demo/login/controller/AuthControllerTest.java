package com.demo.login.controller;

import com.demo.login.dto.LoginDTO;
import com.demo.login.service.IAuthService;
import com.demo.login.vo.LoginVO;
import com.demo.login.vo.UserInfoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器测试类
 *
 * @author Claude
 * @since 2024-03-02
 */
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAuthService authService;

    private LoginVO loginVO;
    private UserInfoVO userInfoVO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        userInfoVO = new UserInfoVO();
        userInfoVO.setId(1L);
        userInfoVO.setUsername("admin");
        userInfoVO.setNickname("管理员");
        userInfoVO.setEmail("admin@example.com");

        loginVO = new LoginVO();
        loginVO.setToken("test-token");
        loginVO.setUserInfo(userInfoVO);
    }

    @Test
    void testLogin() throws Exception {
        // Mock服务层
        when(authService.login(any(LoginDTO.class))).thenReturn(loginVO);

        // 准备请求数据
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("123456");
        loginDTO.setRemember(false);

        // 执行请求并验证
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").value("test-token"))
                .andExpect(jsonPath("$.data.userInfo.username").value("admin"));
    }

    @Test
    void testLoginWithInvalidParams() throws Exception {
        // 准备无效的请求数据（用户名为空）
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("");
        loginDTO.setPassword("123456");

        // 执行请求并验证
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001));
    }

    @Test
    void testLogout() throws Exception {
        // 执行请求并验证
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    void testGetUserInfo() throws Exception {
        // Mock服务层
        when(authService.getUserInfo(anyString())).thenReturn(userInfoVO);

        // 执行请求并验证
        mockMvc.perform(get("/api/auth/userinfo")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.nickname").value("管理员"));
    }

    @Test
    void testRefreshToken() throws Exception {
        // Mock服务层
        when(authService.refreshToken(anyString())).thenReturn("new-test-token");

        // 执行请求并验证
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("new-test-token"));
    }
}
