package com.demo.login.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.dto.LoginLogQueryDTO;
import com.demo.login.entity.LoginLog;

import java.util.Map;

/**
 * 登录日志服务接口
 *
 * @author Antigravity
 * @since 2026-06-06
 */
public interface ILoginLogService {

    /**
     * 分页查询登录日志
     *
     * @param queryDTO 查询条件
     * @return 分页数据
     */
    Page<LoginLog> getLoginLogList(LoginLogQueryDTO queryDTO);

    /**
     * 记录登录日志
     *
     * @param loginLog 登录日志信息
     */
    void recordLoginLog(LoginLog loginLog);

    /**
     * 获取首页登录日志的统计数据
     *
     * @return 包含7日登录趋势和浏览器分布的统计Map
     */
    Map<String, Object> getLoginStatistics();
}
