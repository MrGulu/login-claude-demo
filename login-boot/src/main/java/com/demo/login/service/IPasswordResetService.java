package com.demo.login.service;

import com.demo.login.dto.ResetPasswordDTO;
import com.demo.login.dto.SendVerificationCodeDTO;
import com.demo.login.dto.VerifyCodeDTO;

/**
 * 密码重置服务接口
 */
public interface IPasswordResetService {

    /**
     * 发送验证码
     * @param dto 发送验证码请求
   */
    void sendVerificationCode(SendVerificationCodeDTO dto);

    /**
     * 验证验证码并获取重置令牌
     * @param dto 验证码验证请求
     * @return 重置令牌
     */
    String verifyCodeAndGetToken(VerifyCodeDTO dto);

    /**
     * 重置密码
     * @param dto 重置密码请求
   */
    void resetPassword(ResetPasswordDTO dto);
}
