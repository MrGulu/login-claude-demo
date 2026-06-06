package com.demo.login.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告已读关联实体类
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Data
@TableName("sys_notice_read")
public class NoticeRead implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 已读时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime readTime;
}
