package com.demo.login.config;

import com.demo.login.interceptor.JwtInterceptor;
import com.demo.login.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 *
 * @author Claude
 * @since 2024-03-05
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 认证拦截器
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有需要认证的接口
                .addPathPatterns("/api/**")
                // 排除登录接口
                .excludePathPatterns("/api/auth/login")
                // 排除静态资源
                .excludePathPatterns("/static/**", "/public/**");

        // 权限验证拦截器（在 JWT 拦截器之后执行）
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login")
                .excludePathPatterns("/static/**", "/public/**");
    }
}
