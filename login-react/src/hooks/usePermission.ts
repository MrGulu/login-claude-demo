/**
 * 权限检查 Hook
 * @param permission - 权限标识
 * @returns 是否有该权限
 */
export const usePermission = (permission: string) => {
  const permissions = localStorage.getItem('userPerms')
  if (!permissions) {
    return false
  }

  const perms = JSON.parse(permissions)
  if (Array.isArray(perms)) {
    return perms.includes(permission)
  }

  return false
}

/**
 * 检查多个权限
 * @param permissions - 权限数组
 * @returns 是否有任一权限
 */
export const hasAnyPermission = (permissions: string[]): boolean => {
  const storedPerms = localStorage.getItem('userPerms')
  if (!storedPerms) {
    return false
  }

  const perms = JSON.parse(storedPerms)
  if (Array.isArray(perms)) {
    return permissions.some(p => perms.includes(p))
  }

  return false
}

/**
 * 检查所有权限
 * @param permissions - 权限数组
 * @returns 是否拥有所有权限
 */
export const hasAllPermissions = (permissions: string[]): boolean => {
  const storedPerms = localStorage.getItem('userPerms')
  if (!storedPerms) {
    return false
  }

  const perms = JSON.parse(storedPerms)
  if (Array.isArray(perms)) {
    return permissions.every(p => perms.includes(p))
  }

  return false
}
