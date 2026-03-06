import request from './auth'

/**
 * 获取完整菜单树（用于权限分配）
 */
export const getMenuTree = () => {
  return request({
    url: '/menus/tree',
    method: 'get'
  })
}

/**
 * 获取当前用户的菜单权限
 */
export const getUserMenus = () => {
  return request({
    url: '/menus/user',
    method: 'get'
  })
}
