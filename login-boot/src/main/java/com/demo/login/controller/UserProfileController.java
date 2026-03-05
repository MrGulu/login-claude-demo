package com.demo.login.controller;
import com.demo.login.common.exception.BusinessException;
import com.demo.login.common.result.Result;
import com.demo.login.common.utils.JwtUtil;
import com.demo.login.dto.ChangePasswordDTO;
import com.demo.login.dto.UpdateProfileDTO;
import com.demo.login.service.ICaptchaService;
import com.demo.login.service.IUserProfileService;
import com.demo.login.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户资料控制器
 *
 * @author Claude
 * @since 2026-03-04
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private IUserProfileService userProfileService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ICaptchaService captchaService;

    /**
     * 更新用户资料
     *
     * @param token Token
     * @param dto 更新资料DTO
     * @return 更新后的用户信息
   */
    @PutMapping("/profile")
    public Result<UserInfoVO> updateProfile(
      @RequestHeader("Authorization") String token,
         @Validated @RequestBody UpdateProfileDTO dto) {
    log.info("更新用户资料请求");

     // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 从Token中获取用户ID
      Long userId = jwtUtil.getUserIdFromToken(token);

        // 更新用户资料
        UserInfoVO userInfo = userProfileService.updateProfile(userId, dto);

        return Result.success("更新成功", userInfo);
    }

    /**
     * 上传头像
     *
     * @param token Token
     * @param requestBody 请求体（包含Base64编码的头像）
     * @return 头像URL
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> requestBody) {
        log.info("上传头像请求");
        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 从Token中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 获取Base64头像数据
        String base64Avatar = requestBody.get("avatar");

        // 上传头像
        String avatarUrl = userProfileService.uploadAvatar(userId, base64Avatar);

        Map<String, String> data = new HashMap<>();
        data.put("avatar", avatarUrl);

        return Result.success("头像上传成功", data);
    }

    /**
     * 修改密码
     *
     * @param token Token
     * @param dto 修改密码DTO
   * @return 响应结果
     */
    @PutMapping("/password")
    public Result<Void> changePassword(
      @RequestHeader("Authorization") String token,
            @Validated @RequestBody ChangePasswordDTO dto) {
        log.info("修改密码请求: captchaKey={}, captcha={}", dto.getCaptchaKey(), dto.getCaptcha());

        // 验证验证码
        if (!captchaService.verifyCaptcha(dto.getCaptchaKey(), dto.getCaptcha())) {
            log.error("验证码验证失败: captchaKey={}, captcha={}", dto.getCaptchaKey(), dto.getCaptcha());
            throw new BusinessException(4005, "验证码错误或已过期");
        }

        log.info("验证码验证成功");

        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 从Token中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);

      // 修改密码
        userProfileService.changePassword(userId, dto);

     return Result.success("密码修改成功", null);
    }
}
