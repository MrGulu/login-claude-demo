package com.demo.login.service.impl;

import cn.hutool.core.util.IdUtil;
import com.demo.login.common.utils.CaptchaUtil;
import com.demo.login.service.ICaptchaService;
import com.demo.login.vo.CaptchaVO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 */
@Service
public class CaptchaServiceImpl implements ICaptchaService {

    // 验证码存储（实际项目建议用Redis）
    private static final Map<String, CaptchaInfo> CAPTCHA_STORE = new ConcurrentHashMap<>();

    private static class CaptchaInfo {
        String code;
        long expireTime;
    }

    @Override
    public CaptchaVO generateCaptcha() {
        String code = CaptchaUtil.generateCode();
        String key = IdUtil.simpleUUID();

        CaptchaInfo info = new CaptchaInfo();
        info.code = code;
        info.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);

        CAPTCHA_STORE.put(key, info);

        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaKey(key);
        vo.setCaptchaImage(CaptchaUtil.generateImage(code));

        return vo;
    }

    @Override
    public boolean verifyCaptcha(String captchaKey, String captcha) {
        CaptchaInfo captchaInfo = CAPTCHA_STORE.get(captchaKey);
        if (captchaInfo == null || captchaInfo.expireTime < System.currentTimeMillis()) {
            return false;
        }
        if (!captchaInfo.code.equalsIgnoreCase(captcha)) {
            return false;
        }
        // 验证成功后删除验证码
        CAPTCHA_STORE.remove(captchaKey);
        return true;
    }
}
