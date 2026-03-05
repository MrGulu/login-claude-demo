package com.demo.login.config;

import com.demo.login.interceptor.JwtInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有需要认证的接口
                .addPathPatterns("/api/**")
                // 排除登录接口
                .excludePathPatterns("/api/auth/login")
                // 排除静态资源
                .excludePathPatterns("/static/**", "/public/**");
    }
}
