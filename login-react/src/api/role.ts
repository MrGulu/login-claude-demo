import request from './index'
import type { ApiResponse } from './index'

/**
 * 分页查询角色列表
 */
export const getRoleList = (params: any): Promise<ApiResponse> => {
  return request({
    url: '/admin/roles',
    method: 'get',
    params
  })
}

/**
 * 查询角色详情
 */
export const getRoleById = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/roles/${id}`,
    method: 'get'
  })
}

/**
 * 创建角色
 */
export const createRole = (data: any): Promise<ApiResponse> => {
  return request({
    url: '/admin/roles',
    method: 'post',
    data
  })
}

/**
 * 更新角色
 */
export const updateRole = (id: number, data: any): Promise<ApiResponse> => {
  return request({
    url: `/admin/roles/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export const deleteRole = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/roles/${id}`,
    method: 'delete'
  })
}

/**
 * 获取角色的菜单权限
 */
export const getRoleMenus = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/roles/${id}/menus`,
    method: 'get'
  })
}

/**
 * 分配菜单权限
 */
export const assignMenus = (id: number, data: any): Promise<ApiResponse> => {
  return request({
    url: `/admin/roles/${id}/menus`,
    method: 'put',
    data
  })
}
