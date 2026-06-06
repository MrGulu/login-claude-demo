package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 登录日志Mapper接口
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 查询最近7天每天的登录成功数
     */
    @Select("SELECT date(login_time, 'localtime') as date, count(*) as count " +
            "FROM sys_login_log " +
            "WHERE login_time >= date('now', '-6 days', 'localtime') AND status = 1 " +
            "GROUP BY date " +
            "ORDER BY date ASC")
    List<Map<String, Object>> selectLast7DaysLoginCount();

    /**
     * 查询各浏览器的登录频次占比
     */
    @Select("SELECT browser, count(*) as count " +
            "FROM sys_login_log " +
            "GROUP BY browser")
    List<Map<String, Object>> selectBrowserDistribution();
}
