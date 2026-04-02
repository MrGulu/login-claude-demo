import React, { useState } from 'react'
import { Card, Form, Input, Button, Upload, message } from 'antd'
import type { UploadChangeParam } from 'antd/es/upload'
import { UploadOutlined, UserOutlined, LockOutlined } from '@ant-design/icons'
import { useUserInfo } from '@/hooks/useUserInfo'
import { updateProfile, uploadAvatar, changePassword } from '@/api/user'

const ProfilePage: React.FC = () => {
  const { userInfo, userName, userAvatar } = useUserInfo()
  const [form] = useState<any>(null)
  const [passwordForm] = useState<any>(null)
  const [activeTab, setActiveTab] = useState<'info' | 'password'>('info')
  const [loading, setLoading] = useState(false)
  const [avatarLoading, setAvatarLoading] = useState(false)

  const handleUpdateProfile = async () => {
    if (!form) return
    try {
      const values = await form.validateFields()
      setLoading(true)
      await updateProfile(values)
      message.success('个人资料更新成功')
    } catch (error) {
      console.error('更新失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleAvatarChange = async (info: UploadChangeParam) => {
    if (info.file.status === 'done') {
      const reader = new FileReader()
      reader.onload = async () => {
        try {
          setAvatarLoading(true)
          const base64 = reader.result as string
          await uploadAvatar({ avatar: base64 })
          message.success('头像上传成功')
          setTimeout(() => window.location.reload(), 1000)
        } catch (error) {
          console.error('上传失败:', error)
        } finally {
          setAvatarLoading(false)
        }
      }
      reader.readAsDataURL(info.file.originFileObj)
    }
  }

  const handleChangePassword = async () => {
    if (!passwordForm) return
    try {
      const values = await passwordForm.validateFields()
      setLoading(true)
      await changePassword(values)
      message.success('密码修改成功')
      passwordForm.resetFields()
    } catch (error) {
      console.error('修改失败:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="profile-page">
      <Card className="profile-card">
        <div className="profile-header">
          <div className="avatar-section">
            <Upload
              name="avatar"
              showUploadList={false}
              customRequest={handleAvatarChange}
              className="avatar-upload"
            >
              {!avatarLoading ? (
                <img src={userAvatar} alt="头像" className="avatar-img" />
              ) : (
                '上传中...'
              )}
            </Upload>
          </div>
          <div className="user-info">
            <h2 className="user-name">{userName}</h2>
            <p className="user-role">超级管理员</p>
          </div>
        </div>

        <div className="tabs">
          <Button
            type={activeTab === 'info' ? 'primary' : 'default'}
            icon={<UserOutlined />}
 />}
            onClick={() => setActiveTab('info')}
          >
            个人资料
          </Button>
          <Button
            type={activeTab === 'password' ? 'primary' : 'default'}
            icon={<LockOutlined />}
            style={{ marginLeft: '1rem' }}
            onClick={() => setActiveTab('password')}
          >
            修改密码
          </Button>
        </div>

        {activeTab === 'info' && (
          <Form
            form={form}
            layout="vertical"
            initialValues={userInfo || {}}
          >
            <Form.Item
              name="nickname"
              label="暱称"
              rules={[{ required: true, message: '请输入暱称' }]}
            >
              <Input placeholder="请输入暱称" />
            </Form.Item>

            <Form.Item
              name="email"
              label="邮箱"
              rules={[
                { required: true, message: '请输入邮箱' },
                { type: 'email', message: '请输入正确的邮箱格式' }
              ]}
            >
              <Input placeholder="请输入邮箱" />
            </Form.Item>

            <Form.Item
              name="phone"
              label="手机号"
              rules={[
                { required: true, message: '请输入手机号' },
                {
                  pattern: /^1[3-9]\d{9}$/,
                  message: '请输入正确的手机号'
                }
              ]}
            >
              <Input placeholder="请输入手机号" />
            </Form.Item>

            <Button
              type="primary"
              loading={loading}
              onClick={handleUpdateProfile}
              className="submit-btn"
            >
              保存修改
            </Button>
          </Form>
        )}

        {activeTab === 'password' && (
          <Form
            form={passwordForm}
            layout="vertical"
          >
            <Form.Item
              name="oldPassword"
              label="原密码"
              rules={[{ required: true, message: '请输入原密码' }]}
            >
              <Input.Password placeholder="请输入原密码" />
            </Form.Item>

            <Form.Item
              name="newPassword"
              label="新密码"
              rules={[
                { required: true, message: '请输入新密码' },
                { min: 6, message: '密码长度不能少于6位' }
              ]}
            >
              <Input.Password placeholder="请输入新密码" />
            </Form.Item>

            <Form.Item
              name="confirmPassword"
              label="确认密码"
              dependencies={['newPassword']}
              rules={[
                { required: true, message: '请确认密码' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) {
                      return Promise.resolve()
                    }
                    return Promise.reject(new Error('两次输入的密码不一致'))
                  }
                })
              ]}
            >
              <Input.Password placeholder="请确认密码" />
            </Form.Item>

            <Button
              type="primary"
              loading={loading}
              onClick={handleChangePassword}
              className="submit-btn"
            >
              修改密码
            </Button>
          </Form>
        )}
      </Card>

      <style>{`
        .profile-page {
          max-width: 800px;
          margin: 0 auto;
          animation: fadeIn 0.5s ease-out;
        }

        .profile-card {
          border-radius: 24px;
          border: none;
          box-shadow: 0 8px 32px rgba(220, 38, 38, 0.12);
        }

        .profile-header {
          display: flex;
          align-items: center;
          gap: 2rem;
          padding-bottom: 2rem;
          border-bottom: 1px solid rgba(220, 38, 38, 0.1);
          margin-bottom: 2rem;
        }

        .avatar-section {
          flex-shrink: 0;
        }

        .avatar-upload {
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .avatar-img {
          width: 100px;
          height: 100px;
          border-radius: 50%;
          object-fit: cover;
          border: 3px solid white;
          box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2);
        }

        .user-info {
          flex: 1;
        }

        .user-name {
          font-family: var(--font-display);
          font-size: 1.5rem;
          font-weight: 700;
          color: var(--color-text);
          margin-bottom: 0.5rem;
        }

        .user-role {
          font-size: 1rem;
          color: var(--color-text-muted);
        }

        .tabs {
          display: flex;
          gap: 1rem;
          margin-bottom: 2rem;
        }

        .submit-btn {
          width: 100%;
          margin-top: 1rem;
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(20px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  )
}

export default ProfilePage
