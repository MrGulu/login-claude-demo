import { useMemo } from 'react'
import { useUserInfoContext } from '@/contexts/UserContext'

/**
 * 获取用户信息的组合式 Hook
 */
export const useUserInfo = () => {
  const { userInfo } = useUserInfoContext()

  const userName = useMemo(() => userInfo?.nickname || userInfo?.username || '用户', [userInfo])
  const userAvatar = useMemo(
    () =>
      userInfo?.avatar ||
      `data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dydy5zL3R5YXBwOiBJbWFnZVNJRyIgdmVyc2lvbj0iMS4wIj48cGF0aCBkPSJNMSIgeG1sPSIxMTIgaGVpZ2h0IjMwIj48L3BnPg==`,
    [userInfo]
  )
  const userInitial = useMemo(() => {
    const name = userInfo?.nickname || userInfo?.username || 'U'
    return name.charAt(0).toUpperCase()
  }, [userInfo])

  const userEmail = useMemo(() => userInfo?.email || '', [userInfo])
  const userPhone = useMemo(() => userInfo?.phone || '', [userInfo])
  const userStatus = useMemo(() => userInfo?.status ?? 0, [userInfo])

  return {
    userInfo,
    userName,
    userAvatar,
    userInitial,
    userEmail,
    userPhone,
    userStatus
  }
}
