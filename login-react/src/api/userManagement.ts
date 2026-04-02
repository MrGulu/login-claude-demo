import request from './index'
import type { ApiResponse } from './index'

/**
 * 查询用户列表
 * @param params - 查询参数
 */
export const getUserList = (params: any): Promise<ApiResponse> => {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 查询用户详情
 * @param id - 用户ID
 */
export const getUserDetail = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

/**
 * 创建用户
 * @param data - 用户信息
 */
export const createUser = (data: any): Promise<ApiResponse> => {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 * @param id - 用户ID
 * @param data - 用户信息
 */
export const updateUser = (id: number, data: any): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param id - 用户ID
 */
export const deleteUser = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

/**
 * 更新用户状态
 * @param id - 用户ID
 * @param status - 状态：0-禁用，1-正常
 */
export const updateUserStatus = (id: number, status: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 获取用户的角色列表
 * @param id - 用户ID
 */
export const getUserRoles = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}/roles`,
    method: 'get'
  })
}

/**
 * 分配角色给用户
 * @param id - 用户ID
 * @param roleIds - 角色ID列表
 */
export const assignRoles = (id: number, roleIds: number[]): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}/roles`,
    method: 'put',
    data: { roleIds }
  })
}

/**
 * 获取用户的岗位列表
 * @param id - 用户ID
 */
 */
export const getUserPositions = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}/positions`,
    method: 'get'
  })
}

/**
 * 分配岗位给用户
 * @param id - 用户ID
 * @param positionIds - 岗位ID列表
 */
export const assignPositions = (id: number, positionIds: number[]): Promise<ApiResponse> => {
  return request({
    url: `/admin/users/${id}/positions`,
    method: 'put',
    data: { positionIds }
  })
}
