package com.demo.login.service.impl;

import com.demo.login.service.ISmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现类（模拟实现）
 */
@Slf4j
@Service
public class SmsServiceImpl implements ISmsService {

    @Override
    public void sendVerificationCode(String phone, String code) {
        // 模拟发送短信（控制台打印）
        log.info("===================");
        log.info("【短信服务】发送验证码");
        log.info("接收手机号: {}", phone);
        log.info("验证码内容: {}", code);
        log.info("有效期: 5分钟");
        log.info("=============");

        // TODO: 后期对接真实短信服务商（阿里云、腾讯云等）
        // 示例代码：
        // SmsClient client = new SmsClient(accessKey, secretKey);
        // client.sendSms(phone, templateId, Map.of("code", code));
    }
}
