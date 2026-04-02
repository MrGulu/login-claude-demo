import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react'
import type { UserInfo } from '@/types/user'

interface UserContextType {
  userInfo: UserInfo | null
  setUserInfo: (info: UserInfo | null) => void
  getUserInfo: () => UserInfo | null
  clearUserInfo: () => void
}

const UserContext = createContext<UserContextType | undefined>(undefined)

export const UserProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [userInfo, setUserInfoState] = useState<UserInfo | null>(() => {
    const stored = localStorage.getItem('userInfo')
    return stored ? JSON.parse(stored) : null
  })

  const setUserInfo = useCallback((info: UserInfo | null) => {
    setUserInfoState(info)
    if (info) {
      localStorage.setItem('userInfo', JSON.stringify(info))
    } else {
      localStorage.removeItem('userInfo')
    }
  }, [])

  const getUserInfo = useCallback((): UserInfo | null => {
    if (!userInfo) {
      const stored = localStorage.getItem('userInfo')
      if (stored) {
        return JSON.parse(stored)
      }
    }
    return userInfo
  }, [userInfo])

  const clearUserInfo = useCallback(() => {
    setUserInfoState(null)
    localStorage.removeItem('userInfo')
  }, [])

  return (
    <UserContext.Provider value={{ userInfo, setUserInfo, getUserInfo, clearUserInfo }}>
      {children}
    </UserContext.Provider>
  )
}

export const useUserInfoContext = () => {
  const context = useContext(UserContext)
  if (context === undefined) {
    throw new Error('useUserInfoContext must be used within a UserProvider')
  }
  return context
}
