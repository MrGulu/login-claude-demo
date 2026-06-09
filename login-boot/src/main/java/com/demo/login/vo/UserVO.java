package com.demo.login.vo;

import lombok.Data;

/**
 * 用户视图对象
 *
 * @author Claude
 * @since 2024-03-04
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 岗位ID列表
     */
    private java.util.List<Long> positionIds;

    /**
     * 岗位名称拼接串
     */
    private String positionNames;

    /**
     * 角色ID列表
     */
    private java.util.List<Long> roleIds;

    /**
     * 角色名称拼接串
     */
    private String roleNames;
}


