import React from 'react'
import { WechatOutlined, QqOutlined, GithubOutlined, WeiboCircleOutlined } from '@ant-design/icons'

const SocialLogin: React.FC = () => {
  const handleSocialLogin = (provider: string) => {
    console.log('社交登录:', provider)
    // 这里可以添加实际的社交登录逻辑
  }

  return (
    <div className="social-login">
      <div
        className="social-btn wechat"
        onClick={() => handleSocialLogin('wechat')}
      >
        <WechatOutlined />
      </div>
      <div
        className="social-btn qq"
        onClick={() => handleSocialLogin('qq')}
      >
        <QqOutlined />
      </div>
      <div
        className="social-btn github"
        onClick={() => handleSocialLogin('github')}
      >
        <GithubOutlined />
      </div>
      <div
        className="social-btn weibo"
        onClick={() => handleSocialLogin('weibo')}
      >
        <WeiboCircleOutlined />
      </div>

      <style>{`
        .social-login {
          display: flex;
          gap: 1rem;
          justify-content: center;
        }

        .social-btn {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s ease;
          font-size: 1.25rem;
          color: var(--color-text-muted);
          background: rgba(255, 255, 255, 0.5);
        }

        .social-btn:hover {
          transform: translateY(-2px);
          color: var(--color-primary);
          background: rgba(220, 38, 38, 0.1);
        }

        .social-btn.wechat:hover {
          color: #07c160;
        }

        .social-btn.qq:hover {
          color: #12b7f5;
        }

        .social-btn.github:hover {
          color: #333;
        }

        .social-btn.weibo:hover {
          color: #e6162d;
        }
      `}</style>
    </div>
  )
}

export default SocialLogin
