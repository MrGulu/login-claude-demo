package com.demo.login.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 分配岗位DTO
 *
 * @author Claude
 * @since 2024-03-16
 */
@Data
public class AssignPositionDTO {

    /**
     * 用户ID（从路径参数获取，不需要验证）
     */
    private Long userId;

    /**
     * 岗位ID列表（覆盖式更新）
     */
    @NotEmpty(message = "岗位ID列表不能为空")
    private List<Long> positionIds;
}
