package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新用户状态参数
 *
 * @author Claude
 * @since 2024-03-04
 */
@Data
public class UpdateUserStatusDTO {

    /**
     * 状态：0-禁用，1-正常
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
