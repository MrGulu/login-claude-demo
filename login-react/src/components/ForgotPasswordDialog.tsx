import React, { useState } from 'react'
import { Modal, Form, Input, Button, message } from 'antd'
import type { FormInstance } from 'antd/es/form'

interface ForgotPasswordDialogProps {
  visible: boolean
  onClose: () => void
}

/* eslint-disable-next-line @typescript-eslint/no-unused-vars */
const ForgotPasswordDialog: React.FC<ForgotPasswordDialogProps> = ({ visible, onClose }) => {
  const [form] = useState<FormInstance | null>(null)
  const [step, setStep] = useState(1)
  const [loading, setLoading] = useState(false)

  const handleSendCode = async () => {
    if (!form) return
    try {
      const values = await form.validateFields()
      console.log('发送验证码:', values)
      message.success('验证码已发送')
      setStep(2)
    } catch (error) {
      console.error('验证失败:', error)
    }
  }

  const handleVerifyCode = async () => {
    if (!form) return
    try {
      const values = await form.validateFields()
      console.log('验证验证码:', values)
      message.success('验证成功')
      setStep(3)
    } catch (error) {
      console.error('验证失败:', error)
    }
  }

  const handleResetPassword = async () => {
    if (!form) return
    try {
      const values = await form.validateFields()
      setLoading(true)
      console.log('重置密码:', values)
      message.success('密码重置成功，请使用新密码登录')
      setLoading(false)
      setTimeout(() => {
        onClose()
        setStep(1)
      }, 1500)
    } catch (error) {
      console.error('重置失败:', error)
      setLoading(false)
    }
  }

  const renderContent = () => {
    switch (step) {
      case 1:
        return (
          <>
            <Form.Item
              name="email"
              rules={[
                { required: true, message: '请输入邮箱' },
                { type: 'email', message: '请输入正确的邮箱格式' }
              ]}
            >
              <Input placeholder="请输入邮箱" size="large" />
            </Form.Item>
            <Form.Item
              name="phone"
              rules={[
                { required: true, message: '请输入手机号' },
                {
                  pattern: /^1[3-9]\d{9}$/,
                  message: '请输入正确的手机号'
                }
              ]}
            >
              <Input placeholder="请输入手机号" size="large" />
            </Form.Item>
            <Button
              type="primary"
              size="large"
              block
              loading={loading}
              onClick={handleSendCode}
            >
              发送验证码
            </Button>
          </>
        )
      case 2:
        return (
          <>
            <Form.Item
              name="code"
              rules={[
                { required: true, message: '请输入验证码' },
                { len: 6, message: '验证码长度为6位' }
              ]}
            >
              <Input placeholder="请输入验证码" size="large" maxLength={6} />
            </Form.Item>
            <Button
              type="primary"
              size="large"
              block
              loading={loading}
              onClick={handleVerifyCode}
            >
              验证
            </Button>
          </>
        )
      case 3:
        return (
          <>
            <Form.Item
              name="newPassword"
              rules={[
                { required: true, message: '请输入新密码' },
                { min: 6, message: '密码长度不能少于6位' }
              ]}
            >
              <Input.Password placeholder="请输入新密码" size="large" />
            </Form.Item>
            <Form.Item
              name="confirmPassword"
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
              <Input.Password placeholder="请确认新密码" size="large" />
            </Form.Item>
            <Button
              type="primary"
              size="large"
              block
              loading={loading}
              onClick={handleResetPassword}
            >
              重置密码
            </Button>
          </>
        )
      default:
        return null
    }
  }

  return (
    <Modal
      title="忘记密码"
      open={visible}
      onCancel={onClose}
      footer={null}
      centered
      width={450}
      className="forgot-password-modal"
    >
      <div className="forgot-content">
        <Form
          form={form}
          layout="vertical"
          size="large"
        >
          {renderContent()}
        </Form>
        {step > 1 && (
          <Button
            type="link"
            onClick={() => setStep(step - 1)}
            style={{ marginTop: '1rem' }}
          >
            上一步
          </Button>
        )}
      </div>

      <style>{`
        .forgot-password-modal {
          .ant-modal-header {
            background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
            border-bottom: none;
            padding: 1.5rem 2rem;
          }

          .ant-modal-title {
            color: white;
            font-size: 1.25rem;
            font-weight: 600;
          }

          .ant-modal-close {
            color: white !important;
            font-size: 1.5rem;
          }

          .ant-modal-close:hover {
            color: rgba(255, 255, 255, 0.8) !important;
          }
        }

        .forgot-content {
          padding: 1rem 0;
        }

        .ant-form-item {
          margin-bottom: 1.5rem;
        }

        .ant-form-item-label > label {
          font-weight: 600;
          color: var(--color-text);
        }

        .ant-input-affix-wrapper,
        .ant-input-password {
          border-radius: 12px !important;
        }
      `}</style>
    </Modal>
  )
}

export default ForgotPasswordDialog
