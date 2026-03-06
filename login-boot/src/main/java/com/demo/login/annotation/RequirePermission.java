package com.demo.login.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 * 用于标记需要权限验证的接口方法
 *
 * @author Claude
 * @since 2024-03-06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识
     * 例如：system:user:add, system:user:edit, system:user:delete
     */
    String value();
}
