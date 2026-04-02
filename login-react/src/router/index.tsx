import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom'
import { lazy, Suspense } from 'react'

// 懒加载组件
const LoginPage = lazy(() => import('@/pages/LoginPage'))
const DashboardLayout = lazy(() => import('@/layouts/DashboardLayout'))
const HomePage = lazy(() => import('@/pages/HomePage'))
const ProfilePage = lazy(() => import('@/pages/ProfilePage'))
const UserManagementPage = lazy(() => import('@/pages/system/UserManagementPage'))
const RoleManagementPage = lazy(() => import('@/pages/system/RoleManagementPage'))
const PositionManagementPage = lazy(() => import('@/pages/system/PositionManagementPage'))

// 私有路由组件
const PrivateRoute = ({ children }: { children: React.ReactNode }) => {
  const token = localStorage.getItem('token')
  if (!token) {
    return <Navigate to="/login" replace />
  }
  return <>{children}</>
}

// 加载组件
const PageWrapper = ({ children }: { children: React.ReactNode }) => {
  return (
    <Suspense
      fallback={
        <div
          style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
            fontSize: '18px',
            color: 'var(--color-text-muted)'
          }}
        >
          加载中...
        </div>
      }
    >
      {children}
    </Suspense>
  )
}

// 路由配置
const router = createBrowserRouter([
  {
    path: '/login',
    element: (
      <PageWrapper>
        <LoginPage />
      </PageWrapper>
    )
  },
  {
    path: '/',
    element: (
      <PrivateRoute>
        <PageWrapper>
          <DashboardLayout />
        </PageWrapper>
      </PrivateRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/home" replace />
      },
      {
        path: 'home',
        element: (
          <PageWrapper>
            <HomePage />
          </PageWrapper>
        )
      },
      {
        path: 'profile',
        element: (
          <PageWrapper>
            <ProfilePage />
          </PageWrapper>
        )
      },
      {
        path: 'system/users',
        element: (
          <PageWrapper>
            <UserManagementPage />
          </PageWrapper>
        )
      },
      {
        path: 'system/roles',
        element: (
          <PageWrapper>
            <RoleManagementPage />
          </PageWrapper>
        )
      },
      {
        path: 'system/positions',
        element: (
          <PageWrapper>
            <PositionManagementPage />
          </PageWrapper>
        )
      }
    ]
  }
])

export default router
