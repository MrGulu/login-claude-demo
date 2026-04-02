import React, { useState } from 'react'
import { Card, Table, Form, Input, Button, Modal, message, Radio, InputNumber, Tag } from 'antd'
import { FolderOutlined, SettingOutlined, DeleteOutlined, PlusOutlined, EditOutlined, OperationOutlined } from '@ant-design/icons'
import type { FormInstance } from 'antd/es/form'
import { getPositionList, createPosition, updatePosition, deletePosition, updatePositionStatus } from '@/api/position'

const PositionManagementPage: React.FC = () => {
  const [form] = useState<FormInstance | null>(null)
  const [loading, setLoading] = useState(false)
  const [dialogVisible, setDialogVisible] = useState(false)
  const [dialogTitle, setDialogTitle] = useState('新增岗位')

  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  })

  const [tableData, setTableData] = useState<any[]>([])

  const [formData, setFormData] = useState({
    id: null as number | null,
    positionName: '',
    positionCode: '',
    status: 1,
    sort: 0,
    remark: ''
  })

  const rules = {
    positionName: [
      { required: true, message: '请输入岗位名称' },
      { min: 2, max: 50, message: '长度在2-50个字符' }
    ],
    positionCode: [
      { required: true, message: '请输入岗位编码' },
      { min: 2, max: 50, message: '长度在2-50个字符' }
    ],
    status: [
      { required: true, message: '请选择状态' }
    ]
  }

  const getPositionData = async () => {
    setLoading(true)
    try {
      const response = await getPositionList({
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      })
      setTableData(response.data.records || [])
      setPagination(prev => ({ ...prev, total: response.data.total }))
    } catch (error) {
      console.error('获取岗位列表失败:', error)
      message.error('获取岗位列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAdd = () => {
    setFormData({
      id: null,
      positionName: '',
      positionCode: '',
      status: 1,
      sort: 0,
      remark: ''
    })
    setDialogTitle('新增岗位')
    setDialogVisible(true)
  }

  const handleEdit = (record: any) => {
    setFormData({
      id: record.id,
      positionName: record.positionName,
      positionCode: record.positionCode,
      status: record.status,
      sort: record.sort,
      remark: record.remark
    })
    setDialogTitle('编辑岗位')
    setDialogVisible(true)
  }

  const handleDelete = (record: any) => {
    Modal.confirm({
      title: '删除确认',
      content: `确定要删除岗位"${record.positionName}"吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deletePosition(record.id)
          message.success('删除成功')
          getPositionData()
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
      content: `确定要${newStatus === 1 ? '启用' : '禁用'}岗位"${record.positionName}"吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await updatePositionStatus(record.id, newStatus)
          message.success(`${newStatus === 1 ? '启用' : '禁用'}成功`)
          getPositionData()
        } catch (error) {
          console.error('状态变更失败:', error)
          message.error('状态变更变更')
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
        positionName: values.positionName,
        positionCode: values.positionCode,
        status: values.status,
        sort: values.sort,
        remark: values.remark
      }

      if (formData.id) {
        await updatePosition(formData.id, data)
        message.success('更新成功')
      } else {
        await createPosition(data)
        message.success('创建成功')
      }

      setDialogVisible(false)
      getPositionData()
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
      positionName: '',
      positionCode: '',
      status: 1,
      sort: 0,
      remark: ''
    })
  }

  const columns = [
    {
      title: '岗位名称',
      dataIndex: 'positionName',
      key: 'positionName'
    },
    {
      title: '岗位编码',
      dataIndex: 'positionCode',
      key: 'positionCode'
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      render: (_: any, record: any) => (
        <div style={{ textAlign: 'center' }}>{record.sort}</div>
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
    <div className="position-management-page">
      <Card title="岗位管理">
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
          <Form.Item name="positionName" label="岗位名称" rules={[rules.positionName[0]]}>
            <Input placeholder="请输入岗位名称" disabled={!!formData.id} />
          </Form.Item>

          <Form.Item name="positionCode" label="岗位编码" rules={[rules.positionCode[0]]}>
            <Input placeholder="请输入岗位编码" disabled={!!formData.id} />
          </Form.Item>

          <Form.Item name="sort" label="排序">
            <InputNumber min={0} max={999} placeholder="请输入排序号" />
          </Form.Item>

          <Form.Item name="status" label="状态">
            <Radio.Group>
              <Radio value={1}>正常</Radio>
              <Radio value={0}>禁用</Radio>
            </Radio.Group>
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
        .position-management-page {
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

export default PositionManagementPage
