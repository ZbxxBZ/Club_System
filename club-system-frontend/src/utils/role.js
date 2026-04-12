const ROLE_ALIASES = {
  student: 'student',
  STUDENT: 'student',
  ROLE_STUDENT: 'student',
  clubAdmin: 'clubAdmin',
  CLUB_ADMIN: 'clubAdmin',
  CLUBADMIN: 'clubAdmin',
  ROLE_CLUB_ADMIN: 'clubAdmin',
  schoolAdmin: 'schoolAdmin',
  SCHOOL_ADMIN: 'schoolAdmin',
  SCHOOLADMIN: 'schoolAdmin',
  ROLE_SCHOOL_ADMIN: 'schoolAdmin',
}

const STATUS_ALIASES = {
  ACTIVE: 'ACTIVE',
  ENABLED: 'ACTIVE',
  NORMAL: 'ACTIVE',
  FROZEN: 'FROZEN',
  DISABLED: 'FROZEN',
  LOCKED: 'FROZEN',
  GRADUATED: 'GRADUATED',
  CANCELED: 'CANCELED',
  CANCELLED: 'CANCELED',
}

export const PERMISSIONS = {
  STUDENT_CLUB_BROWSE: 'student:club:browse',
  STUDENT_EVENT_SIGNUP: 'student:event:signup',
  STUDENT_CLUB_JOIN: 'student:club:join',
  CLUB_INFO_MANAGE: 'club:info:manage',
  CLUB_EVENT_MANAGE: 'club:event:manage',
  CLUB_FINANCE_MANAGE: 'club:finance:manage',
  SCHOOL_CLUB_APPROVAL: 'school:club:approval',
  SCHOOL_FINANCE_AUDIT: 'school:finance:audit',
  SCHOOL_DATA_STATS: 'school:data:stats',
  SCHOOL_USER_STATUS_MANAGE: 'school:user-status:manage',
}

const PERMISSIONS_BY_ROLE = {
  student: [PERMISSIONS.STUDENT_CLUB_BROWSE, PERMISSIONS.STUDENT_EVENT_SIGNUP, PERMISSIONS.STUDENT_CLUB_JOIN],
  clubAdmin: [PERMISSIONS.CLUB_INFO_MANAGE, PERMISSIONS.CLUB_EVENT_MANAGE, PERMISSIONS.CLUB_FINANCE_MANAGE],
  schoolAdmin: [
    PERMISSIONS.SCHOOL_CLUB_APPROVAL,
    PERMISSIONS.SCHOOL_FINANCE_AUDIT,
    PERMISSIONS.SCHOOL_DATA_STATS,
    PERMISSIONS.SCHOOL_USER_STATUS_MANAGE,
  ],
}

export const DASHBOARD_PATH_BY_ROLE = {
  student: '/dashboard/student',
  clubAdmin: '/dashboard/club-admin',
  schoolAdmin: '/dashboard/school-admin',
}

export const normalizeRole = (rawRole) => {
  if (typeof rawRole !== 'string') return ''
  const trimmedRole = rawRole.trim()
  if (!trimmedRole) return ''
  return ROLE_ALIASES[trimmedRole] || ''
}

export const normalizeUserStatus = (rawStatus) => {
  if (typeof rawStatus !== 'string') return ''
  const trimmedStatus = rawStatus.trim()
  if (!trimmedStatus) return ''
  return STATUS_ALIASES[trimmedStatus.toUpperCase()] || ''
}

export const resolveRoleFromLoginData = (data = {}) => {
  const roleCandidates = [
    data.permissionType,
    data.role,
    data.roleCode,
    data.userRole,
    Array.isArray(data.roles) ? data.roles[0] : '',
  ]

  for (const roleCandidate of roleCandidates) {
    const normalizedRole = normalizeRole(roleCandidate)
    if (normalizedRole) {
      return normalizedRole
    }
  }

  return ''
}

export const getPermissionsByRole = (role) => PERMISSIONS_BY_ROLE[role] || []

export const hasRolePermission = (role, permission) => {
  if (!permission) return false
  return getPermissionsByRole(role).includes(permission)
}

export const getDefaultDashboardPath = (role) => DASHBOARD_PATH_BY_ROLE[role] || '/'
