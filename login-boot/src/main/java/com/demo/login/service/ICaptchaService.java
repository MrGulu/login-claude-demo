package com.demo.login.service;

import com.demo.login.vo.CaptchaVO;

/**
 * 验证码服务接口
 */
public interface ICaptchaService {

    /**
     * 生成验证码
     * @return 验证码VO
     */
    CaptchaVO generateCaptcha();

    /**
     * 验证验证码
     * @param captchaKey 验证码Key
     * @param captcha 验证码
     * @return 是否验证通过
     */
    boolean verifyCaptcha(String captchaKey, String captcha);
}
