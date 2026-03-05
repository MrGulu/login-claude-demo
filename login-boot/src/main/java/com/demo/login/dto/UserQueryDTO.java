package com.demo.login.dto;

import lombok.Data;

/**
 * 用户查询参数
 *
 * @author Claude
 * @since 2024-03-04
 */
@Data
public class UserQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 状态：0-禁用，1-正常，null-全部
     */
    private Integer status;
}
