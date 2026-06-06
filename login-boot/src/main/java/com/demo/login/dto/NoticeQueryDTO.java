package com.demo.login.dto;

import lombok.Data;

/**
 * 公告查询DTO
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Data
public class NoticeQueryDTO {

    /**
     * 公告标题（模糊查询）
     */
    private String title;

    /**
     * 发布人（模糊查询）
     */
    private String author;

    /**
     * 状态：0-关闭，1-正常
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
