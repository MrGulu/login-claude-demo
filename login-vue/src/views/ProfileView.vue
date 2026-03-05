<template>
  <div class="profile-view">
    <div class="profile-header">
      <h1 class="page-title">个人资料</h1>
      <p class="page-subtitle">管理您的个人信息和账户设置</p>
    </div>

    <div class="profile-content">
      <!-- 头像和基本信息卡片 -->
      <div class="profile-card">
        <div class="card-header">
          <h2 class="card-title">基本信息</h2>
        </div>
        <div class="card-body">
          <div class="profile-layout">
            <!-- 头像上传区 -->
         <div class="avatar-section">
              <div class="avatar-wrapper">
         <img :src="avatarUrl || defaultAvatar" alt="头像" class="avatar-preview" />
                <div class="avatar-overlay">
            <el-icon class="upload-icon"><Upload /></el-icon>
              </div>
           </div>
              <el-upload
                :show-file-list="false"
                :before-upload="handleAvatarChange"
         accept="image/jpeg,image/png,image/gif"
              >
         <el-button type="primary" class="upload-btn">
            <el-icon><Upload /></el-icon>
              上传头像
             </el-button>
              </el-upload>
           <p class="avatar-tip">支持 JPG、PNG、GIF 格式，大小不超过 2MB</p>
      </div>

            <!-- 基本信息表单 -->
            <div class="form-section">
              <el-form
                ref="profileFormRef"
                :model="profileForm"
           :rules="profileRules"
                label-width="80px"
                label-position="left"
        >
                <el-form-item label="用户名">
         <el-input v-model="profileForm.username" disabled />
                </el-form-item>
          <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
         </el-form-item>
                <el-form-item label="邮箱" prop="email">
               <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" maxlength="11" />
             </el-form-item>
           <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="profileLoading">
              保存修改
             </el-button>
                </el-form-item>
          </el-form>
            </div>
          </div>
        </div>
      </div>

      <!-- 修改密码卡片 -->
      <div class="profile-card">
        <div class="card-header">
       <h2 class="card-title">修改密码</h2>
        </div>
      <div class="card-body">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
        :rules="passwordRules"
       label-width="100px"
            label-position="left"
            class="password-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
           show-password
              />
          </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
              v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（6-20个字符）"
          show-password
         />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="验证码" prop="captcha">
              <div class="captcha-input">
          <el-input
                  v-model="passwordForm.captcha"
         placeholder="请输入验证码"
           maxlength="4"
                  style="flex: 1"
              />
                <img
            :src="captchaImage"
              alt="验证码"
             class="captcha-image"
               @click="refreshCaptcha"
                  title="点击刷新"
                />
            </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                修改密码
              </el-button>
          </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '../api/auth'
import { updateProfile, uploadAvatar, changePassword } from '../api/user'
import { getCaptcha } from '../api/captcha'
import { useUserInfo } from '../composables/useUserInfo'

// 使用全局用户信息
const { updateUserInfo } = useUserInfo()

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2RjMjYyNiIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjQwIiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZG9taW5hbnQtYmFzZWxpbmU9Im1pZGRsZSI+VTwvdGV4dD48L3N2Zz4='

// 表单引用
const profileFormRef = ref(null)
const passwordFormRef = ref(null)

// 加载状态
const profileLoading = ref(false)
const passwordLoading = ref(false)

// 头像URL
const avatarUrl = ref('')

// 验证码
const captchaImage = ref('')
const captchaKey = ref('')

// 基本信息表单
const profileForm = ref({
  username: '',
  nickname: '',
  email: '',
  phone: ''
})

// 密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  captcha: ''
})

// 验证确认密码
const validatePasswordMatch = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 基本信息验证规则
const profileRules = {
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validatePasswordMatch, trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为4位', trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    const userInfo = res.data

    profileForm.value = {
      username: userInfo.username || '',
      nickname: userInfo.nickname || '',
      email: userInfo.email || '',
      phone: userInfo.phone || ''
    }

    avatarUrl.value = userInfo.avatar || ''
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

// 更新基本信息
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  try {
    // 验证表单
    await profileFormRef.value.validate()

    profileLoading.value = true
    const res = await updateProfile({
      nickname: profileForm.value.nickname,
      email: profileForm.value.email,
      phone: profileForm.value.phone
    })

    console.log('更新成功，响应数据:', res)

    // 使用全局更新方法
    updateUserInfo({
      nickname: res.data.nickname,
      email: res.data.email,
      phone: res.data.phone
    })

  console.log('准备显示成功消息')
    ElMessage({
      message: '保存成功',
      type: 'success',
      duration: 3000
  })
    console.log('成功消息已调用')
  } catch (error) {
    console.error('更新失败:', error)
    // 如果是表单验证错误，不显示错误消息
    if (error && error.message) {
      ElMessage.error(error.message || '更新失败')
    }
  } finally {
    profileLoading.value = false
  }
}

// 处理头像上传
const handleAvatarChange = (file) => {
  // 文件大小验证
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('头像大小不能超过2MB')
    return false
  }

  // 文件类型验证
  const validTypes = ['image/jpeg', 'image/png', 'image/gif']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('只支持 JPG、PNG、GIF 格式')
    return false
  }

  // 转换为Base64
  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      const res = await uploadAvatar({ avatar: e.target.result })
      avatarUrl.value = res.data.avatar

      // 使用全局更新方法
      updateUserInfo({ avatar: res.data.avatar })

      ElMessage.success('头像上传成功')
    } catch (error) {
      ElMessage.error(error.message || '头像上传失败')
    }
  }
  reader.readAsDataURL(file)
  return false // 阻止自动上传
}

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaImage.value = res.data.captchaImage
    captchaKey.value = res.data.captchaKey
  } catch (error) {
    ElMessage.error('获取验证码失败')
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    // 验证表单
    await passwordFormRef.value.validate()

    passwordLoading.value = true
    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword,
      confirmPassword: passwordForm.value.confirmPassword,
      captcha: passwordForm.value.captcha,
      captchaKey: captchaKey.value
    })

    // 清空表单
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
      captcha: ''
    }
    passwordFormRef.value.resetFields()

    // 刷新验证码
    await refreshCaptcha()

    ElMessage.success('密码修改成功')
  } catch (error) {
    // 刷新验证码
    await refreshCaptcha()
    // 如果是表单验证错误，不显示错误消息
    if (error && error.message) {
      ElMessage.error(error.message || '密码修改失败')
    }
  } finally {
    passwordLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
  refreshCaptcha()
})
</script>

<style scoped>
.profile-view {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(220, 38, 38, 0.1);
  box-shadow: 0 8px 32px rgba(220, 38, 38, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 12px 48px rgba(220, 38, 38, 0.12);
  transform: translateY(-2px);
}
.card-header {
  padding: 24px 32px;
  border-bottom: 1px solid rgba(220, 38, 38, 0.1);
  background: linear-gradient(135deg, rgba(220, 38, 38, 0.03), rgba(245, 158, 11, 0.03));
}
.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.card-body {
  padding: 32px;
}

.profile-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 48px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid rgba(220, 38, 38, 0.1);
  box-shadow: 0 8px 24px rgba(220, 38, 0.15);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  border-color: rgba(220, 38, 38, 0.3);
  box-shadow: 0 12px 32px rgba(220, 38, 38, 0.25);
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.upload-icon {
  font-size: 32px;
  color: white;
}

.upload-btn {
  width: 100%;
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  border: none;
  border-radius: 12px;
  height: 40px;
  font-weight: 500;
}

.upload-btn:hover {
  background: linear-gradient(135deg, #b91c1c, #991b1b);
}

.avatar-tip {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin: 0;
  line-height: 1.5;
}

.form-section {
  flex: 1;
}

.password-form {
  max-width: 500px;
}

.captcha-input {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
}

.captcha-image:hover {
  border-color: #dc2626;
  box-shadow: 0 2px 8px rgba(220, 38, 38, 0.2);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.08);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 16px rgba(220, 38, 38, 0.15);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  border: none;
  border-radius: 12px;
  padding: 12px 32px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #b91c1c, #991b1b);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(220, 38, 38, 0.3);
}

@media (max-width: 768px) {
  .profile-layout {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .card-body {
    padding: 24px;
  }

  .password-form {
    max-width: 100%;
  }
}
</style>
