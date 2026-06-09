package com.demo.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.dto.DepartmentDTO;
import com.demo.login.dto.DepartmentQueryDTO;
import com.demo.login.entity.Department;
import com.demo.login.entity.User;
import com.demo.login.mapper.DepartmentMapper;
import com.demo.login.mapper.UserMapper;
import com.demo.login.service.IDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 部门服务实现类
 *
 * @author Claude
 * @since 2026-06-09
 */
@Slf4j
@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Department> getDepartmentList(DepartmentQueryDTO queryDTO) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getDeptName())) {
            wrapper.like(Department::getDeptName, queryDTO.getDeptName());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Department::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(Department::getSort);
        return departmentMapper.selectList(wrapper);
    }

    @Override
    public Department getDepartmentById(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        return dept;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDepartment(DepartmentDTO departmentDTO) {
        // 如果有 parentId 且不是 0，校验父部门是否存在并启用
        if (departmentDTO.getParentId() != null && departmentDTO.getParentId() != 0) {
            Department parent = departmentMapper.selectById(departmentDTO.getParentId());
            if (parent == null || parent.getStatus() != 1) {
                throw new BusinessException("父部门不存在或已被禁用");
            }
        }

        Department dept = new Department();
        BeanUtils.copyProperties(departmentDTO, dept);
        departmentMapper.insert(dept);

        log.info("创建部门成功，部门ID: {}", dept.getId());
        return dept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 树结构循环引用防护：父部门不能选择自己，也不能选择自己的子部门
        if (departmentDTO.getParentId() != null && departmentDTO.getParentId() != 0) {
            if (departmentDTO.getParentId().equals(id)) {
                throw new BusinessException("父部门不能选择自己");
            }
            // 检查是否选择了自己的子部门作为父部门
            if (isChildDepartment(id, departmentDTO.getParentId())) {
                throw new BusinessException("父部门不能选择自己的子部门");
            }
            // 校验父部门是否存在且正常
            Department parent = departmentMapper.selectById(departmentDTO.getParentId());
            if (parent == null || parent.getStatus() != 1) {
                throw new BusinessException("父部门不存在或已被禁用");
            }
        }

        BeanUtils.copyProperties(departmentDTO, dept);
        departmentMapper.updateById(dept);

        log.info("更新部门成功，部门ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 校验是否有子部门
        LambdaQueryWrapper<Department> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Department::getParentId, id);
        if (departmentMapper.selectCount(childWrapper) > 0) {
            throw new BusinessException("存在子部门，无法删除");
        }

        // 校验是否有绑定该部门的用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getDeptId, id);
        if (userMapper.selectCount(userWrapper) > 0) {
            throw new BusinessException("该部门下有绑定用户，无法删除");
        }

        departmentMapper.deleteById(id);
        log.info("删除部门成功，部门ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartmentStatus(Long id, Integer status) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        dept.setStatus(status);
        departmentMapper.updateById(dept);

        log.info("更新部门状态成功，部门ID: {}, 状态: {}", id, status);
    }

    /**
     * 判断 parentId 是否是 deptId 的子部门/后代部门
     */
    private boolean isChildDepartment(Long deptId, Long parentId) {
        Long currentParentId = parentId;
        while (currentParentId != null && currentParentId != 0) {
            Department dept = departmentMapper.selectById(currentParentId);
            if (dept == null) {
                break;
            }
            if (dept.getParentId().equals(deptId)) {
                return true;
            }
            currentParentId = dept.getParentId();
        }
        return false;
    }
}
