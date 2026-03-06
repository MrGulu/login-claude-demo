package com.demo.login.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录响应VO
 *
 * @author Claude
 * @since 2024-03-02
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    /**
     * 用户权限标识列表
     */
    private List<String> permissions;
}
