package com.demo.login.controller;

import com.demo.login.common.result.Result;
import com.demo.login.dto.ResetPasswordDTO;
import com.demo.login.dto.SendVerificationCodeDTO;
import com.demo.login.dto.VerifyCodeDTO;
import com.demo.login.service.IPasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码重置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    @Autowired
    private IPasswordResetService passwordResetService;

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public Result<Void> sendVerificationCode(@Validated @RequestBody SendVerificationCodeDTO dto) {
        log.info("发送验证码: {}", dto.getAccount());
        passwordResetService.sendVerificationCode(dto);
        return Result.success("验证码已发送", null);
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify-code")
    public Result<Map<String, String>> verifyCode(@Validated @RequestBody VerifyCodeDTO dto) {
        log.info("验证验证码: {}", dto.getAccount());
        String resetToken = passwordResetService.verifyCodeAndGetToken(dto);
        Map<String, String> data = new HashMap<>();
        data.put("resetToken", resetToken);
     return Result.success("验证成功", data);
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset")
    public Result<Void> resetPassword(@Validated @RequestBody ResetPasswordDTO dto) {
        log.info("重置密码");
        passwordResetService.resetPassword(dto);
        return Result.success("密码重置成功", null);
    }
}
