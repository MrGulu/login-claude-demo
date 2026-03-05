package com.demo.login.controller;

import com.demo.login.dto.CreateUserDTO;
import com.demo.login.dto.UpdateUserDTO;
import com.demo.login.dto.UpdateUserStatusDTO;
import com.demo.login.dto.UserQueryDTO;
import com.demo.login.service.IUserManagementService;
import com.demo.login.vo.PageResult;
import com.demo.login.vo.UserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户管理控制器测试类
 *
 * @author Claude
 * @since 2024-03-04
 */
@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUserManagementService userManagementService;

    private UserVO testUserVO;

    @BeforeEach
    void setUp() {
        testUserVO = new UserVO();
        testUserVO.setId(1L);
        testUserVO.setUsername("testuser");
        testUserVO.setNickname("测试用户");
        testUserVO.setEmail("test@example.com");
        testUserVO.setPhone("13800138000");
        testUserVO.setStatus(1);
        testUserVO.setCreateTime("2024-03-04 10:00:00");
        testUserVO.setUpdateTime("2024-03-04 10:00:00");
        testUserVO.setRemark("测试账号");
    }

    @Test
    void testGetUserList() throws Exception {
        // 准备测试数据
        PageResult<UserVO> pageResult = new PageResult<>(1L, Arrays.asList(testUserVO));
        when(userManagementService.getUserList(any(UserQueryDTO.class))).thenReturn(pageResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/users")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value("testuser"));

        verify(userManagementService, times(1)).getUserList(any(UserQueryDTO.class));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userManagementService.getUserById(1L)).thenReturn(testUserVO);

        // 执行测试
        mockMvc.perform(get("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userManagementService, times(1)).getUserById(1L);
    }

    @Test
    void testCreateUser() throws Exception {
        // 准备测试数据
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("newuser");
        dto.setPassword("123456");
        dto.setNickname("新用户");
        dto.setEmail("new@example.com");
        dto.setPhone("13900139000");
        dto.setStatus(1);
        dto.setRemark("新账号");

        doNothing().when(userManagementService).createUser(any(CreateUserDTO.class));

        // 执行测试
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userManagementService, times(1)).createUser(any(CreateUserDTO.class));
    }

    @Test
    void testCreateUser_ValidationFailed() throws Exception {
        // 准备测试数据（缺少必填字段）
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("ab"); // 用户名太短

        // 执行测试
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(userManagementService, never()).createUser(any(CreateUserDTO.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        // 准备测试数据
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setNickname("更新昵称");
        dto.setEmail("update@example.com");
        dto.setPhone("13900139000");
        dto.setStatus(1);

        doNothing().when(userManagementService).updateUser(eq(1L), any(UpdateUserDTO.class));

        // 执行测试
        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userManagementService, times(1)).updateUser(eq(1L), any(UpdateUserDTO.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userManagementService).deleteUser(1L);

        // 执行测试
        mockMvc.perform(delete("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userManagementService, times(1)).deleteUser(1L);
    }

    @Test
    void testUpdateUserStatus() throws Exception {
        // 准备测试数据
        UpdateUserStatusDTO dto = new UpdateUserStatusDTO();
        dto.setStatus(0);

        doNothing().when(userManagementService).updateUserStatus(1L, 0);

        // 执行测试
        mockMvc.perform(put("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userManagementService, times(1)).updateUserStatus(1L, 0);
    }
}
