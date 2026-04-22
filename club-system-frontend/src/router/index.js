import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/public/HomeView.vue'
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'
import StudentDashboardView from '../views/dashboard/student/StudentDashboardView.vue'
import ClubAdminDashboardView from '../views/dashboard/club-admin/ClubAdminDashboardView.vue'
import SchoolAdminDashboardView from '../views/dashboard/school-admin/SchoolAdminDashboardView.vue'
import UnauthorizedView from '../views/common/UnauthorizedView.vue'
import AccountDisabledView from '../views/common/AccountDisabledView.vue'
import CheckinView from '../views/checkin/CheckinView.vue'
import { useAuthStore } from '../stores/auth'
import { getDefaultDashboardPath, hasRolePermission, PERMISSIONS } from '../utils/role'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { public: true },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { public: true },
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      redirect: () => {
        const role = getUserRole()
        return getDefaultDashboardPath(role)
      },
    },
    {
      path: '/dashboard/student',
      name: 'studentDashboard',
      component: StudentDashboardView,
      meta: {
        roles: ['student'],
        permissions: [PERMISSIONS.STUDENT_CLUB_BROWSE, PERMISSIONS.STUDENT_EVENT_SIGNUP, PERMISSIONS.STUDENT_CLUB_JOIN],
      },
    },
    {
      path: '/dashboard/club-admin',
      name: 'clubAdminDashboard',
      component: ClubAdminDashboardView,
      meta: {
        roles: ['clubAdmin'],
        permissions: [PERMISSIONS.CLUB_INFO_MANAGE, PERMISSIONS.CLUB_EVENT_MANAGE, PERMISSIONS.CLUB_FINANCE_MANAGE],
      },
    },
    {
      path: '/dashboard/school-admin',
      name: 'schoolAdminDashboard',
      component: SchoolAdminDashboardView,
      meta: {
        roles: ['schoolAdmin'],
        permissions: [
          PERMISSIONS.SCHOOL_CLUB_APPROVAL,
          PERMISSIONS.SCHOOL_FINANCE_AUDIT,
          PERMISSIONS.SCHOOL_DATA_STATS,
          PERMISSIONS.SCHOOL_USER_STATUS_MANAGE,
        ],
      },
    },
    {
      path: '/unauthorized',
      name: 'unauthorized',
      component: UnauthorizedView,
      meta: { public: true },
    },
    {
      path: '/account-disabled',
      name: 'accountDisabled',
      component: AccountDisabledView,
      meta: { public: true },
    },
    {
      path: '/checkin',
      name: 'checkin',
      component: CheckinView,
      meta: { public: true },
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const token = authStore.token
  const role = authStore.role
  const isUserActive = authStore.isUserActive
  const isPublicRoute = to.matched.some((record) => record.meta.public)
  const requiredRoles = to.meta.roles
  const requiredPermissions = to.meta.permissions

  if (!isPublicRoute && !token) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath,
      },
    }
  }

  if (token && !isPublicRoute && !isUserActive && to.path !== '/account-disabled') {
    return '/account-disabled'
  }

  if (token && Array.isArray(requiredRoles) && requiredRoles.length > 0) {
    if (!role || !requiredRoles.includes(role)) {
      return '/unauthorized'
    }
  }

  if (token && Array.isArray(requiredPermissions) && requiredPermissions.length > 0) {
    const permissionMatched = requiredPermissions.every((permission) => hasRolePermission(role, permission))
    if (!permissionMatched) {
      return '/unauthorized'
    }
  }

  if (token && (to.path === '/login' || to.path === '/register')) {
    return getDefaultDashboardPath(role)
  }

  return true
})

export default router
