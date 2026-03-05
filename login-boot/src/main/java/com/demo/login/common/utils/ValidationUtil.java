package com.demo.login.common.utils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidationUtil {

    /**
     * 手机号正则：1开头，第二位3-9，后面9位数字
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱正则：基本格式验证
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    /**
     * 密码长度正则：6-20位
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,20}$");

    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * 验证邮箱格式
     * @param email 邮箱
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
      if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

      /**
     * 验证密码长度
     * @param password 密码
     * @return 是否符合要求（6-20位）
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
      return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 判断账号是手机号还是邮箱
     * @param account 账号
     * @return "phone" / "email" / null
     */
    public static String getAccountType(String account) {
        if (isValidPhone(account)) {
            return "phone";
        } else if (isValidEmail(account)) {
      return "email";
        }
        return null;
    }
}
