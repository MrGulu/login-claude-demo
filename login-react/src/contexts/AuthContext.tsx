import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react'

interface AuthContextType {
  token: string | null
  updateToken: (token: string) => void
  clearToken: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => {
    const stored = localStorage.getItem('token')
    return stored
  })

  const updateToken = useCallback((newToken: string) => {
    setToken(newToken)
    localStorage.setItem('token', newToken)
  }, [])

  const clearToken = useCallback(() => {
    setToken(null)
    localStorage.removeItem('token')
  }, [])

  return (
    <AuthContext.Provider value={{ token, updateToken, clearToken }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
