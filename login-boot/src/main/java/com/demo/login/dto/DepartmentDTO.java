package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 部门DTO
 *
 * @author Claude
 * @since 2026-06-09
 */
@Data
public class DepartmentDTO {

    /**
     * 父部门ID
     */
    private Long parentId = 0L;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 部门状态：0-停用，1-正常
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
