package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告Mapper接口
 *
 * @author Antigravity
 * @since 2026-06-06
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
