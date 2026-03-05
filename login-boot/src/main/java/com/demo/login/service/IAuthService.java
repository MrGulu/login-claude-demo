package com.demo.login.service;

import com.demo.login.dto.LoginDTO;
import com.demo.login.vo.LoginVO;
import com.demo.login.vo.UserInfoVO;

/**
 * 认证服务接口
 *
 * @author Claude
 * @since 2024-03-02
 */
public interface IAuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应数据
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     *
     * @param token Token
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     *
     * @param token Token
     * @return 用户信息
     */
    UserInfoVO getUserInfo(String token);

    /**
     * 刷新Token
     *
     * @param token 旧Token
     * @return 新Token
     */
    String refreshToken(String token);
}
