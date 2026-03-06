package com.demo.login.dto;

import lombok.Data;

/**
 * 角色查询DTO
 *
 * @author Claude
 * @since 2024-03-06
 */
@Data
public class RoleQueryDTO {

    /**
     * 角色名称（模糊查询）
     */
    private String roleName;

    /**
     * 角色标识（模糊查询）
     */
    private String roleKey;

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
