/**
 * 权限指令
 * 用法：v-permission="'system:user:add'"
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    const userPerms = JSON.parse(localStorage.getItem('userPerms') || '[]')

    console.log('权限检查:', {
      需要的权限: value,
      用户权限列表: userPerms,
      是否有权限: userPerms.includes(value)
    })

    if (value && !userPerms.includes(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
