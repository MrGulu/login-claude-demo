package com.demo.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.login.dto.LoginLogQueryDTO;
import com.demo.login.entity.LoginLog;
import com.demo.login.mapper.LoginLogMapper;
import com.demo.login.service.ILoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现类
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Slf4j
@Service
public class LoginLogServiceImpl implements ILoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public Page<LoginLog> getLoginLogList(LoginLogQueryDTO queryDTO) {
        Page<LoginLog> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(LoginLog::getUsername, queryDTO.getUsername());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(LoginLog::getStatus, queryDTO.getStatus());
        }

        // 默认按访问时间降序排列
        wrapper.orderByDesc(LoginLog::getLoginTime);
        return loginLogMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordLoginLog(LoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public Map<String, Object> getLoginStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 1. 产生最近 7 天的日期序列 (yyyy-MM-dd)
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).toString());
        }

        // 2. 查询数据库中近 7 天的数据
        List<Map<String, Object>> dbTrend = loginLogMapper.selectLast7DaysLoginCount();
        Map<String, Long> trendMap = new HashMap<>();
        if (dbTrend != null) {
            for (Map<String, Object> map : dbTrend) {
                String date = (String) map.get("date");
                Number count = (Number) map.get("count");
                if (date != null && count != null) {
                    trendMap.put(date, count.longValue());
                }
            }
        }

        // 3. 补齐 7 天内缺失的日期数据，保证数据连续不中断
        List<Long> counts = new ArrayList<>();
        for (String date : dates) {
            counts.add(trendMap.getOrDefault(date, 0L));
        }

        Map<String, Object> trendData = new HashMap<>();
        trendData.put("dates", dates);
        trendData.put("counts", counts);
        result.put("trend", trendData);

        // 4. 获取浏览器占比分布数据
        List<Map<String, Object>> dbBrowser = loginLogMapper.selectBrowserDistribution();
        List<Map<String, Object>> browserData = new ArrayList<>();
        if (dbBrowser != null) {
            for (Map<String, Object> map : dbBrowser) {
                Map<String, Object> item = new HashMap<>();
                String browser = (String) map.get("browser");
                Number count = (Number) map.get("count");
                item.put("name", StringUtils.hasText(browser) ? browser : "未知浏览器");
                item.put("value", count != null ? count.longValue() : 0L);
                browserData.add(item);
            }
        }
        result.put("browser", browserData);

        return result;
    }
}
