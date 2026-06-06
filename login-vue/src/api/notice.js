import request from './auth'

export const getNoticeList = (params) => {
  return request({
    url: '/admin/notices',
    method: 'get',
    params
  })
}

export const getNoticeById = (id) => {
  return request({
    url: `/admin/notices/${id}`,
    method: 'get'
  })
}

export const createNotice = (data) => {
  return request({
    url: '/admin/notices',
    method: 'post',
    data
  })
}

export const updateNotice = (id, data) => {
  return request({
    url: `/admin/notices/${id}`,
    method: 'put',
    data
  })
}

export const deleteNotice = (id) => {
  return request({
    url: `/admin/notices/${id}`,
    method: 'delete'
  })
}

export const updateNoticeStatus = (id, status) => {
  return request({
    url: `/admin/notices/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export const getUnreadCount = () => {
  return request({
    url: '/admin/notices/unread-count',
    method: 'get'
  })
}

export const markAsRead = (id) => {
  return request({
    url: `/admin/notices/read/${id}`,
    method: 'post'
  })
}

export const markAllAsRead = () => {
  return request({
    url: '/admin/notices/read-all',
    method: 'post'
  })
}

