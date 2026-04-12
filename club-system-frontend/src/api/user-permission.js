import http from './http'

// Student abilities
export const getClubPublicListApi = (params) => http.get('/clubs/public', { params })
export const joinClubApi = (clubId, payload) => http.post(`/clubs/${clubId}/join`, payload)
export const getMyJoinClubApplyListApi = (params) => http.get('/clubs/join/mine', { params })
export const getMyJoinedClubsApi = (params) => http.get('/clubs/joined/mine', { params })
export const uploadClubApplyMaterialApi = (formData) =>
  http.post('/files/upload', formData)
export const submitClubApplyApi = (payload) => http.post('/clubs/apply', payload)
export const getMyClubApplyListApi = (params) => http.get('/clubs/apply/mine', { params })
export const getOpenEventsApi = (params) => http.get('/events/open', { params })
export const signupEventApi = (eventId) => http.post(`/events/${eventId}/signup`)
export const checkinByCodeApi = (payload) => http.post('/events/checkin', payload)
export const getEventDetailApi = (eventId) => http.get(`/events/${eventId}`)
export const cancelEventSignupApi = (eventId) => http.post(`/events/${eventId}/cancel-signup`)
export const updateProfileApi = (payload) => http.patch('/user/profile', payload)
export const getProfileApi = () => http.get('/user/profile')
export const updateBasicInfoApi = (payload) => http.patch('/user/profile/basic', payload)
export const updatePasswordApi = (payload) => http.patch('/user/profile/password', payload)
export const updateContactApi = (payload) => http.patch('/user/profile/contact', payload)

// Club admin abilities
export const getMyClubProfileApi = () => http.get('/club-admin/me/club')
export const updateMyClubProfileApi = (payload) => http.put('/club-admin/me/club', payload)
export const getMyClubRecruitConfigApi = () => http.get('/club-admin/me/club/recruit-config')
export const updateMyClubRecruitConfigApi = (payload) => http.put('/club-admin/me/club/recruit-config', payload)
export const getMyClubPositionsApi = (params) => http.get('/club-admin/me/positions', { params })
export const createMyClubPositionApi = (payload) => http.post('/club-admin/me/positions', payload)
export const updateMyClubPositionApi = (positionId, payload) => http.patch(`/club-admin/me/positions/${positionId}`, payload)
export const deleteMyClubPositionApi = (positionId) => http.delete(`/club-admin/me/positions/${positionId}`)
export const submitClubCancelApplyApi = (payload) => http.post('/club-admin/me/club/cancel', payload)
export const getMyClubCancelApplyListApi = (params) => http.get('/club-admin/me/club/cancel-applies', { params })
export const getMyClubMembersApi = (params) => http.get('/club-admin/me/members', { params })
export const updateMemberRoleApi = (memberId, payload) => http.patch(`/club-admin/me/members/${memberId}/role`, payload)
export const removeMemberApi = (memberId) => http.delete(`/club-admin/me/members/${memberId}`)
export const getMyClubJoinApplyQueueApi = (params) => http.get('/club-admin/me/join-applies', { params })
export const decideMyClubJoinApplyApi = (applyId, payload) => http.post(`/club-admin/me/join-applies/${applyId}/decision`, payload)
export const getMyClubFinanceApi = (params) => http.get('/club-admin/me/finances', { params })
export const createIncomeApi = (payload) => http.post('/club-admin/me/incomes', payload)
export const getMyClubIncomesApi = (params) => http.get('/club-admin/me/incomes', { params })
export const createExpenseApi = (payload) => http.post('/club-admin/me/expenses', payload)
export const resubmitExpenseApi = (expenseId, payload) => http.put(`/club-admin/me/expenses/${expenseId}/resubmit`, payload)
export const getMyClubExpensesApi = (params) => http.get('/club-admin/me/expenses', { params })
export const getMyClubLedgerApi = (params) => http.get('/club-admin/me/ledger', { params })
export const getMyClubBalanceApi = () => http.get('/club-admin/me/balance')
export const getMyClubEventsApi = (params) => http.get('/club-admin/me/events', { params })
export const createMyClubEventApi = (payload) => http.post('/club-admin/me/events', payload)
export const getMyClubEventDetailApi = (eventId) => http.get(`/club-admin/me/events/${eventId}`)
export const resubmitMyClubEventApi = (eventId, payload) => http.put(`/club-admin/me/events/${eventId}`, payload)
export const getMyClubEventSignupsApi = (eventId, params) => http.get(`/club-admin/me/events/${eventId}/signups`, { params })
export const updateEventCheckinCodeApi = (eventId, payload) => http.patch(`/club-admin/me/events/${eventId}/checkin-code`, payload)
export const checkinMyClubEventApi = (eventId, payload) => http.post(`/club-admin/me/events/${eventId}/checkin`, payload)
export const cancelCheckinMyClubEventApi = (eventId, userId) => http.delete(`/club-admin/me/events/${eventId}/checkin/${userId}`)
export const submitMyClubEventSummaryApi = (eventId, payload) => http.post(`/club-admin/me/events/${eventId}/summary`, payload)
export const getMyClubEventSummaryApi = (eventId) => http.get(`/club-admin/me/events/${eventId}/summary`)

// School admin abilities
export const getUserListApi = (params) => http.get('/school-admin/users', { params })
export const updateUserStatusApi = (userId, payload) => http.patch(`/school-admin/users/${userId}/status`, payload)
export const disableUserApi = (userId, reason = '学校管理员手动禁用') =>
  updateUserStatusApi(userId, { status: 'FROZEN', reason })
export const restoreUserApi = (userId, reason = '学校管理员手动恢复') =>
  updateUserStatusApi(userId, { status: 'ACTIVE', reason })
export const updateUserRoleApi = (userId, payload) => http.patch(`/school-admin/users/${userId}/role`, payload)
export const updateClubStatusApi = (clubId, payload) => http.patch(`/school-admin/clubs/${clubId}/status`, payload)
export const freezeClubApi = (clubId, reason = '学校管理员手动冻结社团') =>
  updateClubStatusApi(clubId, { status: 'FROZEN', reason })
export const enableClubApi = (clubId, reason = '学校管理员手动启用社团') =>
  updateClubStatusApi(clubId, { status: 'ACTIVE', reason })
export const graduateStudentsExitClubApi = (payload) => http.post('/school-admin/users/graduation/exit-clubs', payload)
export const freezeClubAccountsApi = (clubId, payload) => http.post(`/school-admin/clubs/${clubId}/freeze-accounts`, payload)

export const getClubApprovalQueueApi = (params) => http.get('/school-admin/approvals/clubs', { params })
export const getClubApplyDetailApi = (approvalId) => http.get(`/school-admin/approvals/clubs/${approvalId}`)
export const updateClubApplyStatusApi = (approvalId, payload) =>
  http.patch(`/school-admin/approvals/clubs/${approvalId}/status`, payload)
export const getClubCancelApprovalQueueApi = (params) => http.get('/school-admin/approvals/club-cancels', { params })
export const getClubCancelApprovalDetailApi = (cancelId) => http.get(`/school-admin/approvals/club-cancels/${cancelId}`)
export const updateClubCancelApprovalStatusApi = (cancelId, payload) =>
  http.patch(`/school-admin/approvals/club-cancels/${cancelId}/status`, payload)
export const decideClubApprovalApi = (approvalId, payload) =>
  http.post(`/school-admin/approvals/clubs/${approvalId}/decision`, payload)
export const getSchoolClubManageListApi = (params) => http.get('/school-admin/clubs/manage', { params })
export const getSchoolClubManageDetailApi = (clubId) => http.get(`/school-admin/clubs/${clubId}/manage-detail`)
export const updateSchoolClubManageApi = (clubId, payload) => http.patch(`/school-admin/clubs/${clubId}`, payload)

export const getAuditStatsApi = (params) => http.get('/school-admin/statistics/overview', { params })
export const getEventApprovalQueueApi = (params) => http.get('/school-admin/approvals/events', { params })
export const getEventApprovalDetailApi = (eventId) => http.get(`/school-admin/approvals/events/${eventId}`)
export const decideEventApprovalApi = (eventId, payload) => http.post(`/school-admin/approvals/events/${eventId}/decision`, payload)
export const getEventSummaryListApi = (params) => http.get('/school-admin/event-summaries', { params })
export const getSchoolAdminEventSummaryApi = (eventId) => http.get(`/school-admin/events/${eventId}/summary`)

// School admin scheduled task controls
export const getScheduledTaskStatusApi = (taskCodes = []) =>
  http.get('/school-admin/scheduled-tasks/status', {
    params: {
      taskCodes: Array.isArray(taskCodes) ? taskCodes.join(',') : taskCodes,
    },
  })

export const commandScheduledTaskApi = (taskCode, payload) =>
  http.post(`/school-admin/scheduled-tasks/${taskCode}/command`, payload)

// School admin finance management
export const getExpenseApprovalQueueApi = (params) => http.get('/school-admin/approvals/expenses', { params })
export const getExpenseApprovalDetailApi = (expenseId) => http.get(`/school-admin/approvals/expenses/${expenseId}`)
export const decideExpenseApprovalApi = (expenseId, payload) => http.post(`/school-admin/approvals/expenses/${expenseId}/decision`, payload)
export const getClubLedgerApi = (clubId, params) => http.get(`/school-admin/clubs/${clubId}/ledger`, { params })
export const getClubFinanceReportApi = (clubId, params) => http.get(`/school-admin/clubs/${clubId}/finance-report`, { params })
export const getAnomalyExpensesApi = (params) => http.get('/school-admin/anomaly-expenses', { params })
export const getClubStatisticsApi = (params) => http.get('/school-admin/statistics/clubs', { params })
export const getEventStatisticsApi = (params) => http.get('/school-admin/statistics/events', { params })
export const getFinanceStatisticsApi = (params) => http.get('/school-admin/statistics/finance', { params })
