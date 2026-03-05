package com.demo.login.common.enums;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author Claude
 * @since 2024-03-02
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),

    // 参数相关 1xxx
    PARAM_ERROR(1001, "参数错误"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),

    // 用户相关 2xxx
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_PASSWORD_ERROR(2002, "用户名或密码错误"),
    USER_DISABLED(2003, "用户已被禁用"),
    USER_LOCKED(2004, "用户已被锁定"),
    USER_ALREADY_EXIST(2005, "用户已存在"),

    // 认证相关 3xxx
    UNAUTHORIZED(3001, "未授权"),
    TOKEN_INVALID(3002, "Token无效"),
    TOKEN_EXPIRED(3003, "Token已过期"),
    LOGIN_FAILED(3004, "登录失败"),
    LOGOUT_FAILED(3005, "登出失败"),

    // 密码重置相关 4xxx
    CAPTCHA_EXPIRED(4001, "验证码已过期"),
    CAPTCHA_ERROR(4002, "验证码错误"),
    OLD_PASSWORD_ERROR(4003, "原密码错误"),
    PASSWORD_NOT_MATCH(4004, "两次密码输入不一致"),
    RESET_TOKEN_EXPIRED(4005, "重置令牌已过期"),

    // 系统相关 9xxx
    SYSTEM_ERROR(9001, "系统错误"),
    DATABASE_ERROR(9002, "数据库错误"),
    REDIS_ERROR(9003, "Redis错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
