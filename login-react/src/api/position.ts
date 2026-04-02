import request from './index'
import type { ApiResponse } from './index'

export const getPositionList = (params: any): Promise<ApiResponse> => {
  return request({
    url: '/admin/positions',
    method: 'get',
    params
  })
}

export const getPositionById = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'get'
  })
}

export const createPosition = (data: any): Promise<ApiResponse> => {
  return request({
    url: '/admin/positions',
    method: 'post',
    data
  })
}

export const updatePosition = (id: number, data: any): Promise<ApiResponse> => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'put',
    data
  })
}

export const deletePosition = (id: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'delete'
  })
}

export const updatePositionStatus = (id: number, status: number): Promise<ApiResponse> => {
  return request({
    url: `/admin/positions/${id}/status`,
    method: 'put',
    data: { status }
  })
}
