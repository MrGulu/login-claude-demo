package com.demo.login.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 验证码响应VO
 */
@Data
public class CaptchaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证码唯一标识
     */
    private String captchaKey;

    /**
     * Base64编码的验证码图片
     */
    private String captchaImage;
}
