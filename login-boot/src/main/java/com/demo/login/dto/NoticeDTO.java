package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公告DTO
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Data
public class NoticeDTO {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;

    /**
     * 状态：0-关闭，1-正常
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
