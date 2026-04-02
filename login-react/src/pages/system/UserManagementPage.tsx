import React, { useState } from 'react'
import { Card, Table, Form, Input, Button, Modal, message, Radio, Select, Tag } from 'antd'
import { SearchOutlined, PlusOutlined, UserOutlined, UserAddOutlined, EditOutlined, DeleteOutlined, KeyOutlined, PhoneOutlined, TeamOutlined, BriefcaseOutlined, MoreFilled, SettingOutlined, OperationOutlined } from '@ant-design/icons'
import type { FormInstance } from 'antd/es/form'
import { getUserList, createUser, updateUser, deleteUser as deleteUserApi, updateUserStatusApi, getUserRoles, assignRoles } from '@/api/userManagement'
import { usePermission } from '@/hooks/usePermission'

const UserManagementPage: React.FC = () => {
  const [form] = useState<FormInstance | null>(null)
  const [loading, setLoading] = useState(false)
  const [dialogVisible, setDialogVisible] = useState(false)
  const [dialogTitle, setDialogTitle] = useState('新增用户')

  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  })

  const [tableData, setTableData] = useState<any[]>([])

  const [formData, setFormData] = useState({
    id: null as number | null,
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    status: 1,
    remark: ''
  })

  const rules = {
    username: [
      { required: true, message: '请输入用户名' },
      { min: 3, max: 20, message: '长度在3-20个字符' }
    ],
    password: [
      { validator: (_, value) => {
        if (!value) return Promise.reject(new Error('请输入密码'))
        if (value.length < 6 || value.length > 20) {
          return Promise.reject(new Error('密码长度在6-20个字符'))
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
    ],
    nickname: [
      { max: 50, message: '昵称长度不能超过50个字符' }
    ],
    email: [
      { type: 'email', message: '请输入正确的邮箱格式' }
    ],
    phone: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
    ],
    status: [
      { required: true, message: '请选择状态' }
    ]
  }

  const getUserData = async () => {
    setLoading(true)
    try {
      const response = await getUserList({
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      })
      setTableData(response.data.list || [])
      setPagination(prev => ({ ...prev, total: response.data.total }))
    } catch (error) {
      console.error('获取用户列表失败:', error)
      message.error('获取用户列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = () => {
    setPagination(prev => ({ ...prev, current: 1 }))
    getUserData()
  }

  const handleAdd = () => {
    setFormData({
      id: null,
      username: '',
      password: '',
      nickname: '',
      email: '',
      phone: '',
      status: 1,
      remark: ''
    })
    setDialogTitle('新增用户')
    setDialogVisible(true)
  }

  const handleEdit = (record: any) => {
    setFormData({
      id: record.id,
      username: record.username,
      password: '',
      nickname: record.nickname,
      email: record.email,
      phone: record.phone,
      status: record.status,
      remark: record.remark
    })
    setDialogTitle('编辑用户')
    setDialogVisible(true)
  }

  const handleDelete = (record: any) => {
    Modal.confirm({
      title: '删除确认',
      content: `确定要删除用户"${record.username}"吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteUserApi(record.id)
          message.success('删除成功')
          getUserData()
        } catch (error) {
          console.error('删除失败:', error)
          message.error('删除失败')
        }
      }
    })
  }

  const handleStatusChange = (record: any) => {
    const newStatus = record.status === 1 ? 0 : 1
    Modal.confirm({
      title: '状态变更',
      content: `确定要${newStatus === 1 ? '启用' : '禁用'}用户"${record.username}"吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await updateUserStatusApi(record.id, newStatus)
          message.success(`${newStatus === 1 ? '启用' : '禁用'}成功`)
          getUserData()
        } catch (error) {
          console.error('状态变更失败:', error)
          message.error('状态变更失败')
        }
      }
    })
  }

  const handleSubmit = async () => {
    if (!form) return

    try {
      const values = await form.validateFields()
      setLoading(true)

      const data: any = {
        username: values.username,
        password: values.password,
        nickname: values.nickname,
        email: values.email,
        phone: values.phone,
        status: values.status,
        remark: values.remark
      }

      if (formData.id) {
        await updateUser(formData.id, data)
        message.success('更新成功')
      } else {
        await createUser(data)
        message.success('创建成功')
      }

      setDialogVisible(false)
      getUserData()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleDialogClose = () => {
    setDialogVisible(false)
    form?.resetFields()
    setFormData({
      id: null,
      username: '',
      password: '',
      nickname: '',
      email: '',
      phone: '',
      status: 1,
      remark: ''
    })
  }

  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      render: (_: any, record: any) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <UserOutlined />
          <span>{record.username}</span>
        </div>
      )
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname'
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email'
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (_: any, record: any) => (
        <Tag color={record.status === 1 ? 'success' : 'error'} effect="light">
          {record.status === 1 ? '正常' : '禁用'}
        </Tag>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: any) => (
        <Button size="small" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
          删除
        </Button>
      )
    }
  ]

  return (
    <div className="user-management-page">
      <Card title="用户管理">
        <Table
          columns={columns}
          dataSource={tableData}
          loading={loading}
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            onChange: (page, pageSize) => {
              setPagination({ current: page, pageSize, total: pagination.total })
            },
            showTotal: true,
            showSizeChanger: true
          }}
          rowKey="id"
        />
      </Card>

      <Modal
        title={dialogTitle}
        open={dialogVisible}
        onCancel={handleDialogClose}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={formData}
          rules={rules}
          labelCol={{ span: 6 }}
        >
          <Form.Item name="username" label="用户名" rules={[rules.username[0]]}>
            <Input placeholder="请输入用户名" disabled={!!formData.id} />
          </Form.Item>

          <Form.Item name="password" label="密码" rules={[rules.password[0]]}>
            <Input.Password placeholder="请输入密码" disabled={!!formData.id} />
          </Form.Item>

          <Form.Item name="nickname" label="昵称">
            <Input placeholder="请输入昵称" />
          </Form.Item>

          <Form.Item name="email" label="邮箱">
            <Input placeholder="请输入邮箱" />
          </Form.Item>

          <Form.Item name="phone" label="手机号">
            <Input placeholder="请输入手机号" />
          </Form.Item>

          <Form.Item name="status" label="状态">
            <Radio.Group>
              <Radio value={1}>正常</Radio>
              <Radio value={0}>禁用</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item labelCol={{ offset: 1, span: 23 }} label="操作">
            <div style={{ display: 'flex', gap: '8px' }}>
              <Button type="primary" htmlType="submit" onClick={handleSubmit} loading={loading}>
                {formData.id ? '保存修改' : '立即创建'}
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>

      <style>{`
        .user-management-page {
          padding: 24px;
          min-height: 100vh;
          background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
          animation: fadeIn 0.6s ease-out;
        }

        @keyframes fadeIn {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
      `}</style>
    </div>
  )
}

export default UserManagementPage
