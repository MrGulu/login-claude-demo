package com.demo.login.controller;

import com.demo.login.common.result.Result;
import com.demo.login.service.ICaptchaService;
import com.demo.login.vo.CaptchaVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private ICaptchaService captchaService;

    /**
     * 生成验证码
     */
    @GetMapping("/generate")
    public Result<CaptchaVO> generateCaptcha() {
        log.info("生成图形验证码");
        CaptchaVO captcha = captchaService.generateCaptcha();
        return Result.success(captcha);
    }
}
