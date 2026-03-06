import axios from 'axios'
import { showError } from '../utils/toast'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    console.log('响应数据:', res)

    // 检查 Token 过期（code 3003）
    if (res.code === 3003) {
      const errorMsg = res.message || 'Token已过期'
      showError(errorMsg)
      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('userPerms')
      // 延迟跳转到登录页，让用户看到提示
      setTimeout(() => {
        window.location.href = '/login'
      }, 1500)
      return Promise.reject(new Error(errorMsg))
    }

    // 如果返回的状态码不是200，说明接口请求失败
    if (res.code !== 200) {
      const errorMsg = res.message || res.msg || '请求失败'
      console.log('业务错误:', errorMsg)
      showError(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }

    return res
  },
  error => {
    console.error('响应错误:', error)

    let message = '网络错误，请稍后重试'

    if (error.response) {
      // 优先使用后端返回的错误信息
      const errorData = error.response.data

      // 检查是否是 Token 过期
      if (errorData && errorData.code === 3003) {
        message = errorData.message || 'Token已过期'
        // 清除本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userPerms')
        showError(message)
        // 延迟跳转到登录页
        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)
        return Promise.reject(error)
      }

      // 尝试多种可能的错误信息字段
      if (errorData) {
        message = errorData.message || errorData.msg || errorData.error || errorData.errorMessage
      }

      // 如果没有获取到具体错误信息，使用默认提示
      if (!message || message === '网络错误，请稍后重试') {
        switch (error.response.status) {
          case 400:
            message = errorData?.message || errorData?.msg || '请求参数错误'
            break
          case 401:
            message = errorData?.message || errorData?.msg || '未授权，请重新登录'
            // 清除token
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            localStorage.removeItem('userPerms')
            // 跳转到登录页
            setTimeout(() => {
              window.location.href = '/login'
            }, 1500)
            break
          case 403:
            message = errorData?.message || errorData?.msg || '拒绝访问'
            break
          case 404:
            message = '请求地址不存在'
            break
          case 500:
            message = errorData?.message || errorData?.msg || '服务器错误'
            break
          default:
            message = errorData?.message || errorData?.msg || '请求失败'
        }
      }
    }

    showError(message)
    return Promise.reject(error)
  }
)

/**
 * 用户登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {boolean} data.remember - 是否记住我
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 * @returns {Promise}
 */
export function getUserInfo() {
  return request({
    url: '/auth/userinfo',
    method: 'get'
  })
}

/**
 * 刷新Token
 * @returns {Promise}
 */
export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}

export default request
