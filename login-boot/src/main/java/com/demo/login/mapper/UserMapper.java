package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author Claude
 * @since 2024-03-02
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
