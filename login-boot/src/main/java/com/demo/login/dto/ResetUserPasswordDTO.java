package com.demo.login.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 重置用户密码参数DTO
 *
 * @author Claude
 * @since 2026-06-09
 */
@Data
public class ResetUserPasswordDTO {

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;
}
