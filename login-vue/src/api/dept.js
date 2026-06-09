import request from './auth'

export const getDepartmentList = (params) => {
  return request({
    url: '/admin/departments',
    method: 'get',
    params
  })
}

export const getDepartmentById = (id) => {
  return request({
    url: `/admin/departments/${id}`,
    method: 'get'
  })
}

export const createDepartment = (data) => {
  return request({
    url: '/admin/departments',
    method: 'post',
    data
  })
}

export const updateDepartment = (id, data) => {
  return request({
    url: `/admin/departments/${id}`,
    method: 'put',
    data
  })
}

export const deleteDepartment = (id) => {
  return request({
    url: `/admin/departments/${id}`,
    method: 'delete'
  })
}

export const updateDepartmentStatus = (id, status) => {
  return request({
    url: `/admin/departments/${id}/status`,
    method: 'put',
    data: { status }
  })
}
