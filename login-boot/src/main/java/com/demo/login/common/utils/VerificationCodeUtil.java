package com.demo.login.common.utils;

import java.util.Random;

/**
 * 验证码生成工具类
 */
public class VerificationCodeUtil {

    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();

    /**
     * 生成6位数字验证码
     * @return 验证码字符串（000000-999999）
     */
    public static String generateNumericCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(RANDOM.nextInt(10));
        }
        return code.toString();
    }
}
