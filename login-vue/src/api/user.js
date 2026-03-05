import request from './auth'

/**
 * 更新用户信息
 * @param {Object} data - 用户信息 { nickname, email, phone }
 * @returns {Promise}
 */
export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

/**
 * 上传头像（Base64）
 * @param {Object} data - 头像数据 { avatar: 'data:image/...' }
 * @returns {Promise}
 */
export function uploadAvatar(data) {
  return request({
    url: '/user/avatar',
    method: 'post',
    data
  })
}

/**
 * 修改密码
 * @param {Object} data - 密码数据 { oldPassword, newPassword, confirmPassword, captcha, captchaKey }
 * @returns {Promise}
 */
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}
