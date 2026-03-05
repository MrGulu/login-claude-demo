package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 验证验证码DTO
 */
@Data
public class VerifyCodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "手机号或邮箱不能为空")
    private String account;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须为6位数字")
    private String verificationCode;
}
