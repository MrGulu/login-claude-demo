package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 岗位DTO
 *
 * @author Claude
 * @since 2026-03-13
 */
@Data
public class PositionDTO {

    /**
     * 岗位名称
     */
    @NotBlank(message = "岗位名称不能为空")
    private String positionName;

    /**
     * 岗位编码
     */
    @NotBlank(message = "岗位编码不能为空")
    private String positionCode;

    /**
     * 状态：0-禁用，1-正常
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
