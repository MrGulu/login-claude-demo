import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { message } from 'antd'

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers!['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error: any) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    console.log('响应数据:', res)

    // 检查 Token 过期（code 3003）
    if (res.code === 3003) {
      const errorMsg = res.message || 'Token已过期'
      message.error(errorMsg)
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
      const errorMsg = res.message || (res as any).msg || '请求失败'
      console.log('业务错误:', errorMsg)
      message.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }

    return res
  },
  (error: AxiosError<ApiResponse>) => {
    console.error('响应错误:', error)

    let msg = '网络错误，请稍后重试'

    if (error.response) {
      // 优先使用后端返回的错误信息
      const errorData = error.response.data

      // 检查是否是 Token 过期
      if (errorData && errorData.code === 3003) {
        msg = errorData.message || 'Token已过期'
        // 清除本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userPerms')
        message.error(msg)
        // 延迟跳转到登录页
        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)
        return Promise.reject(error)
      }

      // 尝试多种可能的错误信息字段
      if (errorData) {
        msg = errorData.message || (errorData as any).msg || (errorData as any).error || (errorData as any).errorMessage
      }

      // 如果没有获取到具体错误信息，使用默认提示
      if (!msg || msg === '网络错误，请稍后重试') {
        switch (error.response.status) {
          case 400:
            msg = errorData?.message || (errorData as any).msg || '请求参数错误'
            break
          case 401:
            msg = errorData?.message || (errorData as any).msg || '未授权，请重新登录'
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
            msg = errorData?.message || (errorData as any).msg || '拒绝访问'
            break
          case 404:
            msg = '请求地址不存在'
            break
          case 500:
            msg = errorData?.message || (errorData as any).msg || '服务器错误'
            break
          default:
            msg = errorData?.message || (errorData as any).msg || '请求失败'
        }
      }
    }

    message.error(msg)
    return Promise.reject(error)
  }
)

export default request
