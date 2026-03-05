package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 验证原密码DTO
 */
@Data
public class VerifyOldPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;
}
