import React from 'react'
import { Card, Row, Col } from 'antd'
import { UserOutlined, TeamOutlined, SafetyOutlined, FolderOutlined, PlusOutlined, SettingOutlined, FileTextOutlined } from '@ant-design/icons'
import { useUserInfo } from '@/hooks/useUserInfo'

const HomePage: React.FC = () => {
  const { userName } = useUserInfo()

  return (
    <div className="home-page">
      <div className="welcome-banner">
        <h1 className="welcome-title">
          欢迎回来，<span className="highlight">{userName}</span>
        </h1>
        <p className="welcome-subtitle">
          今天是个新的一天，让我们一起开始工作吧！
        </p>
      </div>

      <Row gutter={[24, 24]}>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card user-card">
            <div className="stat-icon">
              <UserOutlined />
            </div>
            <div className="stat-info">
              <div className="stat-value">1,234</div>
              <div className="stat-label">总用户数</div>
            </div>
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card role-card">
            <div className="stat-icon">
              <TeamOutlined />
            </div>
            <div className="stat-info">
              <div className="stat-value">56</div>
              <div className="stat-label">角色数量</div>
            </div>
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card position-card">
            <div className="stat-icon">
              <SafetyOutlined />
            </div>
            <div className="stat-info">
              <div className="stat-value">128</div>
              <div className="stat-label">岗位数量</div>
            </div>
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card active-card">
            <div className="stat-icon">
              <FolderOutlined />
            </div>
            <div className="stat-info">
              <div className="stat-value">892</div>
              <div className="stat-label">今日活跃</div>
            </div>
          </Card>
        </Col>
      </Row>

      <Row gutter={[24, 24]} style={{ marginTop: '24px' }}>
        <Col xs={24} lg={12}>
          <Card title="快捷操作" className="action-card">
            <div className="action-list">
              <div className="action-item">
                <div className="action-icon">
                  <PlusOutlined />
                </div>
                <span>新增用户</span>
              </div>
              <div className="action-item">
                <div className="action-icon">
                  <PlusOutlined />
                </div>
                <span>创建角色</span>
              </div>
              <div className="action-item">
                <div className="action-icon">
                  <PlusOutlined />
                </div>
                <span>添加岗位</span>
              </div>
              <div className="action-item">
                <div className="action-icon">
                  <SettingOutlined />
                </div>
                <span>系统配置</span>
              </div>
            </div>
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="最近动态" className="activity-card">
            <div className="activity-list">
              <div className="activity-item">
                <div className="activity-icon">📝</div>
                <div className="activity-content">
                  <div className="activity-title">用户登录</div>
                  <div className="activity-time">5分钟前</div>
                </div>
              </div>
              <div className="activity-item">
                <div className="activity-icon">👤</div>
                <div className="activity-content">
                  <div className="activity-title">角色权限更新</div>
                  <div className="activity-time">15分钟前</div>
                </div>
              </div>
              <div className="activity-item">
                <div className="activity-icon">📋</div>
                <div className="activity-content">
                  <div className="activity-title">系统配置变更</div>
                  <div className="activity-time">1小时前</div>
                </div>
              </div>
              <div className="activity-item">
                <div className="activity-icon">👥</div>
                <div className="activity-content">
                  <div className="activity-title">数据备份完成</div>
                  <div className="activity-time">2小时前</div>
                </div>
              </div>
            </div>
          </Card>
        </Col>
      </Row>

      <style>{`
        .home-page {
          animation: fadeIn 0.5s ease-out;
        }

        .welcome-banner {
          background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
          border-radius: 24px;
          padding: 3rem 2rem;
          margin-bottom: 24px;
          position: relative;
          overflow: hidden;
        }

        .welcome-banner::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: url('data:image/svg+xml;charset=utf-8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><circle cx="20" cy="20" r="2" fill="rgba(255,255,255,0.1) /></svg>');
          background-size: 20px 20px;
        }

        .welcome-title {
          font-family: var(--font-display);
          font-size: 2rem;
          font-weight: 700;
          color: white;
          margin-bottom: 1rem;
        }

        .welcome-title .highlight {
          color: var(--color-accent);
        }

        .welcome-subtitle {
          font-size: 1.125rem;
          color: rgba(255, 255, 255, 0.9);
        }

        .stats-card {
          border-radius: 20px;
          border: none;
          box-shadow: 0 8px 24px rgba(220, 38, 38, 0.12);
          transition: all 0.3s ease;
          cursor: pointer;
        }

        .stats-card:hover {
          transform: translateY(-5px);
          box-shadow: 0 12px 32px rgba(220, 38, 38, 0.2);
        }

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.75rem;
          color: white;
          margin-bottom: 1rem;
        }

        .stats-card.user-card .stat-icon {
          background: linear-gradient(135deg, #3b82f6, #2563eb);
        }

        .stats-card.role-card .stat-icon {
          background: linear-gradient(135deg, #8b5cf6, #06b6d4);
        }

        .stats-card.position-card .stat-icon {
          background: linear-gradient(135deg, #f59e0b, #d97706);
    }

        .stats-card.active-card .stat-icon {
          background: linear-gradient(135deg, #10b981, #059669);
    }

        .stat-info {
          text-align: center;
        }

        .stat-value {
          font-family: var(--font-display);
          font-size: 1.75rem;
          font-weight: 700;
          color: var(--color-text);
          margin-bottom: 0.25rem;
        }

        .stat-label {
          font-size: 0.875rem;
          color: var(--color-text-muted);
          font-weight: 600;
        }

        .action-card,
        .activity-card {
          border-radius: 20px;
          border: none;
          box-shadow: 0 8px 24px rgba(220, 38, 38, 0.12);
        }

        .action-list {
          display: flex;
          flex-direction: column;
          gap: 1rem;
        }

        .action-item {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding: 1rem;
          border-radius: 12px;
          transition: all 0.2s;
          cursor: pointer;
        }

        .action-item:hover {
          background: rgba(220, 38, 38, 0.05);
        }

        .action-icon {
          width: 40px;
          height: 40px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
          color: white;
        }

        .activity-list {
          display: flex;
          flex-direction: column;
          gap: 1rem;
        }

        .activity-item {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding: 1rem;
          border-radius: 12px;
          transition: all 0.2s;
        }

        .activity-item:hover {
          background: rgba(220, 38, 38, 0.05);
        }

        .activity-icon {
          font-size: 1.5rem;
        }

        .activity-content {
          flex: 1;
        }

        .activity-title {
          font-weight: 600;
          color: var(--color-text);
        }

        .activity-time {
          font-size: 0.875rem;
          color: var(--color-text-muted);
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(20px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  )
}

export default HomePage
