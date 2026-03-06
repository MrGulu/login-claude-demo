package com.demo.login.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单树VO（用于权限分配）
 *
 * @author Claude
 * @since 2024-03-06
 */
@Data
public class MenuTreeVO {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型：M-目录，C-菜单，F-按钮
     */
    private String menuType;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子菜单列表
     */
    private List<MenuTreeVO> children;
}
