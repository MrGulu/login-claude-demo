import request from './index'
import type { ApiResponse } from './index'

/**
 * 用户登录
 * @param data - 登录数据
 */
export const login = (data: { username: string; password: string; remember?: boolean }): Promise<ApiResponse<any>> => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export const logout = (): Promise<ApiResponse<any>> => {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 */
export const getUserInfo = (): Promise<ApiResponse<any>> => {
  return request({
    url: '/auth/userinfo',
    method: 'get'
  })
}

/**
 * 刷新Token
 */
export const refreshToken = (): Promise<ApiResponse<any>> => {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}
