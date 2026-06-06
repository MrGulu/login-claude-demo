import request from './auth'

export const getLoginLogList = (params) => {
  return request({
    url: '/admin/login-logs',
    method: 'get',
    params
  })
}

export const getStatistics = () => {
  return request({
    url: '/admin/login-logs/statistics',
    method: 'get'
  })
}
