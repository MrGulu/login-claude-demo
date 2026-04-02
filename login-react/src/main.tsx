import React from 'react'
import { createRoot } from 'react-dom/client'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/es/locale/zh_CN'
import { AuthProvider } from '@/contexts/AuthContext'
import { UserProvider } from '@/contexts/UserContext'
import '@/assets/styles/main.scss'
import '@/assets/styles/animations.scss'
import App from './App'

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ConfigProvider locale={zhCN} theme={{
      token: {
        colorPrimary: '#dc2626',
        colorSuccess: '#52c41a',
        colorWarning: '#faad14',
        colorError: '#dc2626',
        colorInfo: '#1677ff',
        borderRadius: 16,
        fontSizeBase: 14,
        colorBgBase: '#f5f5f5'
      }
    }}>
      <AuthProvider>
        <UserProvider>
          <App />
        </UserProvider>
      </AuthProvider>
    </ConfigProvider>
  </React.StrictMode>
)
