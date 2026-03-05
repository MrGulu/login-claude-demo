import request from './auth'

/**
 * 发送验证码
 */
export function sendVerificationCode(data) {
  return request({
    url: '/password/send-code',
    method: 'post',
    data
  })
}

/**
 * 验证验证码
 */
export function verifyCode(data) {
  return request({
    url: '/password/verify-code',
    method: 'post',
    data
  })
}

/**
 * 重置密码
 */
export function resetPassword(data) {
  return request({
    url: '/password/reset',
    method: 'post',
    data
  })
}
