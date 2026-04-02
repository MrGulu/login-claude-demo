import React, { useState } from 'react'
import { Outlet } from 'react-router-dom'
import Background from '@/components/Background'
import Header from '@/components/Header'
import Sidebar from '@/components/Sidebar'

const DashboardLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)

  const toggleSidebar = () => {
    setCollapsed(!collapsed)
  }

  return (
    <div className="dashboard-layout">
      <Background />
      <Sidebar collapsed={collapsed} onToggle={toggleSidebar} />
      <div className={`main-wrapper ${collapsed ? 'sidebar-collapsed' : ''}`}>
        <Header onToggle={toggleSidebar} />
        <main className="content-area">
          <Outlet />
        </main>
      </div>

      <style>{`
        .dashboard-layout {
          min-height: 100vh;
          position: relative;
          overflow-x: hidden;
        }

        .main-wrapper {
          margin-left: 280px;
          min-height: 100vh;
          transition: margin-left 0.3s ease;
        }

        .main-wrapper.sidebar-collapsed {
          margin-left: 80px;
        }

        .content-area {
          padding: 100px 40px 40px;
          position: relative;
          z-index: 1;
        }

        @media (max-width: 768px) {
          .main-wrapper {
            margin-left: 0;
          }

          .main-wrapper.sidebar-collapsed {
            margin-left: 0;
          }

          .content-area {
            padding: 80px 20px 20px;
          }
        }
      `}</style>
    </div>
  )
}

export default DashboardLayout
