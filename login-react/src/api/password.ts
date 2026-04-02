import request from './index'
import type { ApiResponse } from './index'

/**
 * 发送验证码
 */
export const sendVerificationCode = (data: any): Promise<ApiResponse<any>> => {
  return request({
    url: '/password/send-code',
    method: 'post',
    data
  })
}

/**
 * 验证验证码
 */
export const verifyCode = (data: any): Promise<ApiResponse<any>> => {
  return request({
    url: '/password/verify-code',
    method: 'post',
    data
  })
}

/**
 * 重置密码
 */
export const resetPassword = (data: any): Promise<ApiResponse<any>> => {
  return request({
    url: '/password/reset',
    method: 'post',
    data
  })
}
