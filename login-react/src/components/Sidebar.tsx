import React from 'react'
import { Menu } from 'antd'
import {
  HomeOutlined,
  UserOutlined,
  TeamOutlined,
  SettingOutlined,
  FolderOutlined,
  SafetyOutlined
} from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import type { MenuProps } from 'antd'

interface SidebarProps {
  collapsed: boolean
  onToggle: () => void
}

const Sidebar: React.FC<SidebarProps> = ({ collapsed, onToggle }) => {
  const navigate = useNavigate()
  const location = useLocation()

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(`/${key}`)
  }

  const items: MenuProps['items'] = [
    {
      key: 'home',
      icon: <HomeOutlined />,
      label: '首页'
    },
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人资料'
    },
    {
      key: 'system',
      icon: <SettingOutlined />,
      label: '系统管理',
      children: [
        {
          key: 'system/users',
          icon: <TeamOutlined />,
          label: '用户管理'
        },
        {
          key: 'system/roles',
          icon: <SafetyOutlined />,
          label: '角色管理'
        },
        {
          key: 'system/positions',
          icon: <FolderOutlined />,
          label: '岗位管理'
        }
      ]
    }
  ]

  const getSelectedKeys = (): string[] => {
    const path = location.pathname
    if (path === '/home') return ['home']
    if (path === '/profile') return ['profile']
    if (path.includes('/system/users')) return ['system/users']
    if (path.includes('/system/roles')) return ['system/roles']
    if (path.includes('/system/positions')) return ['system/positions']
    return []
  }

  return (
    <aside className={`sidebar ${collapsed ? 'collapsed' : ''}`}>
      <div className="sidebar-header">
        <div className="logo">
          <span className="logo-icon">A</span>
          {!collapsed && <span className="logo-text">AdminHub</span>}
        </div>
      </div>

      <Menu
        mode="inline"
        theme="light"
        selectedKeys={getSelectedKeys()}
        items={items}
        inlineCollapsed={collapsed}
        onClick={handleMenuClick}
        className="sidebar-menu"
      />
    </aside>

  <style>{`
    .sidebar {
      position: fixed;
      top: 0;
      left: 0;
      width: 280px;
      height: 100vh;
      background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.95) 0%,
        rgba(255, 255, 255, 0.7) 100%);
      transition: all 0.3s ease;
      z-index: 100;
    }

    .sidebar-header {
      display: flex;
      align-items: center;
      padding: 0 40px 0 20px;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .logo-icon {
      width: 40ra;
      height: 40ra;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
      color: white;
      font-size: 1.75rem;
    }

    .logo-text {
      font-family: var(--font-display);
      font-size: 1.25rem;
      font-weight: 700;
      color: var(--color-text);
      transition: all 0.3s ease;
    }

    .sidebar-menu {
      border: none;
    }

    @media (max-width: 768px) {
      .sidebar {
        display: none;
      }

      .sidebar-header {
        padding: 0 24px 0;
      }
    }
  `}</style>
  )
}

export default Sidebar
