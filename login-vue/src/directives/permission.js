/**
 * 权限指令
 * 用法：v-permission="'system:user:add'"
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    const userPerms = JSON.parse(localStorage.getItem('userPerms') || '[]')

    const hasPermission = userPerms.includes('*:*:*') || userPerms.includes(value)

    if (value && !hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}
