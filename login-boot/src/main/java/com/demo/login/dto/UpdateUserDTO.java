package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * 更新用户参数
 *
 * @author Claude
 * @since 2024-03-04
 */
@Data
public class UpdateUserDTO {

    /**
     * 密码（选填，为空则不修改）
     */
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 状态：0-禁用，1-正常
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
