import request from './auth'

/**
 * 查询用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} params.username - 用户名（模糊查询）
 * @param {string} params.phone - 手机号（模糊查询）
 * @param {number} params.status - 状态：0-禁用，1-正常
 */
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 查询用户详情
 * @param {number} id - 用户ID
 */
export function getUserDetail(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

/**
 * 创建用户
 * @param {Object} data - 用户信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.nickname - 昵称
 * @param {string} data.email - 邮箱
 * @param {string} data.phone - 手机号
 * @param {number} data.status - 状态：0-禁用，1-正常
 * @param {string} data.remark - 备注
 */
export function createUser(data) {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 * @param {number} id - 用户ID
 * @param {Object} data - 用户信息
 * @param {string} data.password - 密码（选填）
 * @param {string} data.nickname - 昵称
 * @param {string} data.email - 邮箱
 * @param {string} data.phone - 手机号
 * @param {number} data.status - 状态：0-禁用，1-正常
 * @param {string} data.remark - 备注
 */
export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 */
export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

/**
 * 更新用户状态
 * @param {number} id - 用户ID
 * @param {number} status - 状态：0-禁用，1-正常
 */
export function updateUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}
