package com.demo.login.service;

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
import com.demo.login.service.impl.UserManagementServiceImpl;
import com.demo.login.vo.PageResult;
import com.demo.login.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户管理服务测试类
 *
 * @author Claude
 * @since 2024-03-04
 */
@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(PasswordUtil.encode("123456"));
        testUser.setNickname("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
        testUser.setRemark("测试账号");
    }

    @Test
    void testGetUserList_Success() {
        // 准备测试数据
        UserQueryDTO dto = new UserQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<User> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testUser));
        page.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        // 执行测试
        PageResult<UserVO> result = userManagementService.getUserList(dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("testuser", result.getList().get(0).getUsername());

        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetUserList_WithSearch() {
        // 准备测试数据
        UserQueryDTO dto = new UserQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setUsername("test");
        dto.setPhone("138");
        dto.setStatus(1);

        Page<User> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testUser));
        page.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        // 执行测试
        PageResult<UserVO> result = userManagementService.getUserList(dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());

        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetUserById_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // 执行测试
        UserVO result = userManagementService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getNickname());

        verify(userMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.getUserById(999L);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, times(1)).selectById(999L);
    }

    @Test
    void testCreateUser_Success() {
        // 准备测试数据
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("newuser");
        dto.setPassword("123456");
        dto.setNickname("新用户");
        dto.setEmail("new@example.com");
        dto.setPhone("13900139000");
        dto.setStatus(1);
        dto.setRemark("新账号");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userManagementService.createUser(dto));

        verify(userMapper, times(3)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testCreateUser_DuplicateUsername() {
        // 准备测试数据
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setStatus(1);

        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.createUser(dto);
        });

        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testCreateUser_DuplicateEmail() {
        // 准备测试数据
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("newuser");
        dto.setPassword("123456");
        dto.setEmail("test@example.com");
        dto.setStatus(1);

        when(userMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(0L)  // 用户名不重复
                .thenReturn(1L); // 邮箱重复

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.createUser(dto);
        });

        assertEquals("邮箱已被使用", exception.getMessage());
        verify(userMapper, times(2)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testCreateUser_DuplicatePhone() {
        // 准备测试数据
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("newuser");
        dto.setPassword("123456");
        dto.setPhone("13800138000");
        dto.setStatus(1);

        when(userMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(0L)  // 用户名不重复
                .thenReturn(1L); // 手机号重复

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.createUser(dto);
        });

        assertEquals("手机号已被使用", exception.getMessage());
        verify(userMapper, times(2)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        // 准备测试数据
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setNickname("更新昵称");
        dto.setEmail("update@example.com");
        dto.setPhone("13900139000");
        dto.setStatus(1);

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userManagementService.updateUser(1L, dto));

        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(2)).selectCount(any(LambdaQueryWrapper.class));
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_WithPassword() {
        // 准备测试数据
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setPassword("newpassword");
        dto.setStatus(1);

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userManagementService.updateUser(1L, dto));

        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_WithoutPassword() {
        // 准备测试数据
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setNickname("更新昵称");
        dto.setStatus(1);

        String originalPassword = testUser.getPassword();

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userManagementService.updateUser(1L, dto));

        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setStatus(1);

        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.updateUser(999L, dto);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, times(1)).selectById(999L);
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.deleteById(1L)).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userManagementService.deleteUser(1L));

        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.deleteUser(999L);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, times(1)).selectById(999L);
        verify(userMapper, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateUserStatus_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userManagementService.updateUserStatus(1L, 0));

        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateUserStatus_NotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.updateUserStatus(999L, 0);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, times(1)).selectById(999L);
        verify(userMapper, never()).updateById(any(User.class));
    }
}
