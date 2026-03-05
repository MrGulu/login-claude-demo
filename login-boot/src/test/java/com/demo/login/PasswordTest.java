package com.demo.login;

import com.demo.login.common.utils.PasswordUtil;

public class PasswordTest {
    public static void main(String[] args) {
        String password = "123456";
        String encoded = PasswordUtil.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密密码: " + encoded);

        // 测试数据库中的密码
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi";
        boolean matches = PasswordUtil.matches(password, dbPassword);
        System.out.println("数据库密码: " + dbPassword);
        System.out.println("密码匹配: " + matches);
    }
}
