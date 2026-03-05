package com.demo.login.service.impl;

import com.demo.login.service.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类（模拟实现）
 */
@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {

    @Override
    public void sendVerificationCode(String email, String code) {
        // 模拟发送邮件（控制台打印）
        log.info("=====================");
        log.info("【邮件服务】发送验证码");
        log.info("接收邮箱: {}", email);
        log.info("验证码内容: {}", code);
        log.info("有效期: 5分钟");
        log.info("===========");

        // TODO: 后期对接真实邮件服务
        // 示例代码：
        // JavaMailSender mailSender = ...;
        // MimeMessage message = mailSender.createMimeMessage();
        // MimeMessageHelper helper = new MimeMessageHelper(message, true);
    // helper.setTo(email);
        // helper.setSubject("密码重置验证码");
        // helper.setText("您的验证码是：" + code + "，5分钟内有效");
     // mailSender.send(message);
    }
}
