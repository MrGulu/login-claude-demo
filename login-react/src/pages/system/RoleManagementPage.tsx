import React, { useState } from 'react'
import { Card, Table, Form, Input, Button, Modal, message, Radio, Tag, Tree, InputNumber } from 'antd'
import { UserOutlined, SettingOutlined, DeleteOutlined, PlusOutlined, EditOutlined, MoreFilled } from '@ant-design/icons'
import type { FormInstance } from 'antd/es/form'
import { getRoleList, createRole, updateRole, deleteRole, getMenuTree, assignMenus } from '@/api/role'

const RoleManagementPage: React.FC = () => {
  const [form] = useState<FormInstance | null>(null)
  const [loading, setLoading] = useState(false)
  const [dialogVisible, setDialogVisible] = useState(false)
  const [dialogTitle, setDialogTitle] = useState('新增角色')

  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  })

  const [tableData, setTableData] = useState<any[]>([])

  const [formData, setFormData] = useState({
    id: null as number | null,
    roleName: '',
    roleKey: '',
    status: 1,
    sort: 0,
    remark: ''
  })

  const rules = {
    roleName: [
      { required: true, message: '请输入角色名称' },
      { min: 2, max: 50, message: '长度在2-50个字符' }
    ],
    roleKey: [
      { required: true, message: '请输入角色标识' },
      { min: 2, max: 50, message: '长度在2-50个字符' }
    ],
    status: [
      { required: true, message: '请选择状态' }
    ]
  }

  const getRoleData = async () => {
    setLoading(true)
    try {
      const response = await getRoleList({
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      })
      setTableData(response.data.records || [])
      setPagination(prev => ({ ...prev, total: response.data.total }))
    } catch (error) {
      console.error('获取角色列表失败:', error)
      message.error('获取角色列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAdd = () => {
    setFormData({
      id: null,
      roleName: '',
      roleKey: '',
      status: 1,
      sort: 0,
      remark: ''
    })
    setDialogTitle('新增角色')
    setDialogVisible(true)
  }

  const handleEdit = (record: any) => {
    setFormData({
      id: record.id,
      roleName: record.roleName,
      roleKey: record.roleKey,
      status: record.status,
      sort: record.sort,
      remark: record.remark
    })
    setDialogTitle('编辑角色')
    setDialogVisible(true)
  }

  const handleDelete = (record: any) => {
    Modal.confirm({
      title: '删除确认',
      content: `确定要删除角色"${record.roleName}"吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteRole(record.id)
          message.success('删除成功')
          getRoleData()
        } catch (error) {
          console.error('删除失败:', error)
          message.error('删除失败')
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
        roleName: values.roleName,
        roleKey: values.roleKey,
        status: values.status,
        sort: values.sort,
        remark: values.remark
      }

      if (formData.id) {
        await updateRole(formData.id, data)
        message.success('更新成功')
      } else {
        await createRole(data)
        message.success('创建成功')
      }

      setDialogVisible(false)
      getRoleData()
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
      roleName: '',
      roleKey: '',
      status: 1,
      sort: 0,
      remark: ''
    })
  }

  const columns = [
    {
      title: '角色名称',
      dataIndex: 'roleName',
      key: 'roleName'
    },
    {
      title: '角色标识',
      dataIndex: 'roleKey',
      key: 'roleKey'
    },
    {
      title: '系统角色',
      render: (_: any, record: any) => (
        <Tag color="danger">系统角色</Tag>
      )
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
      title: '排序',
      dataIndex: 'sort',
      key: 'sort'
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark'
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
    <div className="role-management-page">
      <Card title="角色管理">
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
          <Form.Item name="roleName" label="角色名称" rules={[rules.roleName[0]]}>
            <Input placeholder="请输入角色名称" disabled={!!formData.id} />
          </Form.Item>

          <Form.Item name="roleKey" label="角色标识" rules={[rules.roleKey[0]]}>
            <Input placeholder="请输入角色标识" disabled={!!formData.id} />
          </Form.Item>

          <Form.Item name="status" label="状态">
            <Radio.Group>
              <Radio value={1}>正常</Radio>
              <Radio value={0}>禁用</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item name="sort" label="排序">
            <InputNumber min={0} max={999} placeholder="请输入排序号" />
          </Form.Item>

          <Form.Item name="remark" label="备注">
            <Input.TextArea placeholder="请输入备注" maxLength={500} />
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
        .role-management-page {
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

export default RoleManagementPage
