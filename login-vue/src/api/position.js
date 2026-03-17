import request from './auth'

export const getPositionList = (params) => {
  return request({
    url: '/admin/positions',
    method: 'get',
    params
  })
}

export const getPositionById = (id) => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'get'
  })
}

export const createPosition = (data) => {
  return request({
    url: '/admin/positions',
    method: 'post',
    data
  })
}

export const updatePosition = (id, data) => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'put',
    data
  })
}

export const deletePosition = (id) => {
  return request({
    url: `/admin/positions/${id}`,
    method: 'delete'
  })
}

export const updatePositionStatus = (id, status) => {
  return request({
    url: `/admin/positions/${id}/status`,
    method: 'put',
    data: { status }
  })
}
