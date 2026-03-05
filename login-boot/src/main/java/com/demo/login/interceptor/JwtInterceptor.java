package com.demo.login.interceptor;

import com.demo.login.common.exception.BusinessException;
import com.demo.login.service.impl.AuthServiceImpl;
import com.demo.login.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 *
 * @author Claude
 * @since 2024-03-05
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(3001, "Token不能为空");
        }

        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token
        if (!jwtUtil.verifyToken(token)) {
            throw new BusinessException(3002, "Token无效");
        }

        // 检查Token是否在内存中存在且未过期
        AuthServiceImpl.TokenInfo tokenInfo = AuthServiceImpl.TOKEN_STORE.get(token);
        if (tokenInfo == null || tokenInfo.getExpireTime() < System.currentTimeMillis()) {
            throw new BusinessException(3003, "Token已过期");
        }

        // 将userId存入request attribute，供后续使用
        request.setAttribute("userId", tokenInfo.getUserId());

        return true;
    }
}
