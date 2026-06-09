package com.demo.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.dto.DepartmentDTO;
import com.demo.login.dto.DepartmentQueryDTO;
import com.demo.login.entity.Department;
import com.demo.login.entity.User;
import com.demo.login.mapper.DepartmentMapper;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 部门服务单元测试类
 *
 * @author Claude
 * @since 2026-06-09
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department dept1;
    private Department dept2;

    @BeforeEach
    void setUp() {
        dept1 = new Department();
        dept1.setId(1L);
        dept1.setParentId(0L);
        dept1.setDeptName("总公司");
        dept1.setSort(1);
        dept1.setStatus(1);

        dept2 = new Department();
        dept2.setId(2L);
        dept2.setParentId(1L);
        dept2.setDeptName("研发部门");
        dept2.setSort(1);
        dept2.setStatus(1);
    }

    @Test
    void testGetDepartmentList() {
        DepartmentQueryDTO queryDTO = new DepartmentQueryDTO();
        queryDTO.setDeptName("研发");
        queryDTO.setStatus(1);

        when(departmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(dept2));

        List<Department> result = departmentService.getDepartmentList(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("研发部门", result.get(0).getDeptName());
        verify(departmentMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetDepartmentById_Success() {
        when(departmentMapper.selectById(1L)).thenReturn(dept1);

        Department result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals("总公司", result.getDeptName());
    }

    @Test
    void testGetDepartmentById_NotFound() {
        when(departmentMapper.selectById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.getDepartmentById(999L);
        });

        assertEquals("部门不存在", exception.getMessage());
    }

    @Test
    void testCreateDepartment_SuccessWithoutParent() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setParentId(0L);
        dto.setDeptName("新部门");
        dto.setStatus(1);

        when(departmentMapper.insert(any(Department.class))).thenAnswer(invocation -> {
            Department dept = invocation.getArgument(0);
            dept.setId(10L);
            return 1;
        });

        Long deptId = departmentService.createDepartment(dto);

        assertEquals(10L, deptId);
        verify(departmentMapper, times(1)).insert(any(Department.class));
        verify(departmentMapper, never()).selectById(anyLong());
    }

    @Test
    void testCreateDepartment_SuccessWithParent() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setParentId(1L);
        dto.setDeptName("研发二组");
        dto.setStatus(1);

        when(departmentMapper.selectById(1L)).thenReturn(dept1);
        when(departmentMapper.insert(any(Department.class))).thenAnswer(invocation -> {
            Department dept = invocation.getArgument(0);
            dept.setId(11L);
            return 1;
        });

        Long deptId = departmentService.createDepartment(dto);

        assertEquals(11L, deptId);
        verify(departmentMapper, times(1)).selectById(1L);
        verify(departmentMapper, times(1)).insert(any(Department.class));
    }

    @Test
    void testCreateDepartment_ParentNotExistOrDisabled() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setParentId(999L);
        dto.setDeptName("新部门");
        dto.setStatus(1);

        when(departmentMapper.selectById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.createDepartment(dto);
        });

        assertEquals("父部门不存在或已被禁用", exception.getMessage());
        verify(departmentMapper, never()).insert(any(Department.class));
    }

    @Test
    void testUpdateDepartment_Success() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setParentId(1L);
        dto.setDeptName("研发二组新名称");
        dto.setStatus(1);

        when(departmentMapper.selectById(2L)).thenReturn(dept2);
        when(departmentMapper.selectById(1L)).thenReturn(dept1);
        when(departmentMapper.updateById(any(Department.class))).thenReturn(1);

        assertDoesNotThrow(() -> departmentService.updateDepartment(2L, dto));

        verify(departmentMapper, times(1)).updateById(any(Department.class));
    }

    @Test
    void testUpdateDepartment_ParentSelectSelf() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setParentId(2L);
        dto.setDeptName("研发部门");
        dto.setStatus(1);

        when(departmentMapper.selectById(2L)).thenReturn(dept2);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.updateDepartment(2L, dto);
        });

        assertEquals("父部门不能选择自己", exception.getMessage());
        verify(departmentMapper, never()).updateById(any(Department.class));
    }

    @Test
    void testUpdateDepartment_ParentSelectChild() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setParentId(2L); // 将总公司的父部门设为研发部门（研发部门原来是总公司的子部门）
        dto.setDeptName("总公司新名称");
        dto.setStatus(1);

        when(departmentMapper.selectById(1L)).thenReturn(dept1);
        // isChildDepartment 递归逻辑中会根据 parentId 往上查：
        // 研发部门(2L) -> parentId为总公司(1L)，总公司(1L)->parentId为0。这期间触碰了1L，即为子部门。
        when(departmentMapper.selectById(2L)).thenReturn(dept2);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.updateDepartment(1L, dto);
        });

        assertEquals("父部门不能选择自己的子部门", exception.getMessage());
        verify(departmentMapper, never()).updateById(any(Department.class));
    }

    @Test
    void testDeleteDepartment_Success() {
        when(departmentMapper.selectById(2L)).thenReturn(dept2);
        when(departmentMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(0L) // 无子部门
                .thenReturn(0L); // 无用户绑定
        when(departmentMapper.deleteById(2L)).thenReturn(1);

        assertDoesNotThrow(() -> departmentService.deleteDepartment(2L));

        verify(departmentMapper, times(1)).deleteById(2L);
    }

    @Test
    void testDeleteDepartment_HasChildren() {
        when(departmentMapper.selectById(1L)).thenReturn(dept1);
        when(departmentMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(1L); // 存在子部门

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.deleteDepartment(1L);
        });

        assertEquals("存在子部门，无法删除", exception.getMessage());
        verify(departmentMapper, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteDepartment_HasUsers() {
        when(departmentMapper.selectById(2L)).thenReturn(dept2);
        when(departmentMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(0L) // 无子部门
                .thenReturn(1L); // 存在用户绑定，这里 mock 两个不同的 count 调用，因为是两个不同的 mapper：
        // 第一个是 departmentMapper.selectCount, 返回 0
        // 第二个是 userMapper.selectCount, 返回 1
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.deleteDepartment(2L);
        });

        assertEquals("该部门下有绑定用户，无法删除", exception.getMessage());
        verify(departmentMapper, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateDepartmentStatus_Success() {
        when(departmentMapper.selectById(1L)).thenReturn(dept1);
        when(departmentMapper.updateById(any(Department.class))).thenReturn(1);

        assertDoesNotThrow(() -> departmentService.updateDepartmentStatus(1L, 0));

        assertEquals(0, dept1.getStatus());
        verify(departmentMapper, times(1)).updateById(dept1);
    }
}
