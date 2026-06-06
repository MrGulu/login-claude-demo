package com.demo.login.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.annotation.RequirePermission;
import com.demo.login.common.result.Result;
import com.demo.login.dto.LoginLogQueryDTO;
import com.demo.login.entity.LoginLog;
import com.demo.login.service.ILoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 登录日志控制器
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/login-logs")
public class LoginLogController {

    @Autowired
    private ILoginLogService loginLogService;

    /**
     * 分页查询登录日志列表
     */
    @GetMapping
    @RequirePermission("system:login-log:query")
    public Result<Page<LoginLog>> getLoginLogList(LoginLogQueryDTO queryDTO) {
        Page<LoginLog> page = loginLogService.getLoginLogList(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取首页看板统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getLoginStatistics() {
        Map<String, Object> statistics = loginLogService.getLoginStatistics();
        return Result.success(statistics);
    }
}
