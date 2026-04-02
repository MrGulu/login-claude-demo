import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'
import Background from '@/components/Background'
import BrandSection from '@/components/BrandSection'
import LoginForm from '@/components/LoginForm'

const LoginPage: React.FC = () => {
  const navigate = useNavigate()
  const { token } = useAuth()

  // 已登录用户跳转到首页
  React.useEffect(() => {
    if (token) {
      navigate('/home', { replace: true })
    }
  }, [token, navigate])

  return (
    <div className="login-page">
      <Background />
      <div className="diagonal-wrapper">
        <BrandSection />
        <LoginForm />
      </div>

      <style>{`
        .login-page {
          width: 100%;
          height: 100vh;
          overflow: hidden;
        }

        .diagonal-wrapper {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 1400px;
          height: 800px;
          display: flex;
          align-items: center;
        }

        @media (max-width: 1200px) {
          .diagonal-wrapper {
            max-width: 1000px;
            height: 600px;
          }
        }

        @media (max-width: 768px) {
          .diagonal-wrapper {
            flex-direction: column;
            height: auto;
            position: relative;
            top: 0;
            left: 0;
            transform: none;
            width: 100%;
            padding: 2rem;
          }
        }
      `}</style>
    </div>
  )
}

export default LoginPage
