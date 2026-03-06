package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 分配角色DTO
 *
 * @author Claude
 * @since 2024-03-06
 */
@Data
public class AssignRoleDTO {

    /**
     * 用户ID（从路径参数获取，不需要验证）
     */
    private Long userId;

    /**
     * 角色ID列表（覆盖式更新）
     */
    @NotEmpty(message = "角色ID列表不能为空")
    private List<Long> roleIds;
}
