import request from './auth'

/**
 * 获取图形验证码
 */
export function getCaptcha() {
  return request({
    url: '/captcha/generate',
    method: 'get'
  })
}
