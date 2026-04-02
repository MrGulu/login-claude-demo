import React, { useState } from 'react'
import { Avatar, Dropdown, Button } from 'antd'
import { UserOutlined, LogoutOutlined, SettingOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { logout } from '@/api/auth'
import { useAuth } from '@/contexts/AuthContext'
import { useUserInfoContext } from '@/contexts/UserContext'
import type { MenuProps } from 'antd'

const Header: React.FC<{ onToggle: () => void }> = ({ onToggle }) => {
  const navigate = useNavigate()
  const { clearToken } = useAuth()
  const { userInfo, clearUserInfo } = useUserInfoContext()
  const [mobileMenuVisible, setMobileMenuVisible] = useState(false)

  const handleLogout = async () => {
    try {
      await logout()
      clearToken()
      clearUserInfo()
      localStorage.removeItem('userPerms')
      navigate('/login')
    } catch (error) {
      console.error('登出失败:', error)
    }
  }

  const items: MenuProps['items'] = [
    {
      key: 'profile',
      label: '个人资料',
      icon: <UserOutlined />,
      onClick: () => navigate('/profile')
    },
    {
      key: 'settings',
      label: '系统设置',
      icon: <SettingOutlined />
    },
    {
      type: 'divider'
    },
    {
      key: 'logout',
      label: '退出登录',
      icon: <LogoutOutlined />,
      danger: true,
      onClick: handleLogout
    }
  ]

  return (
    <header className="header">
      <div className="header-left">
        <Button
          type="text"
          icon={<span>☰</span>}
          onClick={onToggle}
          className="toggle-btn"
        >
          菜单
        </Button>
      </div>

      <div className="header-right">
        <Dropdown menu={{ items }} placement="bottomRight" arrow>
          <div className="user-info" onClick={() => setMobileMenuVisible(!mobileMenuVisible)}>
            <Avatar src={userInfo?.avatar} size="default">
              {userInfo?.nickname?.charAt(0) || 'U'}
            </Avatar>
            <span className="username">{userInfo?.nickname || userInfo?.username || '用户'}</span>
          </div>
        </Dropdown>
      </div>

      <style>{`
        .header {
          position: fixed;
          top: 0;
          right: 0;
          left: 280px;
          height: 70px;
          background: linear-gradient(135deg,
            rgba(255,255,255,0.95) 0%,
            rgba(255,255,255,0.9) 100%);
          backdrop-filter: blur(20px);
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 0 1.5rem;
          z-index: 100;
          transition: left 0.3s ease;
        }

        .header-left {
          display: flex;
          align-items: center;
          gap: 1rem;
        }

        .toggle-btn {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          font-size: 1rem;
          font-weight: 600;
          color: var(--color-text);
        }

        .header-right {
          display: flex;
          align-items: center;
        }

        .user-info {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          cursor: pointer;
          padding: 0.25rem 0.5rem;
          border-radius: 8px;
          transition: all 0.2s;
        }

        .user-info:hover {
          background: rgba(220, 38, 38, 0.1);
        }

        .username {
          font-weight: 600;
          color: var(--color-text);
        }

        @media (max-width: 768px) {
          .header {
            left: 0;
          }
        }
      `}</style>
    </header>
  )
}

export default Header
