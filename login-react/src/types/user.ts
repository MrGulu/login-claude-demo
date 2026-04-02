/**
 * 用户信息类型
 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  status: number
  remark?: string
  roles?: Role[]
  positions?: Position[]
}

/**
 * 角色信息类型
 */
export interface Role {
  id: number
  name: string
  code: string
  description?: string
  status: number
  createdAt: string
}

/**
 * 岗位信息类型
 */
export interface Position {
  id: number
  name: string
  code: string
  description?: string
  status: number
  sort: number
  createdAt: string
}

/**
 * 菜单信息类型
 */
export interface Menu {
  id: number
  name: string
  path?: string
  component?: string
  icon?: string
  type: number
  parentId?: number
  sort: number
  status: number
  children?: Menu[]
}

/**
 * 登录响应类型
 */
export interface LoginResponse {
  token: string
  userinfo: UserInfo
}
