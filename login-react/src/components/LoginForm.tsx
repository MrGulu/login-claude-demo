import React, { useState } from 'react'
import { Form, Input, Button, Checkbox, message } from 'antd'
import type { FormInstance } from 'antd/es/form'
import { useNavigate } from 'react-router-dom'
import { login } from '@/api/auth'
import { useAuth } from '@/contexts/AuthContext'
import { useUserInfoContext } from '@/contexts/UserContext'
import SocialLogin from './SocialLogin'
import ForgotPasswordDialog from './ForgotPasswordDialog'

const LoginForm: React.FC = () => {
  const [form] = useState<FormInstance | null>(null)
  const [loading, setLoading] = useState(false)
  const [remember, setRemember] = useState(false)
  const [dialogVisible, setDialogVisible] = useState(false)
  const navigate = useNavigate()
  const { updateToken } = useAuth()
  const { setUserInfo } = useUserInfoContext()

  const handleSubmit = async () => {
    if (!form) return

    try {
      const values = await form.validateFields()
      setLoading(true)

      const response = await login({
        username: values.username,
        password: values.password,
        remember
      })

      // 保存token和用户信息
      updateToken(response.data.token)
      setUserInfo(response.data.userinfo)
      localStorage.setItem('userPerms', JSON.stringify(response.data.permissions || []))

      message.success('登录成功！正在跳转...')

      setTimeout(() => {
        navigate('/home')
      }, 1000)
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const showForgotPassword = () => {
    setDialogVisible(true)
  }

  return (
    <div className="login-section">
      <div className="form-header">
        <h2 className="form-title">欢迎回来</h2>
        <p className="form-subtitle">登录您的管理账户</p>
      </div>

      <Form
        form={form}
        layout="vertical"
        size="large"
        onFinish={handleSubmit}
        className="login-form"
      >
        <Form.Item
          name="username"
          label="用户名"
          rules={[
            { required: true, message: '请输入用户名' },
            { min: 3, max: 20, message: '长度在 3 到 20 个字符' }
          ]}
        >
          <Input placeholder="请输入用户名" />
        </Form.Item>

        <Form.Item
          name="password"
          label="密码"
          rules={[
            { required: true, message: '请输入密码' },
            { min: 6, message: '密码长度不能少于 6 个字符' }
          ]}
        >
          <Input.Password placeholder="请输入密码" />
        </Form.Item>

        <div className="form-options">
          <Checkbox checked={remember} onChange={e => setRemember(e.target.checked)}>
            记住我
          </Checkbox>
          <a className="forgot-link" onClick={showForgotPassword}>
            忘记密码？
          </a>
        </div>

        <Button
          type="primary"
          size="large"
          block
          loading={loading}
          htmlType="submit"
          className="submit-btn"
        >
          {loading ? '登录中...' : '登录'}
        </Button>

        <div className="divider">或使用以下方式登录</div>

        <SocialLogin />
      </Form>

      <ForgotPasswordDialog
        visible={dialogVisible}
        onClose={() => setDialogVisible(false)}
      />

      <style>{`
        .login-section {
          position: absolute;
          right: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 480px;
          background: linear-gradient(135deg,
            rgba(255,255,255,0.95) 0%,
            rgba(255,255,255,0.85) 100%);
          backdrop-filter: blur(30px);
          border-radius: 32px;
          padding: 3rem 2.5rem;
          box-shadow:
            0 40px 100px rgba(220, 38, 38, 0.25),
            inset 0 0 0 1px rgba(255,255,255,1),
            0 0 0 1px rgba(220, 38, 38, 0.1);
          animation: slideInRight 1s cubic-bezier(0.16, 1, 0.3, 1);
          z-index: 20;
        }

        .login-section::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 3px;
          background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-accent) 100%);
          border-radius: 32px 32px 0 0;
        }

        .form-header {
          margin-bottom: 2rem;
          animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1) 0.4s backwards;
        }

        .form-title {
          font-family: var(--font-display);
          font-size: 2rem;
          font-weight: 700;
          color: var(--color-text);
          margin-bottom: 0.5rem;
          letter-spacing: -0.02em;
        }

        .form-subtitle {
          font-size: 1rem;
          color: var(--color-text-muted);
          font-weight: 400;
        }

        .login-form {
          animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1) 0.6s backwards;
        }

        .login-form .ant-form-item {
          margin-bottom: 1.5rem;
        }

        .login-form .ant-form-item-label > label {
          font-size: 0.875rem !important;
          font-weight: 700 !important;
          color: var(--color-text) !important;
          text-transform: uppercase;
          letter-spacing: 0.05em;
        }

        .form-options {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 2rem;
          font-size: 0.875rem;
        }

        .forgot-link {
          color: var(--color-primary);
          text-decoration: none;
          font-weight: 700;
          transition: all 0.2s;
          cursor: pointer;
        }

        .forgot-link:hover {
          color: var(--color-primary-dark);
          text-decoration: underline;
        }

        .submit-btn {
          width: 100%;
          padding: 1.125rem;
          font-size: 1rem;
          position: relative;
          overflow: hidden;
        }

        .submit-btn::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
          transition: left 0.6s;
        }

        .submit-btn:hover::before {
          left: 100%;
        }

        .divider {
          display: flex;
          align-items: center;
          gap: 1rem;
          margin: 2rem 0;
          color: var(--color-text-muted);
          font-size: 0.875rem;
          font-weight: 600;
          white-space: nowrap;
        }

        .divider::before,
        .divider::after {
          content: '';
          flex: 1;
          height: 2px;
          background: linear-gradient(90deg, transparent, rgba(220, 38, 38, 0.2), transparent);
        }

        @keyframes slideInRight {
          from {
            opacity: 0;
            transform: translateY(-50%) translateX(100px);
          }
          to {
            opacity: 1;
            transform: translateY(-50%) translateX(0);
          }
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(20px); }
          to { opacity: 1; transform: translateY(0); }
        }

        @media (max-width: 1200px) {
          .login-section {
            width: 420px;
          }
        }

        @media (max-width: 768px) {
          .login-section {
            position: relative;
            width: 100%;
            transform: none;
          }
        }
      `}</style>
    </div>
  )
}

export default LoginForm
