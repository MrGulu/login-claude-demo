import request from './index'
import type { ApiResponse } from './index'

/**
 * 获取图形验证码
 */
export const getCaptcha = (): Promise<ApiResponse> => {
  return request({
    url: '/captcha/generate',
    method: 'get'
  })
}
