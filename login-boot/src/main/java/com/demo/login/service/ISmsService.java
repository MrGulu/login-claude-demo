package com.demo.login.service;

/**
 * 短信服务接口
 */
public interface ISmsService {

    /**
     * 发送验证码短信
     * @param phone 手机号
     * @param code 验证码
     */
    void sendVerificationCode(String phone, String code);
}
