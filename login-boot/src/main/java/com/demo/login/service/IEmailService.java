package com.demo.login.service;

/**
 * 邮件服务接口
 */
public interface IEmailService {

    /**
     * 发送验证码邮件
     * @param email 邮箱地址
     * @param code 验证码
     */
    void sendVerificationCode(String email, String code);
}
