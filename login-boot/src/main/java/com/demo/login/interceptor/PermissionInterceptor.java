package com.demo.login.interceptor;

import com.demo.login.annotation.RequirePermission;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.common.utils.JwtUtil;
import com.demo.login.service.IMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 权限验证拦截器
 *
 * @author Claude
 * @since 2024-03-06
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理方法级别的请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission annotation = handlerMethod.getMethodAnnotation(RequirePermission.class);

        // 如果方法没有 @RequirePermission 注解，直接放行
        if (annotation == null) {
            return true;
        }

        // 获取需要的权限标识
        String requiredPermission = annotation.value();

        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("权限验证失败：未提供有效的 Token");
            throw new BusinessException("未授权访问");
        }

        token = token.substring(7);

        // 从 token 中获取用户 ID
        Long userId;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            log.warn("权限验证失败：Token 解析失败", e);
            throw new BusinessException("Token无效");
        }

        // 获取用户的所有权限
        List<String> userPermissions = menuService.getUserPermissions(userId);

        // 验证用户是否拥有所需权限
        if (!userPermissions.contains(requiredPermission)) {
            log.warn("权限验证失败：用户 {} 缺少权限 {}", userId, requiredPermission);
            throw new BusinessException("权限不足");
        }

        log.debug("权限验证通过：用户 {} 拥有权限 {}", userId, requiredPermission);
        return true;
    }
}
