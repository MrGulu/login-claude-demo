import { createApp } from 'vue'
import ErrorToast from '../components/ErrorToast.vue'
import SuccessToast from '../components/SuccessToast.vue'

let toastInstance = null

export function showError(message, duration = 4000, title = '操作失败') {
  // 如果已有提示，先移除
  if (toastInstance) {
    toastInstance.unmount()
    toastInstance = null
  }

  // 创建容器
  const container = document.createElement('div')
  document.body.appendChild(container)

  // 创建组件实例
  const app = createApp(ErrorToast, {
    title,
    message,
    duration,
    onClose: () => {
      app.unmount()
      document.body.removeChild(container)
      toastInstance = null
    }
  })

  toastInstance = app
  app.mount(container)
}

export function showSuccess(message, duration = 3000) {
  // 如果已有提示，先移除
  if (toastInstance) {
    toastInstance.unmount()
    toastInstance = null
  }

  // 创建容器
  const container = document.createElement('div')
  document.body.appendChild(container)

  // 创建组件实例
  const app = createApp(SuccessToast, {
    message,
    duration,
    onClose: () => {
      app.unmount()
      document.body.removeChild(container)
      toastInstance = null
    }
  })

  toastInstance = app
  app.mount(container)
}
