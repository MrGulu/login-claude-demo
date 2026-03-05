package com.demo.login.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息VO
 *
 * @author Claude
 * @since 2024-03-02
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;
}
