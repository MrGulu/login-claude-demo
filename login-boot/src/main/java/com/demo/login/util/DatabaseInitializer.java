package com.demo.login.util;

import com.demo.login.entity.User;
import com.demo.login.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化工具
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        // 更新admin用户的手机号和邮箱
        User user = userMapper.selectById(1L);
        if (user != null) {
            System.out.println("===================");
            System.out.println("当前admin用户信息:");
            System.out.println("手机号: " + user.getPhone());
            System.out.println("邮箱: " + user.getEmail());

            // 强制更新
            user.setPhone("13800138000");
            user.setEmail("admin@example.com");
          userMapper.updateById(user);

       System.out.println("已更新admin用户信息:");
        System.out.println("手机号: " + user.getPhone());
     System.out.println("邮箱: " + user.getEmail());
            System.out.println("================");
        } else {
            System.out.println("未找到ID为1的用户");
        }
    }
}
