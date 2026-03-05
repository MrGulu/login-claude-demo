package com.demo.login.service;

import com.demo.login.dto.ChangePasswordDTO;
import com.demo.login.dto.UpdateProfileDTO;
import com.demo.login.vo.UserInfoVO;

/**
 * 用户资料服务接口
 *
 * @author Claude
 * @since 2026-03-04
 */
public interface IUserProfileService {

    /**
     * 更新用户资料
     *
     * @param userId 用户ID
     * @param dto 更新资料DTO
     * @return 更新后的用户信息
     */
    UserInfoVO updateProfile(Long userId, UpdateProfileDTO dto);

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param base64Avatar Base64编码的头像
     * @return 头像URL
     */
    String uploadAvatar(Long userId, String base64Avatar);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto 修改密码DTO
     */
    void changePassword(Long userId, ChangePasswordDTO dto);
}
