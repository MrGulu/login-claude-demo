package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.PasswordResetLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 密码重置日志Mapper接口
 */
@Mapper
public interface PasswordResetLogMapper extends BaseMapper<PasswordResetLog> {
}
