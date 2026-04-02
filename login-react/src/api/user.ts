import request from './index'
import type { ApiResponse } from './index'

/**
 * 更新用户信息
 * @param data - 用户信息 { nickname, email, phone }
 */
export const updateProfile = (data: any): Promise<ApiResponse> => {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

/**
 * 上传头像（Base64）
 * @param data - 头像数据 { avatar: 'data:image/...' }
 */
export const uploadAvatar = (data: any): Promise<ApiResponse> => {
  return request({
    url: '/user/avatar',
    method: 'post',
    data
  })
}

/**
 * 修改密码
 * @param data - 密码数据 { oldPassword, newPassword, confirmPassword, captcha, captchaKey }
 */
export const changePassword = (data: any): Promise<ApiResponse> => {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}
