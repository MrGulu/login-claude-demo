package com.demo.login.controller;

import com.demo.login.common.result.Result;
import com.demo.login.dto.LoginDTO;
import com.demo.login.service.IAuthService;
import com.demo.login.vo.LoginVO;
import com.demo.login.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author Claude
 * @since 2024-03-02
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: {}", loginDTO.getUsername());
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户登出
     *
     * @param token Token
     * @return 响应结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("用户登出请求");
        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return Result.success("登出成功", null);
    }

    /**
     * 获取当前用户信息
     *
     * @param token Token
     * @return 用户信息
     */
    @GetMapping("/userinfo")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("Authorization") String token) {
        log.info("获取用户信息请求");
        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        UserInfoVO userInfo = authService.getUserInfo(token);
        return Result.success(userInfo);
    }

    /**
     * 刷新Token
     *
     * @param token 旧Token
     * @return 新Token
     */
    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken(@RequestHeader("Authorization") String token) {
        log.info("刷新Token请求");
        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String newToken = authService.refreshToken(token);
        Map<String, String> data = new HashMap<>();
        data.put("token", newToken);
        return Result.success(data);
    }
}
