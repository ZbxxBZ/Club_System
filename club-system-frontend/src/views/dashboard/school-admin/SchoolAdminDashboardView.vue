<script setup>
import { computed, onMounted, onBeforeUnmount, nextTick, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../stores/auth'
import {
  commandScheduledTaskApi,
  disableUserApi,
  enableClubApi,
  freezeClubApi,
  getAuditStatsApi,
  getClubCancelApprovalDetailApi,
  getClubCancelApprovalQueueApi,
  getClubApplyDetailApi,
  getClubApprovalQueueApi,
  getSchoolClubManageDetailApi,
  getSchoolClubManageListApi,
  getScheduledTaskStatusApi,
  getUserListApi,
  restoreUserApi,
  updateClubCancelApprovalStatusApi,
  updateSchoolClubManageApi,
  updateClubApplyStatusApi,
  updateUserRoleApi,
  getEventApprovalQueueApi,
  getEventApprovalDetailApi,
  decideEventApprovalApi,
  getEventSummaryListApi,
  getSchoolAdminEventSummaryApi,
  getExpenseApprovalQueueApi,
  getExpenseApprovalDetailApi,
  decideExpenseApprovalApi,
  getClubLedgerApi,
  getClubFinanceReportApi,
  getAnomalyExpensesApi,
  getClubStatisticsApi,
  getEventStatisticsApi,
  getFinanceStatisticsApi,
} from '../../../api/user-permission'
import * as echarts from 'echarts'
import * as XLSX from 'xlsx'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const feedback = ref('')
const activeMenu = ref('stats')
const approvalMenuExpanded = ref(true)
const users = ref([])
const selectedUserIds = ref([])
const userFilters = reactive({
  keyword: '',
  roleCode: '',
  status: '',
})
const userRoleOptions = [
  { label: '学生', value: 'STUDENT' },
  { label: '社团管理员', value: 'CLUB_ADMIN' },
  { label: '学校管理员', value: 'SCHOOL_ADMIN' },
]
const userStatusOptions = [
  { label: '正常', value: 'ACTIVE' },
  { label: '禁用', value: 'FROZEN' },
  { label: '已毕业', value: 'GRADUATED' },
  { label: '已注销', value: 'CANCELED' },
]
const managedClubs = ref([])
const manageLoading = ref(false)
const manageFilters = reactive({
  keyword: '',
  category: '',
})
const manageCategoryOptions = ['文化类', '体育类', '学术类']
const manageDetailVisible = ref(false)
const manageDetailLoading = ref(false)
const manageDetail = ref(null)
const manageEditVisible = ref(false)
const manageEditSubmitting = ref(false)
const manageEditForm = reactive({
  clubId: null,
  clubName: '',
  category: '',
  purpose: '',
  instructorName: '',
})
const clubs = ref([])
const clubLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const clubApplyDetail = ref(null)
const clubFilters = reactive({
  keyword: '',
  applyStatus: '',
})
const cancelClubs = ref([])
const cancelLoading = ref(false)
const cancelDetailVisible = ref(false)
const cancelDetailLoading = ref(false)
const cancelApplyDetail = ref(null)
const cancelFilters = reactive({
  keyword: '',
  cancelStatus: '',
})
const taskState = reactive({
  GRADUATION_EXIT_CLUB: 'STOPPED',
  CLUB_CANCEL_FREEZE_ACCOUNT: 'STOPPED',
})
const taskLoading = reactive({
  GRADUATION_EXIT_CLUB: false,
  CLUB_CANCEL_FREEZE_ACCOUNT: false,
})
const stats = reactive({
  activeClubCount: 0,
  totalClubCount: 0,
  activeUserCount: 0,
  totalUserCount: 0,
  pendingApprovalCount: 0,
  suspiciousExpenseCount: 0,
})
const eventApprovalList = ref([])
const eventApprovalLoading = ref(false)
const eventApprovalDetailDialog = reactive({ visible: false, event: null })
const eventDecisionLoading = ref(false)
const eventApprovalSearch = reactive({ keyword: '', eventStatus: 2 })
const eventSummaryList = ref([])
const eventSummaryLoading = ref(false)
const eventSummaryViewDialog = reactive({ visible: false, summary: null })
const eventManageList = ref([])
const eventManageLoading = ref(false)
const eventManageSearch = reactive({ keyword: '', eventStatus: null })
const eventManageDetailDialog = reactive({ visible: false, event: null })
const eventManageSummaryDialog = reactive({ visible: false, summary: null })

// -- Finance: expense approval --
const expenseApprovalList = ref([])
const expenseApprovalTotal = ref(0)
const expenseApprovalPage = ref(1)
const expenseApprovalKeyword = ref('')
const expenseApprovalLoading = ref(false)
const selectedExpenseDetail = ref(null)
const expenseDetailVisible = ref(false)
const expenseDetailLoading = ref(false)
const expenseDecisionForm = reactive({ action: '', rejectReason: '' })
const expenseDecisionLoading = ref(false)
const rejectDialogVisible = ref(false)
const financeMenuExpanded = ref(true)

// -- Finance: monitor --
const monitorClubId = ref(null)
const monitorLedgerList = ref([])
const monitorLedgerTotal = ref(0)
const monitorLedgerPage = ref(1)
const monitorBizType = ref('')
const monitorStartTime = ref('')
const monitorEndTime = ref('')
const monitorLoading = ref(false)

// -- Finance: report --
const reportClubId = ref(null)
const reportYear = ref(new Date().getFullYear())
const reportData = ref(null)
const reportLoading = ref(false)
const clubListForSelect = ref([])
const monthlyChartRef = ref(null)
const categoryChartRef = ref(null)
let monthlyChart = null
let categoryChart = null

// -- Anomaly expenses --
const anomalyExpenseList = ref([])
const anomalyExpenseTotal = ref(0)
const anomalyExpensePage = ref(1)
const anomalyExpenseKeyword = ref('')
const anomalyExpenseLoading = ref(false)

// -- Statistics --
const statsMenuExpanded = ref(false)
const statsYear = ref(new Date().getFullYear())
const clubStatsData = ref(null)
const clubStatsLoading = ref(false)
const eventStatsData = ref(null)
const eventStatsLoading = ref(false)
const financeStatsData = ref(null)
const financeStatsLoading = ref(false)
// Chart refs
const clubCategoryChartRef = ref(null)
const clubStatusChartRef = ref(null)
const clubMemberSizeChartRef = ref(null)
const eventMonthlyChartRef = ref(null)
const eventStatusChartRef = ref(null)
const financeMonthlyChartRef = ref(null)
const financeIncomeTypeChartRef = ref(null)
let clubCategoryChart = null, clubStatusChart = null, clubMemberSizeChart = null
let eventMonthlyChart = null, eventStatusChart = null
let financeMonthlyStatsChart = null, financeIncomeTypeChart = null

const getBizData = (response) => {
  if (response && typeof response.code !== 'undefined') {
    if (response.code !== 0) {
      throw new Error(response.message || '请求失败')
    }
    return response.data
  }
  return response
}

const normalizeStatus = (status) => {
  if (status === null || typeof status === 'undefined') return ''
  const raw = String(status).trim()
  if (!raw) return ''
  const upper = raw.toUpperCase()
  const statusMap = {
    ACTIVE: 'ACTIVE',
    ENABLED: 'ACTIVE',
    NORMAL: 'ACTIVE',
    '1': 'ACTIVE',
    FROZEN: 'FROZEN',
    DISABLED: 'FROZEN',
    LOCKED: 'FROZEN',
    '2': 'FROZEN',
    禁用: 'FROZEN',
    冻结: 'FROZEN',
    正常: 'ACTIVE',
  }
  return statusMap[upper] || statusMap[raw] || upper
}

const STATUS_TEXT_MAP = {
  ACTIVE: '正常',
  FROZEN: '禁用',
  GRADUATED: '已毕业',
  CANCELED: '已注销',
}

const getUserStatusText = (status) => STATUS_TEXT_MAP[normalizeStatus(status)] || '未知'

const APPLY_STATUS_TEXT_MAP = {
  1: '待初审',
  2: '答辩中',
  3: '公示中',
  4: '通过',
  5: '驳回',
}

const parseApplyStatus = (status) => {
  if (typeof status === 'number') return status
  const parsed = Number(status)
  return Number.isNaN(parsed) ? 0 : parsed
}

const getApplyStatusText = (status) => APPLY_STATUS_TEXT_MAP[parseApplyStatus(status)] || '未知'

const CANCEL_STATUS_TEXT_MAP = {
  1: '待公示',
  2: '待经费结清',
  3: '待资产移交',
  4: '已完成',
  5: '驳回',
}

const parseCancelStatus = (status) => {
  if (typeof status === 'number') return status
  const parsed = Number(status)
  return Number.isNaN(parsed) ? 0 : parsed
}

const getCancelStatusText = (status) => CANCEL_STATUS_TEXT_MAP[parseCancelStatus(status)] || '未知'

const getCancelStatusClass = (status) => {
  const parsed = parseCancelStatus(status)
  if (parsed === 4) return 'approved'
  if (parsed === 5) return 'rejected'
  if ([1, 2, 3].includes(parsed)) return 'pending'
  return 'warning'
}

const CLUB_STATUS_TEXT_MAP = {
  1: '待审核',
  2: '正常',
  3: '限期整改/冻结',
  4: '待注销',
  5: '已注销',
}

const parseClubStatus = (status) => {
  if (typeof status === 'number') return status
  const parsed = Number(status)
  return Number.isNaN(parsed) ? 0 : parsed
}

const getClubStatusText = (status) => CLUB_STATUS_TEXT_MAP[parseClubStatus(status)] || '未知'

const canEditClub = (record = {}) => {
  if (typeof record.canEdit === 'boolean') return record.canEdit
  const clubStatus = parseClubStatus(record.status)
  const applyStatus = parseApplyStatus(record.applyStatus)
  return clubStatus !== 5 && ![1, 2, 3].includes(applyStatus)
}

const localizeDisabledReason = (reason, fallback) => {
  if (!reason) return fallback
  const normalized = String(reason).trim()
  if (!normalized) return fallback

  const lower = normalized.toLowerCase()
  if (lower.includes('only rejected-pending-review or canceled clubs can be deleted')) {
    return '仅驳回待审核或已注销的社团允许删除'
  }
  if (lower.includes('cannot be edited') || lower.includes('editing is not allowed')) {
    return '审批进行中或社团已注销，暂不允许修改'
  }
  if (lower.includes('cannot be deleted') || lower.includes('deletion is not allowed')) {
    return '当前状态不允许删除'
  }

  return normalized
}

const getEditDisabledReason = (record = {}) => {
  if (canEditClub(record)) return ''
  return localizeDisabledReason(record.editDisabledReason, '审批进行中或社团已注销，暂不允许修改')
}

const canToggleClubFreeze = (record = {}) => {
  const clubStatus = parseClubStatus(record.status)
  return [2, 3].includes(clubStatus)
}

const isClubFrozen = (record = {}) => parseClubStatus(record.status) === 3

const getToggleFreezeText = (record = {}) => (isClubFrozen(record) ? '解冻' : '冻结')

const getToggleFreezeDisabledReason = (record = {}) => {
  if (canToggleClubFreeze(record)) return ''
  const clubStatus = parseClubStatus(record.status)
  if (clubStatus === 5) return '已注销社团不允许冻结或解冻'
  if (clubStatus === 1) return '待审核社团不允许冻结或解冻'
  if (clubStatus === 4) return '待注销社团不允许冻结或解冻'
  return '当前状态不允许冻结或解冻'
}

const getNextApplyStatus = (status) => {
  const current = parseApplyStatus(status)
  if (current === 1) return 2
  if (current === 2) return 3
  if (current === 3) return 4
  return null
}

const canMoveToNextStep = (status) => getNextApplyStatus(status) !== null

const canRejectApply = (status) => ![4, 5].includes(parseApplyStatus(status))

const getNextStepDisabledReason = (status) => {
  const current = parseApplyStatus(status)
  if (current === 4) return '已通过，不能继续进入下一流程'
  if (current === 5) return '已驳回，不能继续进入下一流程'
  return ''
}

const getRejectDisabledReason = (status) => {
  const current = parseApplyStatus(status)
  if (current === 4) return '已通过，不能驳回'
  if (current === 5) return '已驳回，无需重复操作'
  return ''
}

const getNextCancelStatus = (status) => {
  const current = parseCancelStatus(status)
  if (current === 1) return 2
  if (current === 2) return 3
  if (current === 3) return 4
  return null
}

const canMoveToNextCancelStep = (status) => getNextCancelStatus(status) !== null

const canRejectCancelApply = (status) => ![4, 5].includes(parseCancelStatus(status))

const getNextCancelStepDisabledReason = (status) => {
  const current = parseCancelStatus(status)
  if (current === 4) return '已完成，不能继续进入下一流程'
  if (current === 5) return '已驳回，不能继续进入下一流程'
  return ''
}

const getRejectCancelDisabledReason = (status) => {
  const current = parseCancelStatus(status)
  if (current === 4) return '已完成，不能驳回'
  if (current === 5) return '已驳回，无需重复操作'
  return ''
}

const EVENT_STATUS_MAP = { 1: '草稿', 2: '待审核', 3: '报名中', 4: '已结束', 5: '已驳回', 6: '进行中' }
const getEventStatusText = (status) => EVENT_STATUS_MAP[status] || '未知'
const getEventStatusClass = (status) => {
  if (status === 2) return 'pending'
  if (status === 3) return 'approved'
  if (status === 6) return 'approved'
  if (status === 4) return 'approved'
  if (status === 5) return 'rejected'
  return 'pending'
}

const loadEventApprovals = async () => {
  eventApprovalLoading.value = true
  try {
    const params = { pageNum: 1, pageSize: 50 }
    if (eventApprovalSearch.keyword.trim()) params.keyword = eventApprovalSearch.keyword.trim()
    if (eventApprovalSearch.eventStatus !== null) params.eventStatus = eventApprovalSearch.eventStatus
    const res = await getEventApprovalQueueApi(params)
    const data = getBizData(res)
    eventApprovalList.value = Array.isArray(data?.records) ? data.records : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动审批列表加载失败'
  } finally { eventApprovalLoading.value = false }
}

const openEventApprovalDetail = async (event) => {
  try {
    const res = await getEventApprovalDetailApi(event.id)
    eventApprovalDetailDialog.event = getBizData(res)
    eventApprovalDetailDialog.visible = true
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动详情加载失败'
  }
}

const handleEventDecision = async (action) => {
  eventDecisionLoading.value = true
  let rejectReason = ''
  if (action === 'REJECT') {
    rejectReason = window.prompt('请输入驳回原因', '') || ''
  }
  try {
    await decideEventApprovalApi(eventApprovalDetailDialog.event.id, { action, rejectReason: rejectReason.trim() })
    feedback.value = action === 'APPROVE' ? '活动已通过审批' : '活动已驳回'
    eventApprovalDetailDialog.visible = false
    await loadEventApprovals()
  } catch (error) {
    feedback.value = error?.response?.data?.message || '审批操作失败'
  } finally { eventDecisionLoading.value = false }
}

const loadEventSummaries = async () => {
  eventSummaryLoading.value = true
  try {
    const res = await getEventSummaryListApi({ pageNum: 1, pageSize: 50 })
    const data = getBizData(res)
    eventSummaryList.value = Array.isArray(data?.records) ? data.records : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动总结列表加载失败'
  } finally { eventSummaryLoading.value = false }
}

const openEventSummaryView = (summary) => {
  eventSummaryViewDialog.summary = summary
  eventSummaryViewDialog.visible = true
}

const loadEventManageList = async () => {
  eventManageLoading.value = true
  try {
    const params = { pageNum: 1, pageSize: 50 }
    if (eventManageSearch.keyword.trim()) params.keyword = eventManageSearch.keyword.trim()
    if (eventManageSearch.eventStatus !== null) params.eventStatus = eventManageSearch.eventStatus
    const res = await getEventApprovalQueueApi(params)
    const data = getBizData(res)
    eventManageList.value = Array.isArray(data?.records) ? data.records : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动管理列表加载失败'
  } finally { eventManageLoading.value = false }
}

const openEventManageDetail = async (event) => {
  try {
    const res = await getEventApprovalDetailApi(event.id)
    eventManageDetailDialog.event = getBizData(res)
    eventManageDetailDialog.visible = true
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动详情加载失败'
  }
}

const openEventManageSummary = async (event) => {
  try {
    const res = await getSchoolAdminEventSummaryApi(event.id)
    eventManageSummaryDialog.summary = getBizData(res)
    eventManageSummaryDialog.visible = true
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动总结加载失败'
  }
}

const createDetailFallback = (record = {}) => ({
  id: record.id,
  clubName: record.clubName || '-',
  applyStatus: record.applyStatus,
  initiatorRealName: record.initiatorRealName || record.applicantRealName || record.realName || '-',
  instructorName: record.instructorName || '-',
  category: record.category || '-',
  purpose: record.purpose || '-',
  remark: record.remark || '-',
  createdAt: record.createdAt || '-',
  charterUrl: record.charterUrl || '',
  instructorProofUrl: record.instructorProofUrl || '',
})

const openClubApplyDetail = async (record) => {
  const approvalId = record?.id
  if (!approvalId) return

  detailVisible.value = true
  detailLoading.value = true
  clubApplyDetail.value = createDetailFallback(record)

  try {
    const response = await getClubApplyDetailApi(approvalId)
    const data = getBizData(response) || {}
    clubApplyDetail.value = {
      ...clubApplyDetail.value,
      ...data,
    }
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团申报详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

const closeClubApplyDetail = () => {
  detailVisible.value = false
}

const createCancelDetailFallback = (record = {}) => ({
  id: record.id,
  clubId: record.clubId,
  clubName: record.clubName || '-',
  cancelStatus: record.cancelStatus,
  applyType: record.applyType,
  applyReason: record.applyReason || '-',
  initiatorRealName: record.initiatorRealName || record.applicantRealName || record.realName || '-',
  createdAt: record.createdAt || record.submittedAt || '-',
  assetSettlementUrl: record.assetSettlementUrl || record.assetSettlementFileUrl || '',
})

const openClubCancelDetail = async (record) => {
  const cancelId = record?.id
  if (!cancelId) return

  cancelDetailVisible.value = true
  cancelDetailLoading.value = true
  cancelApplyDetail.value = createCancelDetailFallback(record)

  try {
    const response = await getClubCancelApprovalDetailApi(cancelId)
    const data = getBizData(response) || {}
    cancelApplyDetail.value = {
      ...cancelApplyDetail.value,
      ...data,
    }
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团注销详情加载失败'
  } finally {
    cancelDetailLoading.value = false
  }
}

const closeClubCancelDetail = () => {
  cancelDetailVisible.value = false
}

const resolveUserStatus = (user = {}) => {
  const statusCandidates = [user.status, user.userStatus, user.accountStatus, user.state]
  for (const statusValue of statusCandidates) {
    const normalized = normalizeStatus(statusValue)
    if (normalized) {
      return normalized
    }
  }
  return ''
}

const normalizeUserRecord = (user = {}) => ({
  ...user,
  status: resolveUserStatus(user) || normalizeStatus(user.status) || '',
  roleText: resolveUserRole(user),
})

const resolveUserRole = (user = {}) => {
  const roleCandidates = [
    user.roleCode,
    user.permissionType,
    user.role,
    user.userRole,
    Array.isArray(user.roles) ? user.roles[0] : '',
  ]

  for (const candidate of roleCandidates) {
    if (!candidate) continue
    const upper = String(candidate).trim().toUpperCase()
    if (!upper) continue
    if (upper.includes('CLUB') && upper.includes('ADMIN')) return '社团管理员'
    if (upper.includes('SCHOOL') && upper.includes('ADMIN')) return '学校管理员'
    if (upper.includes('STUDENT')) return '学生'
  }

  return '未知角色'
}

const getUserRoleTagClass = (roleText) => {
  if (roleText === '学生') return 'student'
  if (roleText === '社团管理员') return 'club-admin'
  if (roleText === '学校管理员') return 'school-admin'
  return 'student'
}

const isUserClubAdmin = (user = {}) => resolveUserRole(user) === '社团管理员'

const isAllUsersSelected = computed(() => users.value.length > 0 && selectedUserIds.value.length === users.value.length)

const hasSelectedUsers = computed(() => selectedUserIds.value.length > 0)

const toggleSelectAllUsers = (event) => {
  const checked = Boolean(event?.target?.checked)
  selectedUserIds.value = checked ? users.value.map((user) => user.id) : []
}

const getSelectedUsers = () => {
  const selectedSet = new Set(selectedUserIds.value)
  return users.value.filter((user) => selectedSet.has(user.id))
}

const normalizeTaskStatus = (status) => {
  if (status === null || typeof status === 'undefined') return 'STOPPED'
  const upper = String(status).trim().toUpperCase()
  if (!upper) return 'STOPPED'
  if (['STARTED', 'RUNNING'].includes(upper)) return upper
  if (['STOPPED', 'FAILED'].includes(upper)) return upper
  return 'STOPPED'
}

const isTaskActive = (taskCode) => ['STARTED', 'RUNNING'].includes(normalizeTaskStatus(taskState[taskCode]))

const applyTaskStatusRecords = (records) => {
  const list = Array.isArray(records) ? records : []
  for (const item of list) {
    const taskCode = item?.taskCode
    if (taskCode && Object.prototype.hasOwnProperty.call(taskState, taskCode)) {
      taskState[taskCode] = normalizeTaskStatus(item?.taskStatus)
    }
  }
}

const fetchTaskStatus = async () => {
  const response = await getScheduledTaskStatusApi(['GRADUATION_EXIT_CLUB', 'CLUB_CANCEL_FREEZE_ACCOUNT'])
  const data = getBizData(response)
  applyTaskStatusRecords(data)
}

const toggleScheduledTask = async (taskCode, label) => {
  if (taskLoading[taskCode]) {
    return
  }

  const shouldStart = !isTaskActive(taskCode)
  const action = shouldStart ? 'START' : 'STOP'
  taskLoading[taskCode] = true

  try {
    const response = await commandScheduledTaskApi(taskCode, {
      action,
      reason: shouldStart ? `学校管理员启动${label}` : `学校管理员关闭${label}`,
    })
    const result = getBizData(response) || {}
    taskState[taskCode] = normalizeTaskStatus(result.taskStatus || (shouldStart ? 'STARTED' : 'STOPPED'))
    feedback.value = shouldStart ? `${label}已启动` : `${label}已关闭`

    // Keep UI state updated even if status sync fails.
    try {
      await fetchTaskStatus()
    } catch {
      // Ignore status sync failure here, command already succeeded.
    }
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || `${label}${shouldStart ? '启动' : '关闭'}失败`
  } finally {
    taskLoading[taskCode] = false
  }
}

const loadData = async () => {
  loading.value = true
  feedback.value = ''
  try {
    const [userRes, statsRes] = await Promise.all([
      getUserListApi({
        pageNum: 1,
        pageSize: 10,
        keyword: userFilters.keyword.trim(),
        roleCode: userFilters.roleCode,
        status: userFilters.status,
      }),
      getAuditStatsApi({}),
    ])
    const userData = getBizData(userRes)
    const statsData = getBizData(statsRes) || {}

    const rawUsers = Array.isArray(userData?.records) ? userData.records : Array.isArray(userData) ? userData : []
    users.value = rawUsers.map((user) => normalizeUserRecord(user))
    const validIds = new Set(users.value.map((user) => user.id))
    selectedUserIds.value = selectedUserIds.value.filter((id) => validIds.has(id))
    // Keep pending approval count from overview API; dedicated approval queue UI is replaced by club management panel.

    stats.activeClubCount = statsData.activeClubCount ?? statsData.normalClubCount ?? 0
    stats.totalClubCount = statsData.totalClubCount ?? statsData.clubCount ?? 0
    stats.activeUserCount = statsData.activeUserCount ?? 0
    stats.totalUserCount = statsData.totalUserCount ?? statsData.userCount ?? 0
    stats.pendingApprovalCount = statsData.pendingApprovalCount ?? 0
    stats.suspiciousExpenseCount = statsData.suspiciousExpenseCount ?? 0
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '数据加载失败'
  } finally {
    loading.value = false
  }
}

const loadClubList = async () => {
  clubLoading.value = true
  try {
    const response = await getClubApprovalQueueApi({
      pageNum: 1,
      pageSize: 50,
      keyword: clubFilters.keyword.trim(),
      applyStatus: clubFilters.applyStatus ? Number(clubFilters.applyStatus) : undefined,
    })
    const data = getBizData(response)
    clubs.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团审批列表加载失败'
  } finally {
    clubLoading.value = false
  }
}

const loadClubCancelList = async () => {
  cancelLoading.value = true
  try {
    const response = await getClubCancelApprovalQueueApi({
      pageNum: 1,
      pageSize: 50,
      keyword: cancelFilters.keyword.trim(),
      cancelStatus: cancelFilters.cancelStatus ? Number(cancelFilters.cancelStatus) : undefined,
    })
    const data = getBizData(response)
    cancelClubs.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团注销审核列表加载失败'
  } finally {
    cancelLoading.value = false
  }
}

const loadManagedClubs = async () => {
  manageLoading.value = true
  try {
    const response = await getSchoolClubManageListApi({
      pageNum: 1,
      pageSize: 50,
      keyword: manageFilters.keyword.trim(),
      category: manageFilters.category.trim(),
    })
    const data = getBizData(response)
    managedClubs.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团管理列表加载失败'
  } finally {
    manageLoading.value = false
  }
}

const openManageDetail = async (record) => {
  const clubId = record?.clubId || record?.id
  if (!clubId) return
  manageDetailVisible.value = true
  manageDetailLoading.value = true
  manageDetail.value = {
    ...record,
    clubId,
  }

  try {
    const response = await getSchoolClubManageDetailApi(clubId)
    const data = getBizData(response) || {}
    manageDetail.value = {
      ...manageDetail.value,
      ...data,
      clubId,
    }
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团管理详情加载失败'
  } finally {
    manageDetailLoading.value = false
  }
}

const closeManageDetail = () => {
  manageDetailVisible.value = false
}

const openManageEdit = async (record) => {
  if (!canEditClub(record)) {
    feedback.value = getEditDisabledReason(record)
    return
  }

  const clubId = record?.clubId || record?.id
  if (!clubId) return

  let editSource = {
    ...record,
    clubId,
  }

  try {
    const response = await getSchoolClubManageDetailApi(clubId)
    const data = getBizData(response) || {}
    editSource = {
      ...editSource,
      ...data,
      clubId,
    }
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团管理详情加载失败，已使用列表数据回填'
  }

  manageEditForm.clubId = editSource.clubId || null
  manageEditForm.clubName = editSource.clubName || ''
  manageEditForm.category = editSource.category || ''
  manageEditForm.purpose = editSource.purpose || ''
  manageEditForm.instructorName = editSource.instructorName || ''
  manageEditVisible.value = true
}

const closeManageEdit = () => {
  manageEditVisible.value = false
}

const submitManageEdit = async () => {
  const clubId = manageEditForm.clubId
  if (!clubId) return
  if (!manageEditForm.clubName.trim()) {
    feedback.value = '社团名称不能为空'
    return
  }
  if (!manageEditForm.category.trim()) {
    feedback.value = '社团分类不能为空'
    return
  }

  manageEditSubmitting.value = true
  try {
    const response = await updateSchoolClubManageApi(clubId, {
      clubName: manageEditForm.clubName.trim(),
      category: manageEditForm.category.trim(),
      purpose: manageEditForm.purpose.trim(),
      instructorName: manageEditForm.instructorName.trim(),
    })
    getBizData(response)
    feedback.value = '社团信息已更新'
    manageEditVisible.value = false
    await loadManagedClubs()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '社团信息更新失败'
  } finally {
    manageEditSubmitting.value = false
  }
}

const handleManageEditClick = (record) => {
  if (!canEditClub(record)) {
    feedback.value = getEditDisabledReason(record)
    return
  }
  openManageEdit(record)
}

const toggleManagedClubFreeze = async (record) => {
  if (!canToggleClubFreeze(record)) {
    feedback.value = getToggleFreezeDisabledReason(record)
    return
  }

  const clubId = record?.clubId || record?.id
  if (!clubId) return

  const shouldUnfreeze = isClubFrozen(record)
  const actionText = shouldUnfreeze ? '解冻' : '冻结'
  const ok = window.confirm(`确认${actionText}社团「${record.clubName || '-'}」吗？`)
  if (!ok) return

  try {
    const response = shouldUnfreeze
      ? await enableClubApi(clubId, '学校管理员手动解冻社团')
      : await freezeClubApi(clubId, '学校管理员手动冻结社团')
    getBizData(response)

    record.status = shouldUnfreeze ? 2 : 3
    if (manageDetail.value && (manageDetail.value.clubId === clubId || manageDetail.value.id === clubId)) {
      manageDetail.value.status = record.status
    }

    feedback.value = shouldUnfreeze ? '社团已解冻' : '社团已冻结'
    await loadManagedClubs()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || `${actionText}社团失败`
  }
}

const goNextApplyStep = async (record) => {
  const approvalId = record?.id
  if (!approvalId) return
  const nextStatus = getNextApplyStatus(record?.applyStatus)
  if (!nextStatus) {
    feedback.value = '当前状态不可继续推进'
    return
  }

  try {
    const response = await updateClubApplyStatusApi(approvalId, {
      applyStatus: nextStatus,
      opinion: '学校管理员推进审批流程',
    })
    getBizData(response)
    feedback.value = `状态已更新为${getApplyStatusText(nextStatus)}`
    await loadClubList()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '推进审批流程失败'
  }
}

const rejectApply = async (record) => {
  const approvalId = record?.id
  if (!approvalId) return
  if (parseApplyStatus(record?.applyStatus) === 5) {
    feedback.value = '该申请已驳回'
    return
  }

  try {
    const response = await updateClubApplyStatusApi(approvalId, {
      applyStatus: 5,
      opinion: '学校管理员驳回申请',
    })
    getBizData(response)
    feedback.value = '申请已驳回'
    await loadClubList()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '驳回申请失败'
  }
}

const handleNextStepClick = (record) => {
  if (!canMoveToNextStep(record?.applyStatus)) {
    feedback.value = getNextStepDisabledReason(record?.applyStatus)
    return
  }
  goNextApplyStep(record)
}

const handleRejectClick = (record) => {
  if (!canRejectApply(record?.applyStatus)) {
    feedback.value = getRejectDisabledReason(record?.applyStatus)
    return
  }
  rejectApply(record)
}

const goNextCancelStep = async (record) => {
  const cancelId = record?.id
  if (!cancelId) return
  const nextStatus = getNextCancelStatus(record?.cancelStatus)
  if (!nextStatus) {
    feedback.value = '当前状态不可继续推进'
    return
  }

  try {
    const response = await updateClubCancelApprovalStatusApi(cancelId, {
      cancelStatus: nextStatus,
      opinion: '学校管理员推进社团注销审核流程',
    })
    getBizData(response)
    feedback.value = `注销状态已更新为${getCancelStatusText(nextStatus)}`
    await loadClubCancelList()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '推进社团注销审核流程失败'
  }
}

const rejectCancelApply = async (record) => {
  const cancelId = record?.id
  if (!cancelId) return
  if (parseCancelStatus(record?.cancelStatus) === 5) {
    feedback.value = '该注销申请已驳回'
    return
  }

  try {
    const response = await updateClubCancelApprovalStatusApi(cancelId, {
      cancelStatus: 5,
      opinion: '学校管理员驳回社团注销申请',
    })
    getBizData(response)
    feedback.value = '社团注销申请已驳回'
    await loadClubCancelList()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '驳回社团注销申请失败'
  }
}

const handleNextCancelStepClick = (record) => {
  if (!canMoveToNextCancelStep(record?.cancelStatus)) {
    feedback.value = getNextCancelStepDisabledReason(record?.cancelStatus)
    return
  }
  goNextCancelStep(record)
}

const handleRejectCancelClick = (record) => {
  if (!canRejectCancelApply(record?.cancelStatus)) {
    feedback.value = getRejectCancelDisabledReason(record?.cancelStatus)
    return
  }
  rejectCancelApply(record)
}

const isUserFrozen = (user) => normalizeStatus(user?.status) === 'FROZEN'

const toggleUserStatus = async (user) => {
  const userId = user?.id
  if (!userId) return
  const shouldCancelDisable = isUserFrozen(user)

  try {
    const response = shouldCancelDisable ? await restoreUserApi(userId) : await disableUserApi(userId)
    getBizData(response)
    user.status = shouldCancelDisable ? 'ACTIVE' : 'FROZEN'
    feedback.value = shouldCancelDisable ? '已取消账号禁用' : '账号已禁用'
    await loadData()
  } catch (error) {
    feedback.value =
      error?.response?.data?.message || error?.message || (shouldCancelDisable ? '取消禁用失败' : '禁用失败')
  }
}

const toggleClubAdminRole = async (user) => {
  const userId = user?.id
  if (!userId) return

  const shouldCancelClubAdmin = isUserClubAdmin(user)
  const nextRoleCode = shouldCancelClubAdmin ? 'STUDENT' : 'CLUB_ADMIN'

  try {
    const response = await updateUserRoleApi(userId, { roleCode: nextRoleCode })
    getBizData(response)
    user.roleCode = nextRoleCode
    user.permissionType = nextRoleCode
    user.roleText = shouldCancelClubAdmin ? '学生' : '社团管理员'
    feedback.value = shouldCancelClubAdmin ? '已取消社团管理员角色' : '角色已调整为社团管理员'
    await loadData()
  } catch (error) {
    feedback.value =
      error?.response?.data?.message || error?.message || (shouldCancelClubAdmin ? '取消社团管理员失败' : '角色调整失败')
  }
}

const batchUpdateClubAdminRole = async (targetRoleCode) => {
  const selectedUsers = getSelectedUsers()
  if (selectedUsers.length === 0) {
    feedback.value = '请先选择至少一名用户'
    return
  }

  try {
    await Promise.all(
      selectedUsers.map((user) =>
        updateUserRoleApi(user.id, {
          roleCode: targetRoleCode,
        }),
      ),
    )

    feedback.value =
      targetRoleCode === 'CLUB_ADMIN'
        ? `已批量设为社团管理员（${selectedUsers.length}人）`
        : `已批量取消社团管理员（${selectedUsers.length}人）`
    await loadData()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '批量角色调整失败'
  }
}

const runGraduateAutoExit = async () => toggleScheduledTask('GRADUATION_EXIT_CLUB', '毕业生自动退社任务')

const runClubCancelFreeze = async () => toggleScheduledTask('CLUB_CANCEL_FREEZE_ACCOUNT', '社团注销账号冻结任务')

const toggleApprovalMenu = () => {
  approvalMenuExpanded.value = !approvalMenuExpanded.value
}

const openApprovalMenu = (menuKey) => {
  activeMenu.value = menuKey
  approvalMenuExpanded.value = true
}

const toggleFinanceMenu = () => {
  financeMenuExpanded.value = !financeMenuExpanded.value
}

const openFinanceMenu = (menuKey) => {
  activeMenu.value = menuKey
  financeMenuExpanded.value = true
  if (menuKey === 'expense-approval') loadExpenseApprovals()
  if (menuKey === 'anomaly-expenses') loadAnomalyExpenses()
  if (menuKey === 'finance-monitor' || menuKey === 'finance-report') loadClubListForSelect()
}

// -- Finance methods --
const loadExpenseApprovals = async () => {
  expenseApprovalLoading.value = true
  try {
    const params = { pageNum: expenseApprovalPage.value, pageSize: 20 }
    if (expenseApprovalKeyword.value.trim()) params.keyword = expenseApprovalKeyword.value.trim()
    const res = await getExpenseApprovalQueueApi(params)
    const data = getBizData(res)
    expenseApprovalList.value = Array.isArray(data?.records) ? data.records : []
    expenseApprovalTotal.value = data?.total || 0
  } catch (error) {
    feedback.value = error?.response?.data?.message || '经费审批列表加载失败'
  } finally { expenseApprovalLoading.value = false }
}

const loadAnomalyExpenses = async () => {
  anomalyExpenseLoading.value = true
  try {
    const params = { pageNum: anomalyExpensePage.value, pageSize: 20 }
    if (anomalyExpenseKeyword.value.trim()) params.keyword = anomalyExpenseKeyword.value.trim()
    const res = await getAnomalyExpensesApi(params)
    const data = getBizData(res)
    anomalyExpenseList.value = Array.isArray(data?.records) ? data.records : []
    anomalyExpenseTotal.value = data?.total || 0
  } catch (error) {
    feedback.value = error?.response?.data?.message || '异常经费列表加载失败'
  } finally { anomalyExpenseLoading.value = false }
}

const showExpenseDetail = async (expense) => {
  expenseDetailVisible.value = true
  expenseDetailLoading.value = true
  selectedExpenseDetail.value = { ...expense }
  expenseDecisionForm.action = ''
  expenseDecisionForm.rejectReason = ''
  try {
    const res = await getExpenseApprovalDetailApi(expense.id)
    selectedExpenseDetail.value = getBizData(res)
  } catch (error) {
    feedback.value = error?.response?.data?.message || '经费详情加载失败'
  } finally { expenseDetailLoading.value = false }
}

const closeExpenseDetail = () => {
  expenseDetailVisible.value = false
}

const openRejectDialog = () => {
  expenseDecisionForm.rejectReason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  const rejectReason = expenseDecisionForm.rejectReason.trim()
  if (!rejectReason) {
    feedback.value = '请输入驳回原因'
    return
  }
  expenseDecisionLoading.value = true
  try {
    await decideExpenseApprovalApi(selectedExpenseDetail.value.id, { action: 'REJECT', rejectReason })
    feedback.value = '经费申请已驳回'
    rejectDialogVisible.value = false
    expenseDetailVisible.value = false
    await loadExpenseApprovals()
  } catch (error) {
    feedback.value = error?.response?.data?.message || '驳回操作失败'
  } finally { expenseDecisionLoading.value = false }
}

const submitExpenseDecision = async (action) => {
  if (action === 'REJECT') {
    openRejectDialog()
    return
  }
  expenseDecisionLoading.value = true
  try {
    await decideExpenseApprovalApi(selectedExpenseDetail.value.id, { action, rejectReason: '' })
    feedback.value = '经费申请已通过'
    expenseDetailVisible.value = false
    await loadExpenseApprovals()
  } catch (error) {
    feedback.value = error?.response?.data?.message || '审批操作失败'
  } finally { expenseDecisionLoading.value = false }
}

const loadClubListForSelect = async () => {
  if (clubListForSelect.value.length > 0) return
  try {
    const res = await getSchoolClubManageListApi({ pageNum: 1, pageSize: 200 })
    const data = getBizData(res)
    clubListForSelect.value = Array.isArray(data?.records) ? data.records : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || '社团列表加载失败'
  }
}

const loadMonitorLedger = async () => {
  if (!monitorClubId.value) {
    feedback.value = '请先选择社团'
    return
  }
  monitorLoading.value = true
  try {
    const params = { pageNum: monitorLedgerPage.value, pageSize: 20 }
    if (monitorBizType.value) params.bizType = monitorBizType.value
    if (monitorStartTime.value) params.startTime = monitorStartTime.value
    if (monitorEndTime.value) params.endTime = monitorEndTime.value
    const res = await getClubLedgerApi(monitorClubId.value, params)
    const data = getBizData(res)
    monitorLedgerList.value = Array.isArray(data?.records) ? data.records : []
    monitorLedgerTotal.value = data?.total || 0
  } catch (error) {
    feedback.value = error?.response?.data?.message || '账本数据加载失败'
  } finally { monitorLoading.value = false }
}

const loadFinanceReport = async () => {
  if (!reportClubId.value) {
    feedback.value = '请先选择社团'
    return
  }
  reportLoading.value = true
  try {
    const res = await getClubFinanceReportApi(reportClubId.value, { year: reportYear.value })
    reportData.value = getBizData(res)
    await nextTick()
    renderFinanceCharts()
  } catch (error) {
    feedback.value = error?.response?.data?.message || '经费报告加载失败'
  } finally { reportLoading.value = false }
}

const renderFinanceCharts = () => {
  if (!reportData.value) return

  // Monthly bar chart
  if (monthlyChartRef.value) {
    if (!monthlyChart) monthlyChart = echarts.init(monthlyChartRef.value)
    const monthly = reportData.value.monthlyStats || []
    const months = monthly.map(m => m.month || `${m.monthIndex}月`)
    monthlyChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['收入', '支出'] },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value', name: '金额 (元)' },
      series: [
        { name: '收入', type: 'bar', data: monthly.map(m => m.income || 0), itemStyle: { color: '#16a34a' } },
        { name: '支出', type: 'bar', data: monthly.map(m => m.expense || 0), itemStyle: { color: '#ef4444' } },
      ],
    })
  }

  // Category pie chart
  if (categoryChartRef.value) {
    if (!categoryChart) categoryChart = echarts.init(categoryChartRef.value)
    const categories = reportData.value.categoryStats || []
    categoryChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} 元 ({d}%)' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        name: '支出分类',
        type: 'pie',
        radius: '60%',
        data: categories.map(c => ({ name: c.category || c.name, value: c.amount || c.value || 0 })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
      }],
    })
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadData()
  fetchTaskStatus()
  loadClubList()
  loadClubCancelList()
  loadManagedClubs()
  loadEventApprovals()
  loadEventSummaries()
  loadEventManageList()
})

// -- Statistics menu --
const toggleStatsMenu = () => { statsMenuExpanded.value = !statsMenuExpanded.value }
const openStatsMenu = (menuKey) => {
  activeMenu.value = menuKey
  statsMenuExpanded.value = true
  if (menuKey === 'stats-club') loadClubStats()
  if (menuKey === 'stats-event') loadEventStats()
  if (menuKey === 'stats-finance') loadFinanceStats()
}

const loadClubStats = async () => {
  clubStatsLoading.value = true
  try {
    const res = await getClubStatisticsApi({ year: statsYear.value })
    clubStatsData.value = getBizData(res)
    await nextTick()
    renderClubCharts()
  } catch (e) { feedback.value = e?.response?.data?.message || '社团统计加载失败' }
  finally { clubStatsLoading.value = false }
}

const loadEventStats = async () => {
  eventStatsLoading.value = true
  try {
    const res = await getEventStatisticsApi({ year: statsYear.value })
    eventStatsData.value = getBizData(res)
    await nextTick()
    renderEventCharts()
  } catch (e) { feedback.value = e?.response?.data?.message || '活动统计加载失败' }
  finally { eventStatsLoading.value = false }
}

const loadFinanceStats = async () => {
  financeStatsLoading.value = true
  try {
    const res = await getFinanceStatisticsApi({ year: statsYear.value })
    financeStatsData.value = getBizData(res)
    await nextTick()
    renderFinanceStatsCharts()
  } catch (e) { feedback.value = e?.response?.data?.message || '经费统计加载失败' }
  finally { financeStatsLoading.value = false }
}

const renderClubCharts = () => {
  const d = clubStatsData.value; if (!d) return
  if (clubCategoryChartRef.value) {
    if (!clubCategoryChart) clubCategoryChart = echarts.init(clubCategoryChartRef.value)
    clubCategoryChart.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' }, legend: { orient: 'vertical', left: 'left' }, series: [{ type: 'pie', radius: '60%', data: (d.categoryDistribution || []).map(i => ({ name: i.category, value: i.count })) }] })
  }
  if (clubStatusChartRef.value) {
    if (!clubStatusChart) clubStatusChart = echarts.init(clubStatusChartRef.value)
    const items = d.statusDistribution || []
    clubStatusChart.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: items.map(i => i.statusName) }, yAxis: { type: 'value' }, series: [{ type: 'bar', data: items.map(i => i.count), itemStyle: { color: '#6366f1' } }] })
  }
  if (clubMemberSizeChartRef.value) {
    if (!clubMemberSizeChart) clubMemberSizeChart = echarts.init(clubMemberSizeChartRef.value)
    clubMemberSizeChart.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' }, legend: { orient: 'vertical', left: 'left' }, series: [{ type: 'pie', radius: '60%', data: (d.memberSizeDistribution || []).map(i => ({ name: i.range, value: i.count })) }] })
  }
}

const renderEventCharts = () => {
  const d = eventStatsData.value; if (!d) return
  if (eventMonthlyChartRef.value) {
    if (!eventMonthlyChart) eventMonthlyChart = echarts.init(eventMonthlyChartRef.value)
    const m = d.monthlyEventStats || []
    eventMonthlyChart.setOption({ tooltip: { trigger: 'axis' }, legend: { data: ['活动数', '参与人次'] }, xAxis: { type: 'category', data: m.map(i => i.month + '月') }, yAxis: [{ type: 'value', name: '活动数' }, { type: 'value', name: '参与人次' }], series: [{ name: '活动数', type: 'bar', data: m.map(i => i.eventCount), itemStyle: { color: '#6366f1' } }, { name: '参与人次', type: 'line', yAxisIndex: 1, data: m.map(i => i.signupCount), itemStyle: { color: '#f59e0b' } }] })
  }
  if (eventStatusChartRef.value) {
    if (!eventStatusChart) eventStatusChart = echarts.init(eventStatusChartRef.value)
    eventStatusChart.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' }, legend: { orient: 'vertical', left: 'left' }, series: [{ type: 'pie', radius: '60%', data: (d.statusDistribution || []).map(i => ({ name: i.statusName, value: i.count })) }] })
  }
}

const renderFinanceStatsCharts = () => {
  const d = financeStatsData.value; if (!d) return
  if (financeMonthlyChartRef.value) {
    if (!financeMonthlyStatsChart) financeMonthlyStatsChart = echarts.init(financeMonthlyChartRef.value)
    const m = d.monthlyFinanceStats || []
    financeMonthlyStatsChart.setOption({ tooltip: { trigger: 'axis' }, legend: { data: ['收入', '支出'] }, xAxis: { type: 'category', data: m.map(i => i.month + '月') }, yAxis: { type: 'value', name: '金额 (元)' }, series: [{ name: '收入', type: 'bar', data: m.map(i => i.income || 0), itemStyle: { color: '#16a34a' } }, { name: '支出', type: 'bar', data: m.map(i => i.expense || 0), itemStyle: { color: '#ef4444' } }] })
  }
  if (financeIncomeTypeChartRef.value) {
    if (!financeIncomeTypeChart) financeIncomeTypeChart = echarts.init(financeIncomeTypeChartRef.value)
    financeIncomeTypeChart.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c} 元 ({d}%)' }, legend: { orient: 'vertical', left: 'left' }, series: [{ type: 'pie', radius: '60%', data: (d.incomeByType || []).map(i => ({ name: i.typeName, value: i.amount || 0 })) }] })
  }
}

// -- Excel export --
const exportToExcel = (rows, headers, filename) => {
  const ws = XLSX.utils.json_to_sheet(rows, { header: headers.map(h => h.key) })
  ws['!cols'] = headers.map(h => ({ wch: h.width || 16 }))
  const headerRow = {}
  headers.forEach((h, i) => { const cell = XLSX.utils.encode_cell({ c: i, r: 0 }); ws[cell].v = h.label })
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
  XLSX.writeFile(wb, filename)
}

const exportClubStats = () => {
  const d = clubStatsData.value; if (!d) return
  const rows = (d.topActiveClubs || []).map(c => ({ clubName: c.clubName, memberCount: c.memberCount, eventCount: c.eventCount }))
  exportToExcel(rows, [{ key: 'clubName', label: '社团名称', width: 20 }, { key: 'memberCount', label: '成员数', width: 10 }, { key: 'eventCount', label: '活动数', width: 10 }], `社团统计_${statsYear.value}.xlsx`)
}

const exportEventStats = () => {
  const d = eventStatsData.value; if (!d) return
  const rows = (d.topEventClubs || []).map(c => ({ clubName: c.clubName, eventCount: c.eventCount }))
  exportToExcel(rows, [{ key: 'clubName', label: '社团名称', width: 20 }, { key: 'eventCount', label: '活动数', width: 10 }], `活动统计_${statsYear.value}.xlsx`)
}

const exportFinanceStats = () => {
  const d = financeStatsData.value; if (!d) return
  const rows = (d.clubFinanceList || []).map(c => ({ clubName: c.clubName, income: c.income, expense: c.expense, balance: c.balance, eventCount: c.eventCount, efficiency: c.efficiency }))
  exportToExcel(rows, [{ key: 'clubName', label: '社团名称', width: 20 }, { key: 'income', label: '收入', width: 12 }, { key: 'expense', label: '支出', width: 12 }, { key: 'balance', label: '结余', width: 12 }, { key: 'eventCount', label: '活动数', width: 10 }, { key: 'efficiency', label: '效率(支出/活动)', width: 16 }], `经费统计_${statsYear.value}.xlsx`)
}

onBeforeUnmount(() => {
  if (monthlyChart) { monthlyChart.dispose(); monthlyChart = null }
  if (categoryChart) { categoryChart.dispose(); categoryChart = null }
  if (clubCategoryChart) { clubCategoryChart.dispose(); clubCategoryChart = null }
  if (clubStatusChart) { clubStatusChart.dispose(); clubStatusChart = null }
  if (clubMemberSizeChart) { clubMemberSizeChart.dispose(); clubMemberSizeChart = null }
  if (eventMonthlyChart) { eventMonthlyChart.dispose(); eventMonthlyChart = null }
  if (eventStatusChart) { eventStatusChart.dispose(); eventStatusChart = null }
  if (financeMonthlyStatsChart) { financeMonthlyStatsChart.dispose(); financeMonthlyStatsChart = null }
  if (financeIncomeTypeChart) { financeIncomeTypeChart.dispose(); financeIncomeTypeChart = null }
})
</script>

<template>
  <div class="admin-page">
    <div class="layout-shell">
      <aside class="left-menu">
        <div class="menu-brand">
          <h2>学校管理员</h2>
          <p class="menu-sub">治理与审批总控台</p>
        </div>

        <button type="button" class="menu-item" :class="{ active: activeMenu === 'stats' }" @click="activeMenu = 'stats'">
          数据总览
        </button>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'approvals' }" @click="activeMenu = 'approvals'">
          社团管理
        </button>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'users' }" @click="activeMenu = 'users'">
          用户管理
        </button>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'event-manage' }" @click="activeMenu = 'event-manage'">
          活动管理
        </button>

        <div class="menu-group">
          <button
            type="button"
            class="menu-group-btn"
            :class="{ 'group-active': ['clubs', 'club-cancel-approvals', 'event-approvals'].includes(activeMenu) }"
            @click="toggleApprovalMenu"
          >
            <span>审批管理</span>
            <span class="menu-arrow">{{ approvalMenuExpanded ? '▾' : '▸' }}</span>
          </button>
          <div v-show="approvalMenuExpanded" class="submenu">
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'clubs' }"
              @click="openApprovalMenu('clubs')"
            >社团申报审批</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'club-cancel-approvals' }"
              @click="openApprovalMenu('club-cancel-approvals')"
            >社团注销审核</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'event-approvals' }"
              @click="openApprovalMenu('event-approvals')"
            >活动审批</button>
          </div>
        </div>

        <div class="menu-group">
          <button
            type="button"
            class="menu-group-btn"
            :class="{ 'group-active': ['expense-approval', 'anomaly-expenses', 'finance-monitor', 'finance-report'].includes(activeMenu) }"
            @click="toggleFinanceMenu"
          >
            <span>经费管理</span>
            <span class="menu-arrow">{{ financeMenuExpanded ? '▾' : '▸' }}</span>
          </button>
          <div v-show="financeMenuExpanded" class="submenu">
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'expense-approval' }"
              @click="openFinanceMenu('expense-approval')"
            >经费审批</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'anomaly-expenses' }"
              @click="openFinanceMenu('anomaly-expenses')"
            >异常经费</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'finance-monitor' }"
              @click="openFinanceMenu('finance-monitor')"
            >经费监管</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'finance-report' }"
              @click="openFinanceMenu('finance-report')"
            >经费报告</button>
          </div>
        </div>

        <div class="menu-group">
          <button type="button" class="menu-group-btn"
            :class="{ 'group-active': ['stats-club', 'stats-event', 'stats-finance'].includes(activeMenu) }"
            @click="toggleStatsMenu">
            <span>数据统计</span>
            <span class="menu-arrow">{{ statsMenuExpanded ? '▾' : '▸' }}</span>
          </button>
          <div v-show="statsMenuExpanded" class="submenu">
            <button type="button" class="submenu-item" :class="{ active: activeMenu === 'stats-club' }" @click="openStatsMenu('stats-club')">社团统计</button>
            <button type="button" class="submenu-item" :class="{ active: activeMenu === 'stats-event' }" @click="openStatsMenu('stats-event')">活动统计</button>
            <button type="button" class="submenu-item" :class="{ active: activeMenu === 'stats-finance' }" @click="openStatsMenu('stats-finance')">经费统计</button>
          </div>
        </div>

        <button type="button" class="menu-item" :class="{ active: activeMenu === 'tasks' }" @click="activeMenu = 'tasks'">
          自动化任务
        </button>

        <div class="menu-divider"></div>
        <button type="button" class="menu-item ghost" :disabled="loading" @click="loadData">刷新数据</button>
        <button type="button" class="menu-item ghost" @click="fetchTaskStatus">刷新任务状态</button>
        <button type="button" class="menu-item logout" @click="handleLogout">退出登录</button>
      </aside>

      <main class="right-content">
        <header class="content-head">
          <p class="eyebrow">SCHOOL ADMIN CONSOLE</p>
          <h1>学校管理员中心</h1>
          <p class="sub">审批、账号治理、风险监控在一个工作台完成。</p>
        </header>

        <p v-if="feedback" class="message">{{ feedback }}</p>

        <section v-show="activeMenu === 'stats'" class="panel">
          <h3>数据总览</h3>
          <ul class="stats-grid">
            <li>
              <span>社团（正常/全部）</span>
              <strong>{{ stats.activeClubCount }}/{{ stats.totalClubCount }}</strong>
            </li>
            <li>
              <span>用户（活跃/全部）</span>
              <strong>{{ stats.activeUserCount }}/{{ stats.totalUserCount }}</strong>
            </li>
            <li>
              <span>待审批事项</span>
              <strong>{{ stats.pendingApprovalCount }}</strong>
            </li>
            <li>
              <span>异常经费笔数</span>
              <strong>{{ stats.suspiciousExpenseCount }}</strong>
            </li>
          </ul>
        </section>

        <section v-show="activeMenu === 'clubs'" class="panel">
          <div class="section-head">
            <h3>社团审批列表</h3>
            <div class="action-row">
              <button type="button" class="btn ghost" :disabled="clubLoading" @click="loadClubList">刷新列表</button>
            </div>
          </div>

          <div class="club-filter-row">
            <label>
              关键词
              <input v-model="clubFilters.keyword" type="text" placeholder="输入社团名称关键词" />
            </label>
            <label>
              审批状态
              <select v-model="clubFilters.applyStatus">
                <option value="">全部状态</option>
                <option value="1">待初审</option>
                <option value="2">答辩中</option>
                <option value="3">公示中</option>
                <option value="4">通过</option>
                <option value="5">驳回</option>
              </select>
            </label>
            <button type="button" class="btn" :disabled="clubLoading" @click="loadClubList">查询</button>
          </div>

          <div class="user-table-wrap">
            <div class="user-row user-head club-row">
              <div>社团名称</div>
              <div>申请状态</div>
              <div>申请人姓名</div>
              <div>操作</div>
            </div>
            <div v-for="club in clubs" :key="club.id" class="user-row club-row">
              <div>{{ club.clubName || '-' }}</div>
              <div>
                <span
                  :class="[
                    'apply-status-tag',
                    {
                      pending: Number(club.applyStatus) === 1,
                      approved: Number(club.applyStatus) === 4,
                      rejected: Number(club.applyStatus) === 5,
                      warning: ![1, 4, 5].includes(Number(club.applyStatus)),
                    },
                  ]"
                >
                  {{ getApplyStatusText(club.applyStatus) }}
                </span>
              </div>
              <div>{{ club.initiatorRealName || club.applicantRealName || club.realName || '-' }}</div>
              <div class="action-row">
                <button type="button" class="btn ghost" @click="openClubApplyDetail(club)">社团详情</button>
                <button
                  type="button"
                  :class="['btn', 'success', { 'btn-disabled': !canMoveToNextStep(club.applyStatus) }]"
                  :title="getNextStepDisabledReason(club.applyStatus)"
                  @click="handleNextStepClick(club)"
                >
                  进入下一流程
                </button>
                <button
                  type="button"
                  :class="['btn', 'danger', { 'btn-disabled': [4, 5].includes(Number(club.applyStatus)) }]"
                  :title="getRejectDisabledReason(club.applyStatus)"
                  @click="handleRejectClick(club)"
                >
                  驳回
                </button>
              </div>
            </div>
            <div v-if="clubs.length === 0" class="empty-text">暂无社团审批数据</div>
          </div>

          <div v-if="detailVisible" class="detail-mask" @click.self="closeClubApplyDetail">
            <section class="detail-dialog">
              <div class="detail-head">
                <h4>社团申报详情</h4>
                <button type="button" class="icon-close-btn" aria-label="关闭" @click="closeClubApplyDetail">×</button>
              </div>

              <div v-if="detailLoading" class="empty-text">详情加载中...</div>
              <div v-else class="detail-grid">
                <div>
                  <span>社团名称</span>
                  <strong>{{ clubApplyDetail?.clubName || '-' }}</strong>
                </div>
                <div>
                  <span>申请状态</span>
                  <strong>{{ getApplyStatusText(clubApplyDetail?.applyStatus) }}</strong>
                </div>
                <div>
                  <span>申报人</span>
                  <strong>{{ clubApplyDetail?.initiatorRealName || clubApplyDetail?.applicantRealName || '-' }}</strong>
                </div>
                <div>
                  <span>指导老师</span>
                  <strong>{{ clubApplyDetail?.instructorName || '-' }}</strong>
                </div>
                <div>
                  <span>社团分类</span>
                  <strong>{{ clubApplyDetail?.category || '-' }}</strong>
                </div>
                <div>
                  <span>提交时间</span>
                  <strong>{{ clubApplyDetail?.createdAt || '-' }}</strong>
                </div>
                <div class="full">
                  <span>社团宗旨</span>
                  <strong>{{ clubApplyDetail?.purpose || '-' }}</strong>
                </div>
                <div class="full">
                  <span>备注</span>
                  <strong>{{ clubApplyDetail?.remark || '-' }}</strong>
                </div>
                <div class="full action-row">
                  <a
                    class="btn"
                    :class="{ disabled: !(clubApplyDetail?.charterUrl || clubApplyDetail?.charterFileUrl) }"
                    :href="clubApplyDetail?.charterUrl || clubApplyDetail?.charterFileUrl || undefined"
                    target="_blank"
                    rel="noopener noreferrer"
                    download
                  >
                    下载章程文件
                  </a>
                  <a
                    class="btn"
                    :class="{ disabled: !(clubApplyDetail?.instructorProofUrl || clubApplyDetail?.instructorProofFileUrl) }"
                    :href="clubApplyDetail?.instructorProofUrl || clubApplyDetail?.instructorProofFileUrl || undefined"
                    target="_blank"
                    rel="noopener noreferrer"
                    download
                  >
                    下载指导教师证明
                  </a>
                </div>
              </div>
            </section>
          </div>
        </section>

        <section v-show="activeMenu === 'club-cancel-approvals'" class="panel">
          <div class="section-head">
            <h3>社团注销审核</h3>
            <div class="action-row">
              <button type="button" class="btn ghost" :disabled="cancelLoading" @click="loadClubCancelList">刷新列表</button>
            </div>
          </div>

          <div class="club-filter-row">
            <label>
              关键词
              <input v-model="cancelFilters.keyword" type="text" placeholder="输入社团名称关键词" />
            </label>
            <label>
              注销状态
              <select v-model="cancelFilters.cancelStatus">
                <option value="">全部状态</option>
                <option value="1">待公示</option>
                <option value="2">待经费结清</option>
                <option value="3">待资产移交</option>
                <option value="4">已完成</option>
                <option value="5">驳回</option>
              </select>
            </label>
            <button type="button" class="btn" :disabled="cancelLoading" @click="loadClubCancelList">查询</button>
          </div>

          <div class="user-table-wrap">
            <div class="user-row user-head club-row">
              <div>社团名称</div>
              <div>注销状态</div>
              <div>申请人姓名</div>
              <div>操作</div>
            </div>
            <div v-for="record in cancelClubs" :key="record.id" class="user-row club-row">
              <div>{{ record.clubName || '-' }}</div>
              <div>
                <span
                  :class="[
                    'apply-status-tag',
                    getCancelStatusClass(record.cancelStatus),
                  ]"
                >
                  {{ getCancelStatusText(record.cancelStatus) }}
                </span>
              </div>
              <div>{{ record.initiatorRealName || record.applicantRealName || record.realName || '-' }}</div>
              <div class="action-row">
                <button type="button" class="btn ghost" @click="openClubCancelDetail(record)">详情</button>
                <button
                  type="button"
                  :class="['btn', 'success', { 'btn-disabled': !canMoveToNextCancelStep(record.cancelStatus) }]"
                  :title="getNextCancelStepDisabledReason(record.cancelStatus)"
                  @click="handleNextCancelStepClick(record)"
                >
                  进入下一流程
                </button>
                <button
                  type="button"
                  :class="['btn', 'danger', { 'btn-disabled': !canRejectCancelApply(record.cancelStatus) }]"
                  :title="getRejectCancelDisabledReason(record.cancelStatus)"
                  @click="handleRejectCancelClick(record)"
                >
                  驳回
                </button>
              </div>
            </div>
            <div v-if="cancelClubs.length === 0" class="empty-text">暂无社团注销审核数据</div>
          </div>

          <div v-if="cancelDetailVisible" class="detail-mask" @click.self="closeClubCancelDetail">
            <section class="detail-dialog">
              <div class="detail-head">
                <h4>社团注销详情</h4>
                <button type="button" class="icon-close-btn" aria-label="关闭" @click="closeClubCancelDetail">×</button>
              </div>

              <div v-if="cancelDetailLoading" class="empty-text">详情加载中...</div>
              <div v-else class="detail-grid">
                <div>
                  <span>社团名称</span>
                  <strong>{{ cancelApplyDetail?.clubName || '-' }}</strong>
                </div>
                <div>
                  <span>注销状态</span>
                  <strong>{{ getCancelStatusText(cancelApplyDetail?.cancelStatus) }}</strong>
                </div>
                <div>
                  <span>申请类型</span>
                  <strong>{{ Number(cancelApplyDetail?.applyType) === 2 ? '强制注销' : '主动注销' }}</strong>
                </div>
                <div>
                  <span>申请人</span>
                  <strong>{{ cancelApplyDetail?.initiatorRealName || cancelApplyDetail?.applicantRealName || '-' }}</strong>
                </div>
                <div>
                  <span>提交时间</span>
                  <strong>{{ cancelApplyDetail?.createdAt || cancelApplyDetail?.submittedAt || '-' }}</strong>
                </div>
                <div class="full">
                  <span>注销原因</span>
                  <strong>{{ cancelApplyDetail?.applyReason || '-' }}</strong>
                </div>
                <div class="full action-row">
                  <a
                    class="btn"
                    :class="{ disabled: !(cancelApplyDetail?.assetSettlementUrl || cancelApplyDetail?.assetSettlementFileUrl) }"
                    :href="cancelApplyDetail?.assetSettlementUrl || cancelApplyDetail?.assetSettlementFileUrl || undefined"
                    target="_blank"
                    rel="noopener noreferrer"
                    download
                  >
                    下载资产清算报告
                  </a>
                </div>
              </div>
            </section>
          </div>
        </section>

        <section v-show="activeMenu === 'approvals'" class="panel">
          <div class="section-head">
            <h3>社团管理</h3>
            <button type="button" class="btn ghost" :disabled="manageLoading" @click="loadManagedClubs">刷新列表</button>
          </div>

          <div class="club-filter-row">
            <label>
              关键词
              <input v-model="manageFilters.keyword" type="text" placeholder="输入社团名称关键词" />
            </label>
            <label>
              分类
              <select v-model="manageFilters.category">
                <option value="">全部分类</option>
                <option v-for="category in manageCategoryOptions" :key="category" :value="category">{{ category }}</option>
              </select>
            </label>
            <button type="button" class="btn" :disabled="manageLoading" @click="loadManagedClubs">查询</button>
          </div>

          <div class="user-table-wrap">
            <div class="user-row user-head manage-row">
              <div>社团名称</div>
              <div>分类</div>
              <div>社团状态</div>
              <div>申报状态</div>
              <div>操作</div>
            </div>
            <div v-for="club in managedClubs" :key="club.clubId || club.id" class="user-row manage-row">
              <div>{{ club.clubName || '-' }}</div>
              <div>{{ club.category || '-' }}</div>
              <div>
                <span
                  :class="[
                    'club-status-tag',
                    {
                      normal: Number(club.status) === 2,
                      muted: Number(club.status) !== 2,
                    },
                  ]"
                >
                  {{ getClubStatusText(club.status) }}
                </span>
              </div>
              <div>
                <span
                  :class="[
                    'apply-status-tag',
                    {
                      pending: Number(club.applyStatus) === 1,
                      approved: Number(club.applyStatus) === 4,
                      rejected: Number(club.applyStatus) === 5,
                      warning: ![1, 4, 5].includes(Number(club.applyStatus)),
                    },
                  ]"
                >
                  {{ getApplyStatusText(club.applyStatus) }}
                </span>
              </div>
              <div class="action-row">
                <button type="button" class="btn ghost" @click="openManageDetail(club)">详情</button>
                <button
                  type="button"
                  :class="['btn', isClubFrozen(club) ? 'success' : 'ghost', { 'btn-disabled': !canToggleClubFreeze(club) }]"
                  :title="getToggleFreezeDisabledReason(club)"
                  @click="toggleManagedClubFreeze(club)"
                >
                  {{ getToggleFreezeText(club) }}
                </button>
                <button
                  type="button"
                  :class="['btn', { 'btn-disabled': !canEditClub(club) }]"
                  :title="getEditDisabledReason(club)"
                  @click="handleManageEditClick(club)"
                >
                  修改
                </button>
              </div>
            </div>
            <div v-if="managedClubs.length === 0" class="empty-text">暂无社团管理数据</div>
          </div>

          <div v-if="manageDetailVisible" class="detail-mask" @click.self="closeManageDetail">
            <section class="detail-dialog">
              <div class="detail-head">
                <h4>社团详情</h4>
                <button type="button" class="icon-close-btn" aria-label="关闭" @click="closeManageDetail">×</button>
              </div>

              <div v-if="manageDetailLoading" class="empty-text">详情加载中...</div>
              <div v-else class="detail-grid">
                <div>
                  <span>社团名称</span>
                  <strong>{{ manageDetail?.clubName || '-' }}</strong>
                </div>
                <div>
                  <span>社团分类</span>
                  <strong>{{ manageDetail?.category || '-' }}</strong>
                </div>
                <div>
                  <span>社团状态</span>
                  <strong>{{ getClubStatusText(manageDetail?.status) }}</strong>
                </div>
                <div>
                  <span>申报状态</span>
                  <strong>{{ getApplyStatusText(manageDetail?.applyStatus) }}</strong>
                </div>
                <div>
                  <span>申报人</span>
                  <strong>{{ manageDetail?.initiatorRealName || manageDetail?.applicantRealName || '-' }}</strong>
                </div>
                <div>
                  <span>指导老师</span>
                  <strong>{{ manageDetail?.instructorName || '-' }}</strong>
                </div>
                <div class="full">
                  <span>社团宗旨</span>
                  <strong>{{ manageDetail?.purpose || '-' }}</strong>
                </div>
                <div class="full action-row">
                  <a
                    class="btn"
                    :class="{ disabled: !manageDetail?.charterUrl }"
                    :href="manageDetail?.charterUrl || undefined"
                    target="_blank"
                    rel="noopener noreferrer"
                    download
                  >
                    下载章程文件
                  </a>
                  <a
                    class="btn"
                    :class="{ disabled: !manageDetail?.instructorProofUrl }"
                    :href="manageDetail?.instructorProofUrl || undefined"
                    target="_blank"
                    rel="noopener noreferrer"
                    download
                  >
                    下载指导教师证明
                  </a>
                </div>
              </div>
            </section>
          </div>

          <div v-if="manageEditVisible" class="detail-mask" @click.self="closeManageEdit">
            <section class="detail-dialog">
              <div class="detail-head">
                <h4>修改社团信息</h4>
                <button type="button" class="icon-close-btn" aria-label="关闭" @click="closeManageEdit">×</button>
              </div>

              <div class="apply-form manage-edit-form">
                <label>
                  社团名称
                  <input v-model="manageEditForm.clubName" type="text" placeholder="请输入社团名称" />
                </label>
                <label>
                  社团分类
                  <select v-model="manageEditForm.category">
                    <option value="">请选择分类</option>
                    <option v-for="category in manageCategoryOptions" :key="category" :value="category">{{ category }}</option>
                  </select>
                </label>
                <label>
                  指导老师
                  <input v-model="manageEditForm.instructorName" type="text" placeholder="请输入指导老师" />
                </label>
                <label class="full">
                  社团宗旨
                  <textarea v-model="manageEditForm.purpose" rows="3" placeholder="请输入社团宗旨" />
                </label>
                <div class="full action-row">
                  <button type="button" class="btn" :disabled="manageEditSubmitting" @click="submitManageEdit">
                    {{ manageEditSubmitting ? '保存中...' : '保存修改' }}
                  </button>
                  <button type="button" class="btn ghost" :disabled="manageEditSubmitting" @click="closeManageEdit">取消</button>
                </div>
              </div>
            </section>
          </div>
        </section>

        <section v-show="activeMenu === 'users'" class="panel">
          <div class="section-head">
            <h3>用户管理</h3>
            <button type="button" class="btn ghost" :disabled="loading" @click="loadData">刷新列表</button>
          </div>
          <div class="club-filter-row">
            <label>
              关键词
              <input v-model="userFilters.keyword" type="text" placeholder="输入姓名或账号关键词" />
            </label>
            <label>
              角色
              <select v-model="userFilters.roleCode">
                <option value="">全部角色</option>
                <option v-for="item in userRoleOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
            </label>
            <label>
              状态
              <select v-model="userFilters.status">
                <option value="">全部状态</option>
                <option v-for="item in userStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
            </label>
            <button type="button" class="btn" :disabled="loading" @click="loadData">查询</button>
          </div>
          <div class="user-toolbar">
            <label class="check-item">
              <input type="checkbox" :checked="isAllUsersSelected" @change="toggleSelectAllUsers" />
              <span>全选</span>
            </label>
            <span class="selected-tip">已选择 {{ selectedUserIds.length }} 人</span>
            <button
              type="button"
              class="btn"
              :disabled="!hasSelectedUsers"
              @click="batchUpdateClubAdminRole('CLUB_ADMIN')"
            >
              批量设为社团管理员
            </button>
            <button
              type="button"
              class="btn ghost"
              :disabled="!hasSelectedUsers"
              @click="batchUpdateClubAdminRole('STUDENT')"
            >
              批量取消社团管理员
            </button>
          </div>

          <div class="user-table-wrap">
            <div class="user-row user-head">
              <div class="col-check">选择</div>
              <div class="col-name">姓名 / 账号</div>
              <div class="col-role">角色</div>
              <div class="col-status">状态</div>
              <div class="col-action">操作</div>
            </div>

            <div v-for="user in users" :key="user.id" class="user-row">
              <div class="col-check">
                <input v-model="selectedUserIds" type="checkbox" :value="user.id" />
              </div>
              <div class="col-name">
                <strong>{{ user.realName || '-' }}</strong>
                <span>{{ user.username || '-' }}</span>
              </div>
              <div class="col-role">
                <span :class="['role-tag', getUserRoleTagClass(user.roleText)]">{{ user.roleText || '-' }}</span>
              </div>
              <div class="col-status">{{ getUserStatusText(user.status) }}</div>
              <div class="col-action action-row">
                <button
                  type="button"
                  class="btn"
                  :class="isUserClubAdmin(user) ? 'success' : 'ghost'"
                  @click="toggleClubAdminRole(user)"
                >
                  {{ isUserClubAdmin(user) ? '取消社团管理员' : '设为社团管理员' }}
                </button>
                <button
                  type="button"
                  class="btn"
                  :class="isUserFrozen(user) ? 'success' : 'ghost'"
                  @click="toggleUserStatus(user)"
                >
                  {{ isUserFrozen(user) ? '取消禁用账号' : '禁用账号' }}
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- 社团统计 -->
        <section v-show="activeMenu === 'stats-club'" class="panel">
          <div class="section-head">
            <h3>社团统计</h3>
            <div style="display:flex;align-items:center;gap:8px">
              <select v-model.number="statsYear" class="filter-select" style="width:100px">
                <option v-for="y in [2024,2025,2026]" :key="y" :value="y">{{ y }}年</option>
              </select>
              <button type="button" class="btn" :disabled="clubStatsLoading" @click="loadClubStats">{{ clubStatsLoading ? '加载中...' : '查询' }}</button>
              <button type="button" class="btn ghost" :disabled="!clubStatsData" @click="exportClubStats">导出Excel</button>
            </div>
          </div>
          <div v-if="clubStatsData" class="stats-content">
            <ul class="stats-grid">
              <li><span>年度新增</span><strong>{{ clubStatsData.yearlyNewCount }}</strong></li>
              <li><span>年度注销</span><strong>{{ clubStatsData.yearlyCancelCount }}</strong></li>
              <li><span>全校学生</span><strong>{{ clubStatsData.totalStudentCount }}</strong></li>
              <li><span>参与社团学生</span><strong>{{ clubStatsData.joinedStudentCount }}</strong></li>
              <li><span>成员覆盖率</span><strong class="income-color">{{ clubStatsData.coverageRate }}%</strong></li>
            </ul>
            <div class="charts-row">
              <div class="chart-box"><h4>社团类型分布</h4><div ref="clubCategoryChartRef" class="chart-container"></div></div>
              <div class="chart-box"><h4>社团状态分布</h4><div ref="clubStatusChartRef" class="chart-container"></div></div>
              <div class="chart-box"><h4>社团人数区间</h4><div ref="clubMemberSizeChartRef" class="chart-container"></div></div>
            </div>
            <h4 style="margin-top:18px">活跃社团 TOP10</h4>
            <div class="user-table-wrap">
              <div class="user-row user-head top-club-row"><div>社团名称</div><div>成员数</div><div>活动数</div></div>
              <div v-for="(c, i) in clubStatsData.topActiveClubs" :key="i" class="user-row top-club-row">
                <div>{{ c.clubName }}</div><div>{{ c.memberCount }}</div><div>{{ c.eventCount }}</div>
              </div>
              <div v-if="(clubStatsData.topActiveClubs || []).length === 0" class="empty-text">暂无数据</div>
            </div>
          </div>
          <div v-else class="empty-text">请选择年份后点击查询</div>
        </section>

        <!-- 活动统计 -->
        <section v-show="activeMenu === 'stats-event'" class="panel">
          <div class="section-head">
            <h3>活动统计</h3>
            <div style="display:flex;align-items:center;gap:8px">
              <select v-model.number="statsYear" class="filter-select" style="width:100px">
                <option v-for="y in [2024,2025,2026]" :key="y" :value="y">{{ y }}年</option>
              </select>
              <button type="button" class="btn" :disabled="eventStatsLoading" @click="loadEventStats">{{ eventStatsLoading ? '加载中...' : '查询' }}</button>
              <button type="button" class="btn ghost" :disabled="!eventStatsData" @click="exportEventStats">导出Excel</button>
            </div>
          </div>
          <div v-if="eventStatsData" class="stats-content">
            <ul class="stats-grid">
              <li><span>年度活动总数</span><strong>{{ eventStatsData.totalEventCount }}</strong></li>
              <li><span>总参与人次</span><strong>{{ eventStatsData.totalSignupCount }}</strong></li>
            </ul>
            <div class="charts-row">
              <div class="chart-box" style="flex:2"><h4>月度活动数与参与人次</h4><div ref="eventMonthlyChartRef" class="chart-container"></div></div>
              <div class="chart-box"><h4>活动状态分布</h4><div ref="eventStatusChartRef" class="chart-container"></div></div>
            </div>
            <h4 style="margin-top:18px">活动最多社团 TOP10</h4>
            <div class="user-table-wrap">
              <div class="user-row user-head top-club-row"><div>社团名称</div><div>活动数</div></div>
              <div v-for="(c, i) in eventStatsData.topEventClubs" :key="i" class="user-row top-club-row">
                <div>{{ c.clubName }}</div><div>{{ c.eventCount }}</div>
              </div>
              <div v-if="(eventStatsData.topEventClubs || []).length === 0" class="empty-text">暂无数据</div>
            </div>
          </div>
          <div v-else class="empty-text">请选择年份后点击查询</div>
        </section>

        <!-- 经费统计 -->
        <section v-show="activeMenu === 'stats-finance'" class="panel">
          <div class="section-head">
            <h3>经费统计</h3>
            <div style="display:flex;align-items:center;gap:8px">
              <select v-model.number="statsYear" class="filter-select" style="width:100px">
                <option v-for="y in [2024,2025,2026]" :key="y" :value="y">{{ y }}年</option>
              </select>
              <button type="button" class="btn" :disabled="financeStatsLoading" @click="loadFinanceStats">{{ financeStatsLoading ? '加载中...' : '查询' }}</button>
              <button type="button" class="btn ghost" :disabled="!financeStatsData" @click="exportFinanceStats">导出Excel</button>
            </div>
          </div>
          <div v-if="financeStatsData" class="stats-content">
            <ul class="stats-grid">
              <li><span>学校总拨款</span><strong class="income-color">{{ financeStatsData.totalSchoolFunding }} 元</strong></li>
              <li><span>总收入</span><strong class="income-color">{{ financeStatsData.totalIncome }} 元</strong></li>
              <li><span>总支出</span><strong class="expense-color">{{ financeStatsData.totalExpense }} 元</strong></li>
              <li><span>总结余</span><strong>{{ financeStatsData.totalBalance }} 元</strong></li>
            </ul>
            <div class="charts-row">
              <div class="chart-box" style="flex:2"><h4>月度收支趋势</h4><div ref="financeMonthlyChartRef" class="chart-container"></div></div>
              <div class="chart-box"><h4>收入类型占比</h4><div ref="financeIncomeTypeChartRef" class="chart-container"></div></div>
            </div>
            <h4 style="margin-top:18px">各社团经费使用效率</h4>
            <div class="user-table-wrap">
              <div class="user-row user-head finance-summary-row"><div>社团</div><div>收入</div><div>支出</div><div>结余</div><div>活动数</div><div>效率(支出/活动)</div></div>
              <div v-for="(c, i) in financeStatsData.clubFinanceList" :key="i" class="user-row finance-summary-row">
                <div>{{ c.clubName }}</div>
                <div class="income-color">{{ c.income }}</div>
                <div class="expense-color">{{ c.expense }}</div>
                <div>{{ c.balance }}</div>
                <div>{{ c.eventCount }}</div>
                <div>{{ c.efficiency }}</div>
              </div>
              <div v-if="(financeStatsData.clubFinanceList || []).length === 0" class="empty-text">暂无数据</div>
            </div>
          </div>
          <div v-else class="empty-text">请选择年份后点击查询</div>
        </section>

        <section v-show="activeMenu === 'tasks'" class="panel">
          <h3>自动化状态任务</h3>
          <div class="action-row">
            <button
              type="button"
              class="btn task-btn"
              :class="isTaskActive('GRADUATION_EXIT_CLUB') ? 'task-done' : 'task-pending'"
              :disabled="taskLoading.GRADUATION_EXIT_CLUB"
              @click="runGraduateAutoExit"
            >
              执行毕业生自动退社
            </button>
            <button
              type="button"
              class="btn task-btn"
              :class="isTaskActive('CLUB_CANCEL_FREEZE_ACCOUNT') ? 'task-done' : 'task-pending'"
              :disabled="taskLoading.CLUB_CANCEL_FREEZE_ACCOUNT"
              @click="runClubCancelFreeze"
            >
              执行社团注销账号冻结
            </button>
          </div>
        </section>

        <section v-show="activeMenu === 'event-approvals'" class="panel">
          <div class="section-head">
            <h3>活动审批</h3>
            <button type="button" class="btn ghost" :disabled="eventApprovalLoading" @click="loadEventApprovals">
              {{ eventApprovalLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <div class="search-bar">
            <input
              v-model="eventApprovalSearch.keyword"
              type="text"
              placeholder="搜索活动标题或社团名称..."
              class="search-input"
              @keyup.enter="loadEventApprovals"
            />
            <select v-model="eventApprovalSearch.eventStatus" class="filter-select" @change="loadEventApprovals">
              <option :value="null">全部状态</option>
              <option :value="2">待审批</option>
              <option :value="3">已通过</option>
              <option :value="5">已驳回</option>
            </select>
            <button type="button" class="btn" @click="loadEventApprovals">搜索</button>
          </div>
          <div class="user-table-wrap">
            <div class="user-row user-head event-row">
              <div>社团</div>
              <div>活动标题</div>
              <div>活动时间</div>
              <div>人数上限</div>
              <div>状态</div>
              <div>提交时间</div>
              <div>操作</div>
            </div>
            <div v-for="ev in eventApprovalList" :key="ev.id" class="user-row event-row">
              <div>{{ ev.clubName }}</div>
              <div>{{ ev.title }}</div>
              <div>{{ ev.startAt?.slice(0,16)?.replace('T',' ') || '-' }}</div>
              <div>{{ ev.limitCount || '不限' }}</div>
              <div>
                <span v-if="ev.eventStatus === 2" class="status-badge pending">待审批</span>
                <span v-else-if="ev.eventStatus === 3" class="status-badge approved">已通过</span>
                <span v-else-if="ev.eventStatus === 6" class="status-badge approved">进行中</span>
                <span v-else-if="ev.eventStatus === 4" class="status-badge approved">已结束</span>
                <span v-else-if="ev.eventStatus === 5" class="status-badge rejected">已驳回</span>
                <span v-else class="status-badge">-</span>
              </div>
              <div>{{ ev.createdAt?.slice(0,16)?.replace('T',' ') || '-' }}</div>
              <div>
                <button type="button" class="btn ghost sm" @click="openEventApprovalDetail(ev)">
                  {{ ev.eventStatus === 2 ? '审批' : '查看' }}
                </button>
              </div>
            </div>
            <div v-if="eventApprovalList.length === 0" class="empty-text">暂无活动记录</div>
          </div>
        </section>

        <section v-show="activeMenu === 'event-manage'" class="panel">
          <div class="section-head">
            <h3>活动管理</h3>
            <button type="button" class="btn ghost" :disabled="eventManageLoading" @click="loadEventManageList">
              {{ eventManageLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <div class="search-bar">
            <input
              v-model="eventManageSearch.keyword"
              type="text"
              placeholder="搜索活动标题或社团名称..."
              class="search-input"
              @keyup.enter="loadEventManageList"
            />
            <select v-model="eventManageSearch.eventStatus" class="filter-select" @change="loadEventManageList">
              <option :value="null">全部状态</option>
              <option :value="2">待审核</option>
              <option :value="3">报名中</option>
              <option :value="6">进行中</option>
              <option :value="4">已结束</option>
              <option :value="5">已驳回</option>
            </select>
            <button type="button" class="btn" @click="loadEventManageList">搜索</button>
          </div>
          <div class="user-table-wrap">
            <div class="user-row user-head event-manage-row">
              <div>社团</div>
              <div>活动标题</div>
              <div>状态</div>
              <div>操作</div>
            </div>
            <div v-for="ev in eventManageList" :key="ev.id" class="user-row event-manage-row">
              <div>{{ ev.clubName }}</div>
              <div>{{ ev.title }}</div>
              <div>
                <span :class="['status-badge', getEventStatusClass(ev.eventStatus)]">{{ getEventStatusText(ev.eventStatus) }}</span>
              </div>
              <div class="action-row">
                <button
                  type="button"
                  :class="['btn', 'ghost', 'sm', { 'btn-disabled': ![3, 6, 4].includes(ev.eventStatus) }]"
                  :title="![3, 6, 4].includes(ev.eventStatus) ? '活动审批未通过，无法查看详情' : ''"
                  @click="[3, 6, 4].includes(ev.eventStatus) && openEventManageDetail(ev)"
                >详情</button>
                <button
                  type="button"
                  :class="['btn', 'ghost', 'sm', { 'btn-disabled': ev.eventStatus !== 4 }]"
                  :title="ev.eventStatus !== 4 ? '活动未结束，暂无总结' : ''"
                  @click="ev.eventStatus === 4 && openEventManageSummary(ev)"
                >活动总结</button>
              </div>
            </div>
            <div v-if="eventManageList.length === 0" class="empty-text">暂无活动数据</div>
          </div>
        </section>

        <section v-show="activeMenu === 'event-summaries'" class="panel">
          <div class="section-head">
            <h3>活动总结</h3>
            <button type="button" class="btn ghost" :disabled="eventSummaryLoading" @click="loadEventSummaries">
              {{ eventSummaryLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <div class="user-table-wrap">
            <div class="user-row user-head summary-row">
              <div>社团</div>
              <div>活动标题</div>
              <div>评分</div>
              <div>提交时间</div>
              <div>操作</div>
            </div>
            <div v-for="s in eventSummaryList" :key="s.id" class="user-row summary-row">
              <div>{{ s.clubName }}</div>
              <div>{{ s.eventTitle }}</div>
              <div>{{ s.feedbackScore ?? '-' }}</div>
              <div>{{ s.createdAt?.slice(0,16)?.replace('T',' ') || '-' }}</div>
              <div><button type="button" class="btn ghost sm" @click="openEventSummaryView(s)">查看</button></div>
            </div>
            <div v-if="eventSummaryList.length === 0" class="empty-text">暂无活动总结</div>
          </div>
        </section>

        <!-- 经费审批 -->
        <section v-show="activeMenu === 'expense-approval'" class="panel">
          <div class="section-head">
            <h3>经费审批</h3>
            <button type="button" class="btn ghost" :disabled="expenseApprovalLoading" @click="loadExpenseApprovals">
              {{ expenseApprovalLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <div class="search-bar">
            <input
              v-model="expenseApprovalKeyword"
              type="text"
              placeholder="搜索社团名称或描述..."
              class="search-input"
              @keyup.enter="loadExpenseApprovals"
            />
            <button type="button" class="btn" @click="loadExpenseApprovals">搜索</button>
          </div>
          <div class="user-table-wrap">
            <div class="user-row user-head expense-row">
              <div>社团名称</div>
              <div>金额</div>
              <div>分类</div>
              <div>操作</div>
            </div>
            <div v-for="exp in expenseApprovalList" :key="exp.id" class="user-row expense-row">
              <div>{{ exp.clubName || '-' }}</div>
              <div style="color:#dc2626;font-weight:600">{{ exp.amount != null ? `${exp.amount} 元` : '-' }}</div>
              <div>{{ exp.category || '-' }}</div>
              <div>
                <button type="button" class="btn ghost sm" @click="showExpenseDetail(exp)">审批</button>
              </div>
            </div>
            <div v-if="expenseApprovalList.length === 0" class="empty-text">暂无待审批经费</div>
          </div>

          <!-- Expense detail dialog -->
          <div v-if="expenseDetailVisible" class="detail-mask" @click.self="closeExpenseDetail">
            <section class="detail-dialog">
              <div class="detail-head">
                <h4>经费审批详情</h4>
                <button type="button" class="icon-close-btn" aria-label="关闭" @click="closeExpenseDetail">×</button>
              </div>
              <div v-if="expenseDetailLoading" class="empty-text">详情加载中...</div>
              <div v-else class="detail-grid">
                <div>
                  <span>社团名称</span>
                  <strong>{{ selectedExpenseDetail?.clubName || '-' }}</strong>
                </div>
                <div>
                  <span>金额</span>
                  <strong>{{ selectedExpenseDetail?.amount != null ? `${selectedExpenseDetail.amount} 元` : '-' }}</strong>
                </div>
                <div>
                  <span>分类</span>
                  <strong>{{ selectedExpenseDetail?.category || '-' }}</strong>
                </div>
                <div>
                  <span>申请人</span>
                  <strong>{{ selectedExpenseDetail?.createdByName || '-' }}</strong>
                </div>
                <div>
                  <span>提交时间</span>
                  <strong>{{ selectedExpenseDetail?.createdAt?.slice(0,16)?.replace('T',' ') || '-' }}</strong>
                </div>
                <div>
                  <span>状态</span>
                  <strong>
                    <span v-if="selectedExpenseDetail?.expenseStatus === 1" class="status-badge pending">待审批</span>
                    <span v-else-if="selectedExpenseDetail?.expenseStatus === 2" class="status-badge approved">已通过</span>
                    <span v-else-if="selectedExpenseDetail?.expenseStatus === 3" class="status-badge rejected">已驳回</span>
                    <span v-else>-</span>
                  </strong>
                </div>
                <div class="full">
                  <span>描述</span>
                  <strong>{{ selectedExpenseDetail?.expenseDesc || '-' }}</strong>
                </div>
                <div v-if="selectedExpenseDetail?.invoiceUrl" class="full">
                  <span>发票附件</span>
                  <a :href="selectedExpenseDetail.invoiceUrl" target="_blank" rel="noopener noreferrer" class="link-text">查看发票</a>
                </div>
                <div class="full action-row">
                  <template v-if="selectedExpenseDetail?.expenseStatus === 1">
                    <button type="button" class="btn approve-btn" :disabled="expenseDecisionLoading" @click="submitExpenseDecision('APPROVE')">通过</button>
                    <button type="button" class="btn reject-btn" :disabled="expenseDecisionLoading" @click="submitExpenseDecision('REJECT')">驳回</button>
                  </template>
                  <button type="button" class="btn close-btn" @click="closeExpenseDetail">关闭</button>
                </div>
              </div>
            </section>
          </div>

          <!-- 驳回理由弹窗 -->
          <div v-if="rejectDialogVisible" class="detail-mask" @click.self="rejectDialogVisible = false">
            <section class="detail-dialog" style="max-width:460px">
              <div class="detail-head">
                <h4>驳回经费申请</h4>
                <button type="button" class="icon-close-btn" aria-label="关闭" @click="rejectDialogVisible = false">×</button>
              </div>
              <div style="padding:16px 20px">
                <p style="margin-bottom:10px;color:#64748b;font-size:13px">
                  请输入驳回原因（必填），将通知社团管理员。
                </p>
                <textarea
                  v-model="expenseDecisionForm.rejectReason"
                  rows="3"
                  placeholder="请输入驳回原因..."
                  class="form-textarea"
                  style="width:100%;resize:vertical"
                />
                <div class="action-row" style="margin-top:14px;justify-content:flex-end">
                  <button type="button" class="btn ghost" @click="rejectDialogVisible = false">取消</button>
                  <button
                    type="button"
                    class="btn reject-btn"
                    :disabled="expenseDecisionLoading || !expenseDecisionForm.rejectReason.trim()"
                    @click="confirmReject"
                  >
                    {{ expenseDecisionLoading ? '提交中...' : '确认驳回' }}
                  </button>
                </div>
              </div>
            </section>
          </div>
        </section>

        <!-- 异常经费 -->
        <section v-show="activeMenu === 'anomaly-expenses'" class="panel">
          <div class="section-head">
            <h3>异常经费</h3>
            <button type="button" class="btn ghost" :disabled="anomalyExpenseLoading" @click="loadAnomalyExpenses">
              {{ anomalyExpenseLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <p class="hint-text" style="margin-bottom:12px;color:#64748b;font-size:13px">
            异常判定规则：金额超过500元但未经学校审批，或缺少发票凭证。
          </p>
          <div class="search-bar">
            <input
              v-model="anomalyExpenseKeyword"
              type="text"
              placeholder="搜索社团名称或描述..."
              class="search-input"
              @keyup.enter="anomalyExpensePage = 1; loadAnomalyExpenses()"
            />
            <button type="button" class="btn" @click="anomalyExpensePage = 1; loadAnomalyExpenses()">搜索</button>
          </div>
          <div class="user-table-wrap">
            <div class="user-row user-head anomaly-row">
              <div>社团</div>
              <div>金额</div>
              <div>分类</div>
              <div>描述</div>
              <div>异常原因</div>
              <div>审批级别</div>
              <div>状态</div>
              <div>发票</div>
              <div>提交时间</div>
            </div>
            <div v-for="item in anomalyExpenseList" :key="item.id" class="user-row anomaly-row">
              <div>{{ item.clubName || '-' }}</div>
              <div style="color:#dc2626;font-weight:600">{{ item.amount != null ? `${item.amount} 元` : '-' }}</div>
              <div>{{ item.category || '-' }}</div>
              <div class="text-ellipsis">{{ item.expenseDesc || '-' }}</div>
              <div>
                <span class="status-badge anomaly">{{ item.anomalyReason || '未知' }}</span>
              </div>
              <div>{{ item.approveLevel === 1 ? '社团自审' : '学校审核' }}</div>
              <div>
                <span :class="['status-badge', item.expenseStatus === 2 ? 'approved' : item.expenseStatus === 3 ? 'rejected' : 'pending']">
                  {{ item.expenseStatus === 2 ? '已通过' : item.expenseStatus === 3 ? '已驳回' : item.expenseStatus === 4 ? '已支付' : '待审核' }}
                </span>
              </div>
              <div>
                <a v-if="item.invoiceUrl" :href="item.invoiceUrl" target="_blank" rel="noopener noreferrer" class="link-text">查看</a>
                <span v-else style="color:#dc2626">缺失</span>
              </div>
              <div>{{ item.createdAt?.slice(0,16)?.replace('T',' ') || '-' }}</div>
            </div>
            <div v-if="anomalyExpenseList.length === 0" class="empty-text">暂无异常经费记录</div>
          </div>
          <div v-if="anomalyExpenseTotal > 20" class="pagination-bar">
            <button type="button" class="btn ghost sm" :disabled="anomalyExpensePage <= 1" @click="anomalyExpensePage--; loadAnomalyExpenses()">上一页</button>
            <span class="page-info">第 {{ anomalyExpensePage }} / {{ Math.ceil(anomalyExpenseTotal / 20) }} 页（共 {{ anomalyExpenseTotal }} 条）</span>
            <button type="button" class="btn ghost sm" :disabled="anomalyExpensePage >= Math.ceil(anomalyExpenseTotal / 20)" @click="anomalyExpensePage++; loadAnomalyExpenses()">下一页</button>
          </div>
        </section>

        <!-- 经费监管 -->
        <section v-show="activeMenu === 'finance-monitor'" class="panel">
          <div class="section-head">
            <h3>经费监管</h3>
            <button type="button" class="btn ghost" :disabled="monitorLoading" @click="loadMonitorLedger">
              {{ monitorLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <div class="club-filter-row">
            <label>
              选择社团
              <select v-model="monitorClubId" @change="monitorLedgerPage = 1; loadMonitorLedger()">
                <option :value="null">请选择社团</option>
                <option v-for="c in clubListForSelect" :key="c.clubId || c.id" :value="c.clubId || c.id">{{ c.clubName }}</option>
              </select>
            </label>
            <label>
              业务类型
              <select v-model="monitorBizType">
                <option value="">全部</option>
                <option value="INCOME">收入</option>
                <option value="EXPENSE">支出</option>
              </select>
            </label>
            <label>
              开始日期
              <input v-model="monitorStartTime" type="date" />
            </label>
            <button type="button" class="btn" :disabled="monitorLoading || !monitorClubId" @click="loadMonitorLedger">查询</button>
          </div>
          <div class="club-filter-row" style="margin-top:-6px;">
            <label>
              结束日期
              <input v-model="monitorEndTime" type="date" />
            </label>
          </div>
          <div class="user-table-wrap">
            <div class="user-row user-head ledger-row">
              <div>时间</div>
              <div>类型</div>
              <div>变动金额</div>
              <div>变动后余额</div>
            </div>
            <div v-for="item in monitorLedgerList" :key="item.id" class="user-row ledger-row">
              <div>{{ item.occurAt ? item.occurAt.slice(0,16).replace('T',' ') : '-' }}</div>
              <div>
                <span :class="['status-badge', item.bizType === 1 ? 'approved' : 'rejected']">
                  {{ item.bizTypeName || (item.bizType === 1 ? '收入' : '支出') }}
                </span>
              </div>
              <div :style="{ color: Number(item.changeAmount) >= 0 ? '#059669' : '#dc2626', fontWeight: 600 }">
                {{ Number(item.changeAmount) >= 0 ? '+' : '' }}{{ Number(item.changeAmount || 0).toFixed(2) }} 元
              </div>
              <div style="font-weight:500">{{ Number(item.balanceAfter || 0).toFixed(2) }} 元</div>
            </div>
            <div v-if="monitorLedgerList.length === 0" class="empty-text">{{ monitorClubId ? '暂无账本数据' : '请先选择社团' }}</div>
          </div>
        </section>

        <!-- 经费报告 -->
        <section v-show="activeMenu === 'finance-report'" class="panel">
          <div class="section-head">
            <h3>经费报告</h3>
            <button type="button" class="btn ghost" :disabled="reportLoading" @click="loadFinanceReport">
              {{ reportLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <div class="club-filter-row">
            <label>
              选择社团
              <select v-model="reportClubId">
                <option :value="null">请选择社团</option>
                <option v-for="c in clubListForSelect" :key="c.clubId || c.id" :value="c.clubId || c.id">{{ c.clubName }}</option>
              </select>
            </label>
            <label>
              年份
              <select v-model="reportYear">
                <option v-for="y in [2026, 2025, 2024, 2023]" :key="y" :value="y">{{ y }}</option>
              </select>
            </label>
            <button type="button" class="btn" :disabled="reportLoading || !reportClubId" @click="loadFinanceReport">查询</button>
          </div>

          <div v-if="reportData" class="report-content">
            <ul class="stats-grid">
              <li>
                <span>总收入</span>
                <strong class="income-color">{{ reportData.totalIncome ?? 0 }} 元</strong>
              </li>
              <li>
                <span>总支出</span>
                <strong class="expense-color">{{ reportData.totalExpense ?? 0 }} 元</strong>
              </li>
              <li>
                <span>余额</span>
                <strong>{{ reportData.balance ?? 0 }} 元</strong>
              </li>
              <li>
                <span>异常笔数</span>
                <strong class="anomaly-color">{{ reportData.anomalyCount ?? 0 }}</strong>
              </li>
            </ul>

            <div class="chart-row">
              <div class="chart-box">
                <h4>月度收支对比</h4>
                <div ref="monthlyChartRef" class="chart-container"></div>
              </div>
              <div class="chart-box">
                <h4>支出分类占比</h4>
                <div ref="categoryChartRef" class="chart-container"></div>
              </div>
            </div>
          </div>
          <div v-else class="empty-text">请选择社团和年份后查询</div>
        </section>
      </main>
    </div>
  </div>

  <div v-if="eventApprovalDetailDialog.visible" class="detail-mask" @click.self="eventApprovalDetailDialog.visible = false">
    <section class="detail-dialog">
      <div class="detail-head">
        <h4>活动审批详情</h4>
        <button type="button" class="icon-close-btn" aria-label="关闭" @click="eventApprovalDetailDialog.visible = false">×</button>
      </div>
      <div class="detail-grid">
        <div>
          <span>社团</span>
          <strong>{{ eventApprovalDetailDialog.event?.clubName }}</strong>
        </div>
        <div>
          <span>标题</span>
          <strong>{{ eventApprovalDetailDialog.event?.title }}</strong>
        </div>
        <div>
          <span>地点</span>
          <strong>{{ eventApprovalDetailDialog.event?.location }}</strong>
        </div>
        <div>
          <span>活动时间</span>
          <strong>{{ eventApprovalDetailDialog.event?.startAt?.slice(0,16)?.replace('T',' ') }} 至 {{ eventApprovalDetailDialog.event?.endAt?.slice(0,16)?.replace('T',' ') }}</strong>
        </div>
        <div>
          <span>报名时间</span>
          <strong>{{ eventApprovalDetailDialog.event?.signupStartAt?.slice(0,16)?.replace('T',' ') || '-' }} 至 {{ eventApprovalDetailDialog.event?.signupEndAt?.slice(0,16)?.replace('T',' ') || '-' }}</strong>
        </div>
        <div>
          <span>人数上限</span>
          <strong>{{ eventApprovalDetailDialog.event?.limitCount || '不限' }}</strong>
        </div>
        <div>
          <span>仅成员</span>
          <strong>{{ eventApprovalDetailDialog.event?.onlyMember ? '是' : '否' }}</strong>
        </div>
        <div v-if="eventApprovalDetailDialog.event?.content" class="full">
          <span>活动内容</span>
          <strong>{{ eventApprovalDetailDialog.event.content }}</strong>
        </div>
        <div v-if="eventApprovalDetailDialog.event?.safetyPlanUrl" class="full">
          <span>安全预案</span>
          <a :href="eventApprovalDetailDialog.event.safetyPlanUrl" target="_blank">下载查看</a>
        </div>
        <div class="full action-row">
          <template v-if="eventApprovalDetailDialog.event?.eventStatus === 2">
            <button type="button" class="btn approve-btn" :disabled="eventDecisionLoading" @click="handleEventDecision('APPROVE')">通过</button>
            <button type="button" class="btn reject-btn" :disabled="eventDecisionLoading" @click="handleEventDecision('REJECT')">驳回</button>
          </template>
          <button type="button" class="btn close-btn" @click="eventApprovalDetailDialog.visible = false">关闭</button>
        </div>
      </div>
    </section>
  </div>

  <div v-if="eventSummaryViewDialog.visible" class="detail-mask" @click.self="eventSummaryViewDialog.visible = false">
    <section class="detail-dialog">
      <div class="detail-head">
        <h4>活动总结详情</h4>
        <button type="button" class="icon-close-btn" aria-label="关闭" @click="eventSummaryViewDialog.visible = false">×</button>
      </div>
      <div class="detail-grid">
        <div>
          <span>社团</span>
          <strong>{{ eventSummaryViewDialog.summary?.clubName }}</strong>
        </div>
        <div>
          <span>活动</span>
          <strong>{{ eventSummaryViewDialog.summary?.eventTitle }}</strong>
        </div>
        <div>
          <span>评分</span>
          <strong>{{ eventSummaryViewDialog.summary?.feedbackScore ?? '-' }}</strong>
        </div>
        <div class="full">
          <span>总结内容</span>
          <strong>{{ eventSummaryViewDialog.summary?.summaryText }}</strong>
        </div>
        <div v-if="eventSummaryViewDialog.summary?.summaryImages && eventSummaryViewDialog.summary.summaryImages.length > 0" class="full">
          <span>活动图片（{{ eventSummaryViewDialog.summary.summaryImages.length }} 张）</span>
          <div style="display:flex;flex-wrap:wrap;gap:8px;margin-top:8px;max-height:300px;overflow-y:auto;padding:4px;border:1px solid #e5e7eb;border-radius:4px">
            <img v-for="(img, idx) in eventSummaryViewDialog.summary.summaryImages" :key="idx" :src="img" style="width:120px;height:120px;object-fit:cover;border-radius:4px;cursor:pointer;border:2px solid #e5e7eb" @click="window.open(img, '_blank')" alt="活动图片" />
          </div>
        </div>
        <div v-if="eventSummaryViewDialog.summary?.issueReflection" class="full">
          <span>问题反思</span>
          <strong>{{ eventSummaryViewDialog.summary.issueReflection }}</strong>
        </div>
        <div class="full">
          <span>反馈评分</span>
          <strong>{{ eventSummaryViewDialog.summary?.feedbackScore ?? '-' }}</strong>
        </div>
        <div v-if="eventSummaryViewDialog.summary?.attachmentUrl" class="full">
          <span>附件</span>
          <a :href="eventSummaryViewDialog.summary.attachmentUrl" target="_blank" style="color:#1d4ed8;text-decoration:underline">下载附件</a>
        </div>
        <div class="full action-row">
          <button type="button" class="btn ghost" @click="eventSummaryViewDialog.visible = false">关闭</button>
        </div>
      </div>
    </section>
  </div>

  <div v-if="eventManageDetailDialog.visible" class="detail-mask" @click.self="eventManageDetailDialog.visible = false">
    <section class="detail-dialog">
      <div class="detail-head">
        <h4>活动详情</h4>
        <button type="button" class="icon-close-btn" aria-label="关闭" @click="eventManageDetailDialog.visible = false">×</button>
      </div>
      <div class="detail-grid">
        <div>
          <span>社团</span>
          <strong>{{ eventManageDetailDialog.event?.clubName }}</strong>
        </div>
        <div>
          <span>标题</span>
          <strong>{{ eventManageDetailDialog.event?.title }}</strong>
        </div>
        <div>
          <span>地点</span>
          <strong>{{ eventManageDetailDialog.event?.location }}</strong>
        </div>
        <div>
          <span>活动时间</span>
          <strong>{{ eventManageDetailDialog.event?.startAt?.slice(0,16)?.replace('T',' ') }} 至 {{ eventManageDetailDialog.event?.endAt?.slice(0,16)?.replace('T',' ') }}</strong>
        </div>
        <div>
          <span>报名时间</span>
          <strong>{{ eventManageDetailDialog.event?.signupStartAt?.slice(0,16)?.replace('T',' ') || '-' }} 至 {{ eventManageDetailDialog.event?.signupEndAt?.slice(0,16)?.replace('T',' ') || '-' }}</strong>
        </div>
        <div>
          <span>已报名/总可报名</span>
          <strong>{{ eventManageDetailDialog.event?.signupCount ?? 0 }}/{{ eventManageDetailDialog.event?.limitCount || '不限' }}</strong>
        </div>
        <div v-if="eventManageDetailDialog.event?.content" class="full">
          <span>活动内容</span>
          <strong>{{ eventManageDetailDialog.event.content }}</strong>
        </div>
        <div v-if="eventManageDetailDialog.event?.safetyPlanUrl" class="full">
          <span>安全预案</span>
          <a :href="eventManageDetailDialog.event.safetyPlanUrl" target="_blank">下载查看</a>
        </div>
        <div class="full action-row">
          <button type="button" class="btn ghost" @click="eventManageDetailDialog.visible = false">关闭</button>
        </div>
      </div>
    </section>
  </div>

  <div v-if="eventManageSummaryDialog.visible" class="detail-mask" @click.self="eventManageSummaryDialog.visible = false">
    <section class="detail-dialog">
      <div class="detail-head">
        <h4>活动总结详情</h4>
        <button type="button" class="icon-close-btn" aria-label="关闭" @click="eventManageSummaryDialog.visible = false">×</button>
      </div>
      <div class="detail-grid">
        <div>
          <span>社团</span>
          <strong>{{ eventManageSummaryDialog.summary?.clubName }}</strong>
        </div>
        <div>
          <span>活动</span>
          <strong>{{ eventManageSummaryDialog.summary?.eventTitle }}</strong>
        </div>
        <div>
          <span>评分</span>
          <strong>{{ eventManageSummaryDialog.summary?.feedbackScore ?? '-' }}</strong>
        </div>
        <div class="full">
          <span>总结内容</span>
          <strong>{{ eventManageSummaryDialog.summary?.summaryText }}</strong>
        </div>
        <div v-if="eventManageSummaryDialog.summary?.summaryImages && eventManageSummaryDialog.summary.summaryImages.length > 0" class="full">
          <span>活动图片（{{ eventManageSummaryDialog.summary.summaryImages.length }} 张）</span>
          <div style="display:flex;flex-wrap:wrap;gap:8px;margin-top:8px;max-height:300px;overflow-y:auto;padding:4px;border:1px solid #e5e7eb;border-radius:4px">
            <img v-for="(img, idx) in eventManageSummaryDialog.summary.summaryImages" :key="idx" :src="img" style="width:120px;height:120px;object-fit:cover;border-radius:4px;cursor:pointer;border:2px solid #e5e7eb" @click="window.open(img, '_blank')" alt="活动图片" />
          </div>
        </div>
        <div v-if="eventManageSummaryDialog.summary?.issueReflection" class="full">
          <span>问题反思</span>
          <strong>{{ eventManageSummaryDialog.summary.issueReflection }}</strong>
        </div>
        <div v-if="eventManageSummaryDialog.summary?.attachmentUrl" class="full">
          <span>附件</span>
          <a :href="eventManageSummaryDialog.summary.attachmentUrl" target="_blank" style="color:#1d4ed8;text-decoration:underline">下载附件</a>
        </div>
        <div class="full action-row">
          <button type="button" class="btn ghost" @click="eventManageSummaryDialog.visible = false">关闭</button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.admin-page {
  --bg-top: #eef4ff;
  --bg-bottom: #f9fafb;
  --panel: #ffffff;
  --line: #d8dee9;
  --text-main: #132238;
  --text-sub: #4f5d75;
  --brand: #1d4ed8;
  --danger: #be123c;

  min-height: 100vh;
  background: linear-gradient(170deg, var(--bg-top), var(--bg-bottom));
  padding: 18px;
}

.layout-shell {
  width: min(1280px, calc(100vw - 24px));
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 16px;
}

.left-menu {
  border: 1px solid #dbe7f3;
  border-radius: 16px;
  background: #ffffff;
  padding: 20px 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.05);
  height: fit-content;
  position: sticky;
  top: 18px;
}

.menu-brand {
  padding: 0 8px 12px;
  border-bottom: 1px solid #eef2ff;
  margin-bottom: 6px;
}

.menu-brand h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.menu-sub {
  margin: 4px 0 0;
  font-size: 12px;
  color: #64748b;
}

.menu-item {
  display: flex;
  align-items: center;
  height: 38px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #334155;
  text-align: left;
  padding: 0 12px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.15s, color 0.15s;
  width: 100%;
}

.menu-item:hover {
  background: #f0f7ff;
}

.menu-item.active {
  background: #eef3ff;
  color: #1d4ed8;
  font-weight: 600;
}

.menu-item.ghost {
  color: #64748b;
  font-size: 13px;
}

.menu-item.logout {
  color: #b91c1c;
}

.menu-item.logout:hover {
  background: #fff1f2;
}

.menu-group {
  display: flex;
  flex-direction: column;
}

.menu-group-btn {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 38px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #334155;
  cursor: pointer;
  padding: 0 12px;
  font-size: 14px;
  font-weight: 600;
  transition: background 0.15s;
}

.menu-group-btn:hover {
  background: #f0f7ff;
}

.menu-group-btn.group-active {
  color: #1d4ed8;
}

.menu-arrow {
  font-size: 11px;
  color: #94a3b8;
}

.submenu {
  padding-left: 8px;
  border-left: 2px solid #dbeafe;
  margin: 2px 0 2px 14px;
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.submenu-item {
  height: 34px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #475569;
  text-align: left;
  padding: 0 10px;
  cursor: pointer;
  font-size: 13px;
  width: 100%;
  transition: background 0.15s, color 0.15s;
}

.submenu-item:hover {
  background: #f0f7ff;
  color: #334155;
}

.submenu-item.active {
  background: #eef3ff;
  color: #1d4ed8;
  font-weight: 600;
}

.menu-divider {
  height: 1px;
  background: #f0f4f8;
  margin: 6px 4px;
}

.right-content {
  border: 1px solid var(--line);
  border-radius: 16px;
  background: #ffffff;
  padding: 20px;
}

.eyebrow {
  margin: 0;
  font-size: 12px;
  letter-spacing: 1.2px;
  color: var(--brand);
}

.content-head h1 {
  margin: 6px 0 0;
  font-size: 30px;
  color: var(--text-main);
}

.sub {
  margin: 10px 0 0;
  color: var(--text-sub);
}

h3 {
  margin: 0 0 14px;
  font-size: 22px;
  color: var(--text-main);
}

h4 {
  margin: 0;
  font-size: 16px;
  color: var(--text-main);
}

.panel {
  margin-top: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--panel);
  padding: 18px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-grid {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}

.stats-grid li {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 12px;
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: #f8fbff;
}

.stats-grid strong {
  font-size: 30px;
  color: var(--brand);
}

.stats-grid span {
  color: var(--text-sub);
  font-size: 13px;
}

.card-grid {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
}

.card-item {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 14px;
  text-align: left;
  background: #fff;
}

.card-item p {
  margin: 8px 0;
  font-size: 13px;
  color: var(--text-sub);
}

.user-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.check-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-sub);
}

.selected-tip {
  font-size: 13px;
  color: #475569;
}

.club-filter-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr auto;
  gap: 10px;
  margin-bottom: 12px;
  align-items: end;
}

.club-filter-row label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: var(--text-sub);
}

.club-filter-row input,
.club-filter-row select {
  height: 34px;
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 0 10px;
}

.user-table-wrap {
  border: 1px solid var(--line);
  border-radius: 12px;
  overflow: hidden;
}

.user-row {
  display: grid;
  grid-template-columns: 80px 1.4fr 1fr 1fr 2fr;
  gap: 10px;
  align-items: center;
  padding: 12px;
  border-top: 1px solid var(--line);
  background: #ffffff;
}

.club-row {
  grid-template-columns: 1.2fr 0.9fr 0.9fr 2fr;
}

.manage-row {
  grid-template-columns: 1.1fr 0.8fr 0.8fr 0.8fr 2fr;
}

.event-row {
  grid-template-columns: 0.9fr 1.2fr 1fr 0.6fr 0.6fr 1fr 0.6fr;
}

.summary-row {
  grid-template-columns: 1fr 1.2fr 0.6fr 1fr 0.6fr;
}

.event-manage-row {
  grid-template-columns: 1fr 1.5fr 0.7fr 1.2fr;
}

.btn.sm {
  height: 30px;
  padding: 0 12px;
  font-size: 13px;
}

.user-row:first-child {
  border-top: none;
}

.user-head {
  background: #f8fafc;
  color: #334155;
  font-size: 13px;
  font-weight: 600;
}

.col-name {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.col-name strong {
  font-size: 14px;
  color: #0f172a;
}

.col-name span {
  font-size: 12px;
  color: #64748b;
}

.role-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
}

.role-tag.student {
  color: #475569;
  background: #e2e8f0;
}

.role-tag.club-admin {
  color: #1d4ed8;
  background: #dbeafe;
}

.role-tag.school-admin {
  color: #166534;
  background: #dcfce7;
}

.empty-text {
  padding: 16px;
  text-align: center;
  color: #64748b;
  font-size: 13px;
}

.search-bar {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 12px 0;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 7px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
}
.search-input:focus { border-color: #6366f1; }

.filter-select {
  padding: 7px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 13px;
  background: #fff;
  cursor: pointer;
  outline: none;
}
.filter-select:focus { border-color: #6366f1; }

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  background: #f1f5f9;
  color: #64748b;
}
.status-badge.pending { background: #fef9c3; color: #a16207; }
.status-badge.approved { background: #dcfce7; color: #15803d; }
.status-badge.rejected { background: #fee2e2; color: #b91c1c; }
.status-badge.anomaly { background: #fef3c7; color: #92400e; font-size: 11px; }

.action-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-grid .action-row {
  flex-direction: row;
  border: none;
  padding: 4px 0 0;
  justify-content: flex-end;
}

.approve-btn {
  background: #16a34a !important;
  border-color: #16a34a !important;
  color: #fff !important;
  font-weight: 600;
}
.approve-btn:hover {
  background: #15803d !important;
}

.reject-btn {
  background: #ef4444 !important;
  border-color: #ef4444 !important;
  color: #fff !important;
  font-weight: 600;
}
.reject-btn:hover {
  background: #dc2626 !important;
}

.close-btn {
  background: #f1f5f9 !important;
  border-color: #e2e8f0 !important;
  color: #64748b !important;
}
.close-btn:hover {
  background: #e2e8f0 !important;
}

.btn {
  height: 36px;
  padding: 0 16px;
  border: none;
  border-radius: 10px;
  background: var(--brand);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn:hover {
  filter: brightness(0.94);
}

.ghost {
  border: 1px solid var(--line);
  color: var(--text-main);
  background: #fff;
}

.success {
  background: #16a34a;
  color: #fff;
}

.task-btn {
  border: 1px solid transparent;
}

.task-pending {
  background: #e5e7eb;
  color: #374151;
}

.task-done {
  background: #22c55e;
  color: #ffffff;
}

.danger {
  background: var(--danger);
}

.btn-disabled {
  background: #cbd5e1;
  color: #475569;
  cursor: not-allowed;
}

.btn-disabled:hover {
  filter: none;
  background: #cbd5e1;
}

.btn.disabled {
  pointer-events: none;
  opacity: 0.5;
}

.detail-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  display: grid;
  place-items: center;
  z-index: 40;
  padding: 16px;
}

.detail-dialog {
  width: min(760px, 100%);
  max-height: 85vh;
  overflow: auto;
  border-radius: 14px;
  border: 1px solid var(--line);
  background: #ffffff;
  padding: 16px;
}

.detail-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 12px;
}

.icon-close-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: #ffffff;
  color: #475569;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
}

.icon-close-btn:hover {
  background: #f8fafc;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.detail-grid div {
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-grid span {
  color: #64748b;
  font-size: 12px;
}

.detail-grid strong {
  color: #0f172a;
  font-size: 14px;
  font-weight: 600;
  word-break: break-all;
}

.detail-grid .full {
  grid-column: 1 / -1;
}

.apply-status-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  color: #334155;
  background: #f1f5f9;
}

.apply-status-tag.approved {
  color: #166534;
  background: #dcfce7;
}

.apply-status-tag.rejected {
  color: #b91c1c;
  background: #fee2e2;
}

.apply-status-tag.pending {
  color: #334155;
  background: #e2e8f0;
}

.apply-status-tag.warning {
  color: #854d0e;
  background: #fef9c3;
}

.club-status-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
}

.club-status-tag.normal {
  color: #166534;
  background: #dcfce7;
}

.club-status-tag.muted {
  color: #334155;
  background: #e2e8f0;
}

.manage-edit-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.manage-edit-form label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: #334155;
  font-size: 13px;
}

.manage-edit-form input,
.manage-edit-form select,
.manage-edit-form textarea {
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
}

.manage-edit-form .full {
  grid-column: 1 / -1;
}

.message {
  margin: 14px 0;
  border: 1px solid #99f6e4;
  background: #f0fdfa;
  border-radius: 10px;
  padding: 10px 12px;
  color: #0f766e;
  font-size: 14px;
}

@media (max-width: 980px) {
  .layout-shell {
    grid-template-columns: 1fr;
  }

  .content-head h1 {
    font-size: 24px;
  }

  .btn {
    width: 100%;
  }

  .user-row {
    grid-template-columns: 60px 1fr;
  }

  .club-filter-row {
    grid-template-columns: 1fr;
  }

  .col-role,
  .col-status,
  .col-action {
    grid-column: 2;
  }

  .club-row {
    grid-template-columns: 1fr;
  }

  .manage-row {
    grid-template-columns: 1fr;
  }

  .event-row {
    grid-template-columns: 1fr;
  }

  .summary-row {
    grid-template-columns: 1fr;
  }

  .event-manage-row {
    grid-template-columns: 1fr;
  }

  .expense-row {
    grid-template-columns: 1fr;
  }

  .anomaly-row {
    grid-template-columns: 1fr;
  }

  .ledger-row {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .manage-edit-form {
    grid-template-columns: 1fr;
  }

  .chart-row {
    grid-template-columns: 1fr;
  }
}

.expense-row {
  grid-template-columns: 1.2fr 0.8fr 0.8fr 0.6fr;
}

.anomaly-row {
  grid-template-columns: 0.9fr 0.6fr 0.6fr 1fr 1.2fr 0.7fr 0.6fr 0.5fr 1fr;
}

.ledger-row {
  grid-template-columns: 1fr 0.6fr 0.8fr 0.8fr;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.link-text {
  color: var(--brand);
  text-decoration: underline;
  cursor: pointer;
  font-size: 13px;
}

.form-textarea {
  width: 100%;
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
  resize: vertical;
}

.report-content {
  margin-top: 12px;
}

.income-color {
  color: #16a34a !important;
}

.expense-color {
  color: #ef4444 !important;
}

.anomaly-color {
  color: #b91c1c !important;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

.chart-box {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 14px;
  background: #f8fbff;
}

.chart-box h4 {
  margin: 0 0 8px;
  font-size: 15px;
  color: var(--text-main);
}

.chart-container {
  width: 100%;
  height: 350px;
}

.stats-content {
  margin-top: 14px;
}

.charts-row {
  display: flex;
  gap: 16px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.charts-row .chart-box {
  flex: 1;
  min-width: 280px;
}

.top-club-row {
  grid-template-columns: 1.5fr 1fr 1fr;
}

.finance-summary-row {
  grid-template-columns: 1.2fr 0.8fr 0.8fr 0.8fr 0.6fr 1fr;
}

@media (max-width: 980px) {
  .charts-row {
    flex-direction: column;
  }

  .top-club-row {
    grid-template-columns: 1fr;
  }

  .finance-summary-row {
    grid-template-columns: 1fr;
  }
}
</style>
