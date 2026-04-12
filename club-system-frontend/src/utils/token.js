const ACCESS_TOKEN_KEY = 'club_access_token'
const USER_ROLE_KEY = 'club_user_role'
const USER_PROFILE_KEY = 'club_user_profile'

export const getAccessToken = () => localStorage.getItem(ACCESS_TOKEN_KEY) || sessionStorage.getItem(ACCESS_TOKEN_KEY)

export const getUserRole = () => localStorage.getItem(USER_ROLE_KEY) || sessionStorage.getItem(USER_ROLE_KEY)

export const getUserProfile = () => {
  const raw = localStorage.getItem(USER_PROFILE_KEY) || sessionStorage.getItem(USER_PROFILE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export const getUserStatus = () => {
  const profile = getUserProfile()
  if (!profile || typeof profile !== 'object') return ''
  return typeof profile.status === 'string' ? profile.status : ''
}

export const setAccessToken = (token, remember = true) => {
  if (!token) return
  if (remember) {
    localStorage.setItem(ACCESS_TOKEN_KEY, token)
    sessionStorage.removeItem(ACCESS_TOKEN_KEY)
  } else {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, token)
    localStorage.removeItem(ACCESS_TOKEN_KEY)
  }
}

export const setUserRole = (role, remember = true) => {
  if (!role) return
  if (remember) {
    localStorage.setItem(USER_ROLE_KEY, role)
    sessionStorage.removeItem(USER_ROLE_KEY)
  } else {
    sessionStorage.setItem(USER_ROLE_KEY, role)
    localStorage.removeItem(USER_ROLE_KEY)
  }
}

export const setUserProfile = (profile, remember = true) => {
  if (!profile || typeof profile !== 'object') return
  const serialized = JSON.stringify(profile)
  if (remember) {
    localStorage.setItem(USER_PROFILE_KEY, serialized)
    sessionStorage.removeItem(USER_PROFILE_KEY)
  } else {
    sessionStorage.setItem(USER_PROFILE_KEY, serialized)
    localStorage.removeItem(USER_PROFILE_KEY)
  }
}

export const clearAccessToken = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  sessionStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(USER_ROLE_KEY)
  sessionStorage.removeItem(USER_ROLE_KEY)
  localStorage.removeItem(USER_PROFILE_KEY)
  sessionStorage.removeItem(USER_PROFILE_KEY)
}
