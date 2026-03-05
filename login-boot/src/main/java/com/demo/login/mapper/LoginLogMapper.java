package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper接口
 *
 * @author Claude
 * @since 2024-03-02
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

}
