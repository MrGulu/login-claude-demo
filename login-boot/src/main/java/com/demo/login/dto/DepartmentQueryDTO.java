package com.demo.login.dto;

import lombok.Data;

/**
 * 部门查询DTO
 *
 * @author Claude
 * @since 2026-06-09
 */
@Data
public class DepartmentQueryDTO {

    /**
     * 部门名称（模糊查询）
     */
    private String deptName;

    /**
     * 部门状态：0-停用，1-正常
     */
    private Integer status;
}
