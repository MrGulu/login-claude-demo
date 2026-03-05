package com.demo.login;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 登录系统启动类
 *
 * @author Claude
 * @since 2024-03-02
 */
@SpringBootApplication
@MapperScan("com.demo.login.mapper")
public class LoginBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginBootApplication.class, args);
        System.out.println("========================================");
        System.out.println("登录系统启动成功！");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("数据库: SQLite (./data/login.db)");
        System.out.println("========================================");
    }
}
