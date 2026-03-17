package com.demo.login.dto;

import lombok.Data;

/**
 * 岗位查询DTO
 *
 * @author Claude
 * @since 2026-03-13
 */
@Data
public class PositionQueryDTO {

    /**
     * 岗位名称（模糊查询）
     */
    private String positionName;

    /**
     * 岗位编码（模糊查询）
     */
    private String positionCode;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
