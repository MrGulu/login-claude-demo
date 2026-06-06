package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.NoticeRead;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告已读关联Mapper接口
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Mapper
public interface NoticeReadMapper extends BaseMapper<NoticeRead> {
}
