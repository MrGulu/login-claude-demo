import { ref, computed } from 'vue'

// 全局响应式用户信息
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

export function useUserInfo() {
  // 用户名
  const userName = computed(() => userInfo.value.username || '用户')

  // 用户头像
  const userAvatar = computed(() => userInfo.value.avatar || '')

  // 用户名首字母
  const userInitial = computed(() => {
    return userName.value.charAt(0).toUpperCase()
  })

  // 更新用户信息
  const updateUserInfo = (newInfo) => {
    userInfo.value = { ...userInfo.value, ...newInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  // 刷新用户信息（从 localStorage 重新加载）
  const refreshUserInfo = () => {
    userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = {}
    localStorage.removeItem('userInfo')
  }

  return {
    userInfo,
    userName,
    userAvatar,
    userInitial,
    updateUserInfo,
    refreshUserInfo,
    clearUserInfo
  }
}
