package com.demo.login.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 密码重置日志实体
 */
@Data
@TableName("sys_password_reset_log")
public class PasswordResetLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String username;

    private String resetType;

    private String ipAddress;

    private Integer status;

    private String message;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime resetTime;
}
