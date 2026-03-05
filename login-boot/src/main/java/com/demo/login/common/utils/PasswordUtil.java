package com.demo.login.common.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码工具类
 *
 * @author Claude
 * @since 2024-03-02
 */
public class PasswordUtil {

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    /**
     * 验证密码
     *
     * @param password 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String password, String encodedPassword) {
        return BCrypt.checkpw(password, encodedPassword);
    }

    /**
     * 生成测试密码（用于测试）
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String password = "123456";
        String encoded = encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密密码: " + encoded);
        System.out.println("验证结果: " + matches(password, encoded));
    }
}
