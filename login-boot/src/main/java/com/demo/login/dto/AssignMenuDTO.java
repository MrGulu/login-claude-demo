package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 分配菜单权限DTO
 *
 * @author Claude
 * @since 2024-03-06
 */
@Data
public class AssignMenuDTO {

    /**
     * 角色ID（从路径参数获取，不需要验证）
     */
    private Long roleId;

    /**
     * 菜单ID列表（覆盖式更新）
     */
    @NotEmpty(message = "菜单ID列表不能为空")
    private List<Long> menuIds;
}
