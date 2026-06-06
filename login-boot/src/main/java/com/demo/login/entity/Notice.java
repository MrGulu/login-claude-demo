package com.demo.login.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体类
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Data
@TableName("sys_notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 发布人/作者
     */
    private String author;

    /**
     * 状态：0-关闭，1-正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 已读状态：0-未读，1-已读 (非持久化属性，供当前用户查询公告列表时展示使用)
     */
    @TableField(exist = false)
    private Integer readStatus;
}
