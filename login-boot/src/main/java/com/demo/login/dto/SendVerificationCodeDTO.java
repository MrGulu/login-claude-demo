package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 发送验证码DTO
 */
@Data
public class SendVerificationCodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "手机号或邮箱不能为空")
    private String account;
}
