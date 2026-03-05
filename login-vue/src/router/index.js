import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import UserManagement from '../views/system/UserManagement.vue'
import DashboardLayout from '../layouts/DashboardLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/home'
      },
    {
        path: '/home',
     name: 'Home',
        component: HomeView
      },
      {
        path: '/profile',
        name: 'Profile',
        component: ProfileView,
        meta: { requiresAuth: true }
      },
      {
        path: '/system/users',
        name: 'UserManagement',
        component: UserManagement,
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查认证状态
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !token) {
    // 需要认证但没有token，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录用户访问登录页，跳转到首页
    next('/home')
  } else {
    next()
  }
})

export default router
