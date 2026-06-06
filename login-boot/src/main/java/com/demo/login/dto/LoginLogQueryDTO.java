package com.demo.login.dto;

import lombok.Data;

/**
 * 登录日志查询DTO
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Data
public class LoginLogQueryDTO {

    /**
     * 用户名（模糊匹配）
     */
    private String username;

    /**
     * 状态：0-失败，1-成功
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
