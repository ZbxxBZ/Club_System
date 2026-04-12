import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  clearAccessToken,
  getAccessToken,
  getUserProfile,
  getUserRole,
  getUserStatus,
  setAccessToken,
  setUserProfile,
  setUserRole,
} from '../utils/token'
import { getPermissionsByRole, normalizeRole, normalizeUserStatus } from '../utils/role'

export const useAuthStore = defineStore('auth', () => {
  const token = ref('')
  const role = ref('')
  const user = ref(null)

  const isAuthenticated = computed(() => Boolean(token.value))
  const permissions = computed(() => getPermissionsByRole(role.value))
  const userStatus = computed(() => normalizeUserStatus(user.value?.status || getUserStatus()))
  const isUserActive = computed(() => userStatus.value === 'ACTIVE')

  const bootstrap = () => {
    token.value = getAccessToken() || ''
    role.value = normalizeRole(getUserRole())
    user.value = getUserProfile()
  }

  const setSession = ({ accessToken, userRole, userProfile, remember = true }) => {
    if (accessToken) {
      setAccessToken(accessToken, remember)
      token.value = accessToken
    }

    const normalizedRole = normalizeRole(userRole)
    if (normalizedRole) {
      setUserRole(normalizedRole, remember)
      role.value = normalizedRole
    }

    if (userProfile && typeof userProfile === 'object') {
      setUserProfile(userProfile, remember)
      user.value = userProfile
    }
  }

  const logout = () => {
    clearAccessToken()
    token.value = ''
    role.value = ''
    user.value = null
  }

  return {
    token,
    role,
    user,
    isAuthenticated,
    permissions,
    userStatus,
    isUserActive,
    bootstrap,
    setSession,
    logout,
  }
})
