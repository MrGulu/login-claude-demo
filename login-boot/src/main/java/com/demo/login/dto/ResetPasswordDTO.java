package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 重置密码DTO
 */
@Data
public class ResetPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "重置令牌不能为空")
    private String resetToken;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
