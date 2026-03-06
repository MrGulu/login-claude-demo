package com.demo.login.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色VO
 *
 * @author Claude
 * @since 2024-03-06
 */
@Data
public class RoleVO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 系统角色标记：0-否，1-是
     */
    private Integer isSystem;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
