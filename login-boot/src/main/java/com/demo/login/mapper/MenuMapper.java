package com.demo.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.login.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper接口
 *
 * @author Claude
 * @since 2024-03-06
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据角色ID列表查询菜单
     *
     * @param roleIds 角色ID列表
     * @return 菜单列表
     */
    @Select("<script>" +
            "SELECT DISTINCT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            " AND m.status = 1 AND m.deleted = 0 " +
            "ORDER BY m.sort ASC" +
            "</script>")
    List<Menu> selectMenusByRoleIds(@Param("roleIds") List<Long> roleIds);
}
