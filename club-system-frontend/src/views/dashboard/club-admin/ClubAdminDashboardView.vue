<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../stores/auth'
import {
  createMyClubPositionApi,
  deleteMyClubPositionApi,
  decideMyClubJoinApplyApi,
  getMyClubJoinApplyQueueApi,
  getMyClubPositionsApi,
  getMyClubRecruitConfigApi,
  getMyClubCancelApplyListApi,
  getMyClubFinanceApi,
  getMyClubMembersApi,
  getMyClubProfileApi,
  removeMemberApi,
  submitClubCancelApplyApi,
  uploadClubApplyMaterialApi,
  updateMemberRoleApi,
  updateMyClubPositionApi,
  updateMyClubProfileApi,
  updateMyClubRecruitConfigApi,
  getMyClubEventsApi,
  createMyClubEventApi,
  getMyClubEventDetailApi,
  resubmitMyClubEventApi,
  getMyClubEventSignupsApi,
  updateEventCheckinCodeApi,
  checkinMyClubEventApi,
  cancelCheckinMyClubEventApi,
  submitMyClubEventSummaryApi,
  getMyClubEventSummaryApi,
  createIncomeApi,
  getMyClubIncomesApi,
  createExpenseApi,
  resubmitExpenseApi,
  getMyClubExpensesApi,
  getMyClubLedgerApi,
  getMyClubBalanceApi,
} from '../../../api/user-permission'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const feedback = ref('')
const cancelSubmitting = ref(false)
const cancelListLoading = ref(false)
const members = ref([])
const memberFilter = reactive({
  keyword: '',
  positionId: '',
})
const filteredMembers = computed(() => {
  let list = members.value
  const kw = memberFilter.keyword.trim().toLowerCase()
  if (kw) {
    list = list.filter(
      (m) =>
        (m.realName || '').toLowerCase().includes(kw) ||
        (m.username || '').toLowerCase().includes(kw),
    )
  }
  if (memberFilter.positionId) {
    if (memberFilter.positionId === '__none__') {
      list = list.filter((m) => !m.positionId)
    } else {
      list = list.filter((m) => String(m.positionId || '') === memberFilter.positionId)
    }
  }
  return list
})
const positions = ref([])
const joinApplyQueue = ref([])
const financeRecords = ref([])
const myCancelApplies = ref([])
const activeMenu = ref('profile')
const clubInfoGroupExpanded = ref(true)
const memberGroupExpanded = ref(true)
const joinApplyLoading = ref(false)
const joinDecisionLoadingMap = reactive({})
const joinDetailDialog = reactive({ visible: false, record: null })
const memberPositionDraft = reactive({})
const positionSaving = ref(false)
const clubProfile = reactive({
  clubName: '',
  category: '',
  introduction: '',
  purpose: '',
  instructorName: '',
})
const recruitForm = reactive({
  recruitStartAt: '',
  recruitEndAt: '',
  recruitLimit: '',
  recruitStatus: 'OPEN',
})
const recruitSaving = ref(false)
const positionForm = reactive({
  id: null,
  positionName: '',
  parentPositionId: '',
})
const cancelForm = reactive({
  applyReason: '',
  assetSettlementFile: null,
})
const immutableClubFields = reactive({
  clubName: '',
  category: '',
  instructorName: '',
})

const eventList = ref([])
const eventLoading = ref(false)
const eventForm = reactive({
  title: '', content: '', location: '',
  startAt: '', endAt: '',
  signupStartAt: '', signupEndAt: '',
  limitCount: 1, onlyMember: false,
  safetyPlanUrl: '', safetyPlanFile: null,
})
const eventSubmitting = ref(false)
const eventFormError = ref('')
const eventCreateDialogVisible = ref(false)
const eventDetailDialog = reactive({ visible: false, event: null })
const eventEditDialogVisible = ref(false)
const eventEditId = ref(null)
const eventSignupList = ref([])
const eventSignupLoading = ref(false)
const eventSignupDialog = reactive({ visible: false, eventId: null, eventTitle: '' })
const checkinLoadingMap = reactive({})
const eventSummaryDialog = reactive({ visible: false, eventId: null, mode: 'view' })
const summaryForm = reactive({ summaryText: '', feedbackScore: '', issueReflection: '', attachmentUrl: '', attachmentFile: null, summaryImages: [], imageFiles: [] })
const summarySubmitting = ref(false)
const summaryData = ref(null)
const checkinCodeDialog = reactive({ visible: false, eventId: null, eventTitle: '' })
const checkinCodeForm = reactive({ checkinCode: '' })
const checkinCodeSubmitting = ref(false)

// ======== 经费管理 ========
const financeGroupExpanded = ref(true)
const financeSubMenu = ref('finance-overview')
const myBalance = ref(null)
const myPendingExpense = ref(null)
const myAvailableBalance = ref(null)
const balanceLoading = ref(false)

const incomeForm = reactive({
  incomeType: 1,
  amount: '',
  occurAt: '',
  proofUrl: '',
  proofFile: null,
  sourceDesc: '',
})
const incomeSubmitting = ref(false)
const incomeList = ref([])
const incomeLoading = ref(false)
const incomePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const expenseForm = reactive({
  category: '',
  amount: '',
  payeeName: '',
  payeeAccount: '',
  invoiceUrl: '',
  invoiceFile: null,
  expenseDesc: '',
})
const expenseSubmitting = ref(false)
const expenseList = ref([])
const expenseLoading = ref(false)
const expensePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const expenseStatusFilter = ref('')
const resubmitDialogVisible = ref(false)
const resubmitExpenseId = ref(null)
const resubmitForm = reactive({
  category: '', amount: '', payeeName: '', payeeAccount: '',
  invoiceUrl: '', invoiceFile: null, expenseDesc: '',
})
const resubmitLoading = ref(false)
const balanceAlertVisible = ref(false)
const balanceAlertMessage = ref('')

const ledgerList = ref([])
const ledgerLoading = ref(false)
const ledgerFilter = reactive({ bizType: '', startTime: '', endTime: '' })
const ledgerPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const INCOME_TYPE_MAP = { 1: '学校拨款', 2: '赞助收入', 3: '成员会费' }

const CANCEL_STATUS_TEXT_MAP = {
  1: '待公示',
  2: '待经费结清',
  3: '待资产移交',
  4: '已完成',
  5: '驳回',
}

const getBizData = (response) => {
  if (response && typeof response.code !== 'undefined') {
    if (response.code !== 0) {
      throw new Error(response.message || '请求失败')
    }
    return response.data
  }
  return response
}

const normalizeJoinApplyStatus = (status) => {
  const normalized = String(status || '').trim().toUpperCase()
  if (['PENDING', 'JOINED', 'REJECTED'].includes(normalized)) return normalized
  const numeric = Number(status)
  if (!Number.isNaN(numeric)) {
    if (numeric === 1) return 'PENDING'
    if (numeric === 2) return 'JOINED'
    if (numeric === 3) return 'REJECTED'
  }
  return 'PENDING'
}

// 根据 positionId 查对应层级，用于职位 badge 着色
const getPositionLevelById = (positionId) => {
  if (!positionId) return 0
  const found = positions.value.find((p) => String(p.id) === String(positionId))
  return Number(found?.levelNo ?? found?.level_no ?? 0)
}

const getJoinApplyStatusText = (status) => {  const normalized = normalizeJoinApplyStatus(status)
  if (normalized === 'JOINED') return '已加入'
  if (normalized === 'REJECTED') return '已驳回'
  return '待批准'
}

const getJoinApplyStatusClass = (status) => {
  const normalized = normalizeJoinApplyStatus(status)
  if (normalized === 'JOINED') return 'done'
  if (normalized === 'REJECTED') return 'rejected'
  return 'pending'
}

const toInputDateTime = (value) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    const plainValue = String(value)
    return plainValue.length >= 16 ? plainValue.slice(0, 16) : plainValue
  }
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hour}:${minute}`
}

const normalizeRecruitStatus = (value) => {
  if (!value) return 'OPEN'
  const status = String(value).toUpperCase()
  if (['OPEN', 'CLOSED'].includes(status)) return status
  if (['1', 'ENABLED', 'ACTIVE'].includes(status)) return 'OPEN'
  if (['0', 'DISABLED', 'INACTIVE'].includes(status)) return 'CLOSED'
  return 'OPEN'
}

const hydrateRecruitForm = (payload) => {
  recruitForm.recruitStartAt = toInputDateTime(payload?.recruitStartAt || payload?.recruit_start_at)
  recruitForm.recruitEndAt = toInputDateTime(payload?.recruitEndAt || payload?.recruit_end_at)
  recruitForm.recruitLimit = payload?.recruitLimit ?? payload?.recruit_limit ?? ''
  recruitForm.recruitStatus = normalizeRecruitStatus(payload?.recruitStatus || payload?.recruit_status || payload?.status)
}

const getPositionList = (payload) => {
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload)) return payload
  return []
}

const sortPositions = (list) =>
  [...list].sort((a, b) => {
    const levelA = Number(a?.levelNo ?? a?.level_no ?? 0)
    const levelB = Number(b?.levelNo ?? b?.level_no ?? 0)
    if (levelA !== levelB) return levelA - levelB

    const sortA = Number(a?.sortNo ?? a?.sort_no ?? 0)
    const sortB = Number(b?.sortNo ?? b?.sort_no ?? 0)
    if (sortA !== sortB) return sortA - sortB

    return String(a?.positionName || '').localeCompare(String(b?.positionName || ''))
  })

// 将扁平岗位列表构建为树形结构
const buildPositionTree = (list) => {
  const map = {}
  list.forEach((p) => { map[String(p.id)] = { ...p, children: [] } })
  const roots = []
  list.forEach((p) => {
    const parentId = p.parentPositionId ?? p.parent_position_id
    if (parentId && map[String(parentId)]) {
      map[String(parentId)].children.push(map[String(p.id)])
    } else {
      roots.push(map[String(p.id)])
    }
  })
  return roots
}

// 将树展开为带连接线前缀的扁平列表
const flattenTree = (nodes, parentPrefix = '') => {
  const result = []
  nodes.forEach((node, i) => {
    const isLast = i === nodes.length - 1
    const connector = parentPrefix === '' ? '' : (isLast ? '└─ ' : '├─ ')
    const childPrefix = parentPrefix === '' ? '' : (isLast ? '   ' : '│  ')
    result.push({
      node,
      prefix: parentPrefix + connector,
      depth: (parentPrefix.match(/[│ ]/g) || []).length / 3,
      isLast,
    })
    if (node.children && node.children.length) {
      result.push(...flattenTree(node.children, parentPrefix + childPrefix))
    }
  })
  return result
}

const flattenedPositions = computed(() => flattenTree(buildPositionTree(positions.value)))

const findPositionById = (id) => {
  const target = String(id)
  return positions.value.find((item) => String(item.id) === target)
}

const getPositionLevel = (position) => Number(position?.levelNo ?? position?.level_no ?? 1) || 1

const loadPositions = async (silent = false) => {
  try {
    const response = await getMyClubPositionsApi({ pageNum: 1, pageSize: 200 })
    const data = getBizData(response)
    positions.value = sortPositions(getPositionList(data))
  } catch (error) {
    positions.value = []
    if (!silent) {
      throw error
    }
  }
}

const hydrateMemberPositionDraft = () => {
  Object.keys(memberPositionDraft).forEach((key) => {
    delete memberPositionDraft[key]
  })

  members.value.forEach((member) => {
    memberPositionDraft[String(member.id)] = member.positionId ? String(member.positionId) : ''
  })
}

const resetPositionForm = () => {
  positionForm.id = null
  positionForm.positionName = ''
  positionForm.parentPositionId = ''
}

const getPositionParentName = (parentId) => {
  if (!parentId) return '顶级岗位'
  const position = findPositionById(parentId)
  return position?.positionName || `ID:${parentId}`
}

const isPositionHasChildren = (positionId) =>
  positions.value.some((item) => {
    const parentId = item?.parentPositionId ?? item?.parent_position_id
    return parentId !== null && typeof parentId !== 'undefined' && String(parentId) === String(positionId)
  })

const startEditPosition = (position) => {
  positionForm.id = position.id
  positionForm.positionName = position.positionName || ''
  const parentId = position.parentPositionId ?? position.parent_position_id
  positionForm.parentPositionId = parentId ? String(parentId) : ''
  activeMenu.value = 'org'
}

const savePosition = async () => {
  if (positionSaving.value) return

  const name = positionForm.positionName.trim()
  if (!name) {
    feedback.value = '请填写岗位名称'
    return
  }

  const parentPosition = positionForm.parentPositionId ? findPositionById(positionForm.parentPositionId) : null
  const parentLevel = parentPosition ? getPositionLevel(parentPosition) : 0
  const payload = {
    positionName: name,
    parentPositionId: positionForm.parentPositionId ? Number(positionForm.parentPositionId) : null,
    levelNo: parentLevel + 1,
  }

  positionSaving.value = true
  try {
    if (positionForm.id) {
      const response = await updateMyClubPositionApi(positionForm.id, payload)
      getBizData(response)
      feedback.value = '组织岗位更新成功'
    } else {
      const response = await createMyClubPositionApi(payload)
      getBizData(response)
      feedback.value = '组织岗位创建成功'
    }
    resetPositionForm()
    await loadPositions()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '组织岗位保存失败'
  } finally {
    positionSaving.value = false
  }
}

const removePosition = async (position) => {
  if (!position?.id) return

  if (isPositionHasChildren(position.id)) {
    feedback.value = '请先删除下级岗位后再删除当前岗位'
    return
  }

  const usedByMember = members.value.some((member) => String(member?.positionId || '') === String(position.id))
  if (usedByMember) {
    feedback.value = '该岗位仍有关联成员，请先调整成员职位'
    return
  }

  const ok = window.confirm(`确认删除岗位“${position.positionName || ''}”吗？`)
  if (!ok) return

  try {
    const response = await deleteMyClubPositionApi(position.id)
    getBizData(response)
    feedback.value = '组织岗位已删除'
    if (String(positionForm.id || '') === String(position.id)) {
      resetPositionForm()
    }
    await loadPositions()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '岗位删除失败'
  }
}

const setMemberPosition = async (member) => {
  const selectedId = memberPositionDraft[String(member.id)] || ''
  const selectedPosition = selectedId ? findPositionById(selectedId) : null
  const payload = selectedPosition
    ? {
        positionId: Number(selectedPosition.id),
        positionName: selectedPosition.positionName,
      }
    : {
        positionId: null,
        positionName: '',
      }

  try {
    const response = await updateMemberRoleApi(member.id, payload)
    getBizData(response)
    feedback.value = selectedPosition ? '成员职位已更新' : '成员职位已清空'
    await loadData()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '成员职位更新失败'
  }
}

const loadRecruitConfig = async () => {
  try {
    const res = await getMyClubRecruitConfigApi()
    const data = getBizData(res) || {}
    hydrateRecruitForm(data)
  } catch {
    // Fallback to profile payload if dedicated recruit API is not ready.
    const profileRes = await getMyClubProfileApi()
    const profileData = getBizData(profileRes) || {}
    hydrateRecruitForm(profileData)
  }
}

const loadData = async () => {
  loading.value = true
  feedback.value = ''
  try {
    const [profileRes, membersRes, financeRes] = await Promise.all([
      getMyClubProfileApi(),
      getMyClubMembersApi({ pageNum: 1, pageSize: 200 }),
      getMyClubFinanceApi({ pageNum: 1, pageSize: 10 }),
    ])
    const profile = getBizData(profileRes) || {}
    clubProfile.clubName = profile.clubName || ''
    clubProfile.category = profile.category || ''
    clubProfile.introduction = profile.introduction || profile.clubIntroduction || profile.description || ''
    clubProfile.purpose = profile.purpose || ''
    clubProfile.instructorName = profile.instructorName || ''
    immutableClubFields.clubName = profile.clubName || ''
    immutableClubFields.category = profile.category || ''
    immutableClubFields.instructorName = profile.instructorName || ''

    const memberData = getBizData(membersRes)
    const financeData = getBizData(financeRes)
    members.value = Array.isArray(memberData?.records) ? memberData.records : Array.isArray(memberData) ? memberData : []
    hydrateMemberPositionDraft()
    financeRecords.value = Array.isArray(financeData?.records)
      ? financeData.records
      : Array.isArray(financeData)
        ? financeData
        : []
    hydrateRecruitForm(profile)

    await Promise.all([loadRecruitConfig(), loadPositions(true)])
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '数据加载失败'
  } finally {
    loading.value = false
  }
}

const parseCancelStatus = (status) => {
  if (typeof status === 'number') return status
  const parsed = Number(status)
  return Number.isNaN(parsed) ? 0 : parsed
}

const getCancelStatusText = (status) => CANCEL_STATUS_TEXT_MAP[parseCancelStatus(status)] || '未知'

const EVENT_STATUS_MAP = { 1: '草稿', 2: '待审核', 3: '报名中', 4: '已结束', 5: '已驳回', 6: '进行中' }

const getEventStatusText = (event) => {
  if (typeof event === 'number') {
    return EVENT_STATUS_MAP[event] || '未知'
  }

  const status = event.eventStatus

  // 待审核和已驳回状态直接返回
  if (status === 2) return '待审核'
  if (status === 5) return '已驳回'
  if (status === 6) return '进行中'

  // 对于报名中(3)和已结束(4)状态，根据时间动态判断
  if (status === 3 || status === 4) {
    const now = new Date()

    // 活动已结束
    if (event.endAt) {
      const endTime = new Date(event.endAt)
      if (now > endTime) {
        return '已结束'
      }
    }

    // 活动进行中
    if (event.startAt && event.endAt) {
      const startTime = new Date(event.startAt)
      const endTime = new Date(event.endAt)
      if (now >= startTime && now <= endTime) {
        return '活动中'
      }
    }

    // 报名中
    if (event.signupStartAt && event.signupEndAt) {
      const signupStart = new Date(event.signupStartAt)
      const signupEnd = new Date(event.signupEndAt)
      if (now >= signupStart && now <= signupEnd) {
        // 检查报名名额是否已满
        if (event.signupCount !== null && event.signupCount !== undefined &&
            event.limitCount !== null && event.limitCount !== undefined &&
            event.signupCount >= event.limitCount) {
          return '报名已满'
        }
        return '报名中'
      }
    }

    // 报名未开始
    if (event.signupStartAt) {
      const signupStart = new Date(event.signupStartAt)
      if (now < signupStart) {
        return '未开始'
      }
    }

    // 报名已结束但活动未开始
    if (event.signupEndAt && event.startAt) {
      const signupEnd = new Date(event.signupEndAt)
      const startTime = new Date(event.startAt)
      if (now > signupEnd && now < startTime) {
        return '报名已结束'
      }
    }
  }

  return EVENT_STATUS_MAP[status] || '未知'
}

const getEventStatusClass = (status) => {
  if (status === 2) return 'pending'
  if (status === 3) return 'done'
  if (status === 6) return 'done'
  if (status === 4) return 'done'
  if (status === 5) return 'rejected'
  return 'pending'
}

const isEventEnded = (event) => {
  // 判断活动是否已结束（可以提交总结）
  if (event.eventStatus === 4) return true
  if ((event.eventStatus === 3 || event.eventStatus === 6) && event.endAt) {
    const now = new Date()
    const endTime = new Date(event.endAt)
    return now > endTime
  }
  return false
}

const getCancelStatusClass = (status) => {
  const parsed = parseCancelStatus(status)
  if (parsed === 4) return 'done'
  if (parsed === 5) return 'rejected'
  if ([1, 2, 3].includes(parsed)) return 'pending'
  return 'pending'
}

const loadMyCancelApplies = async () => {
  cancelListLoading.value = true
  try {
    const response = await getMyClubCancelApplyListApi({ pageNum: 1, pageSize: 20 })
    const data = getBizData(response)
    myCancelApplies.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '注销申请记录加载失败'
  } finally {
    cancelListLoading.value = false
  }
}

const loadMyClubJoinApplyQueue = async () => {
  joinApplyLoading.value = true
  try {
    const response = await getMyClubJoinApplyQueueApi({ pageNum: 1, pageSize: 50 })
    const data = getBizData(response)
    joinApplyQueue.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '入社申请队列加载失败'
  } finally {
    joinApplyLoading.value = false
  }
}

const decideJoinApply = async (record, decision) => {
  const applyId = record?.id
  if (!applyId) return

  const key = String(applyId)
  if (joinDecisionLoadingMap[key]) return

  let rejectReason = ''
  if (decision === 'REJECT') {
    rejectReason = window.prompt('请输入驳回原因（可选）', '') || ''
  }

  joinDecisionLoadingMap[key] = true
  try {
    const response = await decideMyClubJoinApplyApi(applyId, {
      decision,
      rejectReason: rejectReason.trim(),
    })
    getBizData(response)
    feedback.value = decision === 'APPROVE' ? '入社申请已通过' : '入社申请已驳回'
    await Promise.all([loadMyClubJoinApplyQueue(), loadData()])
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '审批操作失败'
  } finally {
    joinDecisionLoadingMap[key] = false
  }
}

const handleAssetSettlementFileChange = (event) => {
  const file = event?.target?.files?.[0] || null
  cancelForm.assetSettlementFile = file
}

const resetCancelForm = () => {
  cancelForm.applyReason = ''
  cancelForm.assetSettlementFile = null
}

const saveProfile = async () => {
  try {
    const response = await updateMyClubProfileApi({
      ...clubProfile,
      clubName: immutableClubFields.clubName,
      category: immutableClubFields.category,
      instructorName: immutableClubFields.instructorName,
    })
    getBizData(response)
    await loadData()
    feedback.value = '社团信息更新成功'
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '更新失败'
  }
}

const saveRecruitConfig = async () => {
  if (recruitSaving.value) return

  if (!recruitForm.recruitStartAt || !recruitForm.recruitEndAt) {
    feedback.value = '请完整设置招新开始和结束时间'
    return
  }

  const startTs = new Date(recruitForm.recruitStartAt).getTime()
  const endTs = new Date(recruitForm.recruitEndAt).getTime()
  if (Number.isNaN(startTs) || Number.isNaN(endTs) || endTs <= startTs) {
    feedback.value = '招新结束时间必须晚于开始时间'
    return
  }

  const limit = Number(recruitForm.recruitLimit)
  if (!Number.isInteger(limit) || limit <= 0) {
    feedback.value = '招新人数上限必须为正整数'
    return
  }

  recruitSaving.value = true
  try {
    const payload = {
      recruitStartAt: recruitForm.recruitStartAt,
      recruitEndAt: recruitForm.recruitEndAt,
      recruitLimit: limit,
      recruitStatus: recruitForm.recruitStatus,
    }

    try {
      const response = await updateMyClubRecruitConfigApi(payload)
      getBizData(response)
    } catch {
      const response = await updateMyClubProfileApi(payload)
      getBizData(response)
    }

    feedback.value = '招新动态设置已保存'
    await loadData()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '招新动态设置保存失败'
  } finally {
    recruitSaving.value = false
  }
}

const submitClubCancelApply = async () => {
  if (cancelSubmitting.value) return

  if (!cancelForm.applyReason.trim()) {
    feedback.value = '请填写注销原因'
    return
  }
  if (!cancelForm.assetSettlementFile) {
    feedback.value = '请上传资产清算报告文件'
    return
  }

  const ok = window.confirm('确认提交社团注销申请吗？提交后将进入学校管理员审核。')
  if (!ok) return

  cancelSubmitting.value = true
  try {
    const fileFormData = new FormData()
    fileFormData.append('file', cancelForm.assetSettlementFile, cancelForm.assetSettlementFile.name)
    fileFormData.append('multipartFile', cancelForm.assetSettlementFile, cancelForm.assetSettlementFile.name)
    fileFormData.append('bizType', 'CLUB_CANCEL_ASSET_SETTLEMENT')
    const uploadRes = await uploadClubApplyMaterialApi(fileFormData)
    const uploadData = getBizData(uploadRes) || {}
    const assetSettlementUrl = uploadData.url || uploadData.fileUrl || uploadData.path || ''
    if (!assetSettlementUrl) {
      throw new Error('资产清算报告上传成功但未返回访问地址')
    }

    const response = await submitClubCancelApplyApi({
      applyType: 1,
      applyReason: cancelForm.applyReason.trim(),
      assetSettlementUrl,
    })
    getBizData(response)
    resetCancelForm()
    await loadMyCancelApplies()
    feedback.value = '社团注销申请已提交，待学校管理员审核'
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '提交社团注销申请失败'
  } finally {
    cancelSubmitting.value = false
  }
}

const removeMember = async (member) => {
  const memberId = member?.id
  if (!memberId) return
  const name = member?.realName || member?.username || '该成员'
  const ok = window.confirm(`确认移除成员「${name}」吗？移除后该成员将退出本社团。`)
  if (!ok) return
  try {
    const response = await removeMemberApi(memberId)
    getBizData(response)
    feedback.value = `成员「${name}」已移除`
    await loadData()
  } catch (error) {
    feedback.value = error?.response?.data?.message || error?.message || '移除失败'
  }
}

const loadMyEvents = async () => {
  eventLoading.value = true
  try {
    const response = await getMyClubEventsApi({ pageNum: 1, pageSize: 50 })
    const data = getBizData(response)
    eventList.value = Array.isArray(data?.records) ? data.records : []
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动列表加载失败'
  } finally { eventLoading.value = false }
}

const resetEventForm = () => {
  eventForm.title = ''; eventForm.content = ''; eventForm.location = ''
  eventForm.startAt = ''; eventForm.endAt = ''
  eventForm.signupStartAt = ''; eventForm.signupEndAt = ''
  eventForm.limitCount = 0; eventForm.onlyMember = false
  eventForm.safetyPlanUrl = ''; eventForm.safetyPlanFile = null
}

const handleCreateEvent = async () => {
  eventFormError.value = ''
  if (!eventForm.title.trim() || !eventForm.location.trim() || !eventForm.startAt || !eventForm.endAt) {
    eventFormError.value = '请填写必填字段（标题、地点、开始/结束时间）'; return
  }
  if (!eventForm.limitCount || eventForm.limitCount < 1) {
    eventFormError.value = '人数上限不能为空且至少为1'; return
  }
  if (eventForm.signupEndAt && eventForm.startAt && new Date(eventForm.startAt) < new Date(eventForm.signupEndAt)) {
    eventFormError.value = '活动开始时间不能早于报名截止时间'; return
  }
  eventSubmitting.value = true
  try {
    let safetyPlanUrl = eventForm.safetyPlanUrl
    if (eventForm.safetyPlanFile) {
      const fd = new FormData()
      fd.append('file', eventForm.safetyPlanFile)
      fd.append('bizType', 'EVENT_SAFETY_PLAN')
      const uploadRes = await uploadClubApplyMaterialApi(fd)
      safetyPlanUrl = getBizData(uploadRes)?.url || ''
    }
    const payload = {
      title: eventForm.title.trim(),
      content: eventForm.content,
      location: eventForm.location.trim(),
      startAt: eventForm.startAt,
      endAt: eventForm.endAt,
      signupStartAt: eventForm.signupStartAt || null,
      signupEndAt: eventForm.signupEndAt || null,
      limitCount: eventForm.limitCount,
      onlyMember: eventForm.onlyMember || false,
      safetyPlanUrl: safetyPlanUrl || null,
    }
    await createMyClubEventApi(payload)
    eventFormError.value = ''
    eventCreateDialogVisible.value = false
    feedback.value = '活动已提交审核'
    resetEventForm()
    await loadMyEvents()
  } catch (error) {
    eventFormError.value = error?.response?.data?.message || '活动创建失败'
  } finally { eventSubmitting.value = false }
}

const handleResubmitEvent = async (eventId) => {
  eventFormError.value = ''
  if (!eventForm.title.trim() || !eventForm.location.trim() || !eventForm.startAt || !eventForm.endAt) {
    eventFormError.value = '请填写必填字段（标题、地点、开始/结束时间）'; return
  }
  if (!eventForm.limitCount || eventForm.limitCount < 1) {
    eventFormError.value = '人数上限不能为空且至少为1'; return
  }
  if (eventForm.signupEndAt && eventForm.startAt && new Date(eventForm.startAt) < new Date(eventForm.signupEndAt)) {
    eventFormError.value = '活动开始时间不能早于报名截止时间'; return
  }
  eventSubmitting.value = true
  try {
    let safetyPlanUrl = eventForm.safetyPlanUrl
    if (eventForm.safetyPlanFile) {
      const fd = new FormData()
      fd.append('file', eventForm.safetyPlanFile)
      fd.append('bizType', 'EVENT_SAFETY_PLAN')
      const uploadRes = await uploadClubApplyMaterialApi(fd)
      safetyPlanUrl = getBizData(uploadRes)?.url || ''
    }
    const payload = {
      title: eventForm.title.trim(), content: eventForm.content, location: eventForm.location.trim(),
      startAt: eventForm.startAt, endAt: eventForm.endAt,
      signupStartAt: eventForm.signupStartAt || null, signupEndAt: eventForm.signupEndAt || null,
      limitCount: eventForm.limitCount, onlyMember: eventForm.onlyMember || false,
      safetyPlanUrl: safetyPlanUrl || null,
    }
    await resubmitMyClubEventApi(eventId, payload)
    eventFormError.value = ''
    eventEditDialogVisible.value = false
    feedback.value = '活动已重新提交审核'
    await loadMyEvents()
  } catch (error) {
    eventFormError.value = error?.response?.data?.message || '重新提交失败'
  } finally { eventSubmitting.value = false }
}

const openEventDetail = async (event) => {
  try {
    const res = await getMyClubEventDetailApi(event.id)
    const detail = getBizData(res)
    eventDetailDialog.event = detail
    eventDetailDialog.visible = true
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动详情加载失败'
  }
}

const openEditEvent = async (ev) => {
  eventFormError.value = ''
  try {
    const res = await getMyClubEventDetailApi(ev.id)
    const e = getBizData(res)
    eventForm.title = e.title || ''; eventForm.content = e.content || ''; eventForm.location = e.location || ''
    eventForm.startAt = e.startAt ? e.startAt.slice(0, 16) : ''
    eventForm.endAt = e.endAt ? e.endAt.slice(0, 16) : ''
    eventForm.signupStartAt = e.signupStartAt ? e.signupStartAt.slice(0, 16) : ''
    eventForm.signupEndAt = e.signupEndAt ? e.signupEndAt.slice(0, 16) : ''
    eventForm.limitCount = e.limitCount || 0; eventForm.onlyMember = e.onlyMember || false
    eventForm.safetyPlanUrl = e.safetyPlanUrl || ''; eventForm.safetyPlanFile = null
    eventEditId.value = ev.id
    eventEditDialogVisible.value = true
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动详情加载失败'
  }
}

const openSignupList = async (event) => {
  eventSignupDialog.eventId = event.id
  eventSignupDialog.eventTitle = event.title
  eventSignupDialog.visible = true
  eventSignupLoading.value = true
  try {
    const res = await getMyClubEventSignupsApi(event.id, { pageNum: 1, pageSize: 200 })
    eventSignupList.value = getBizData(res)?.records || []
  } catch (error) {
    feedback.value = error?.response?.data?.message || '报名列表加载失败'
  } finally { eventSignupLoading.value = false }
}

const handleCheckin = async (signup) => {
  const key = String(signup.userId)
  if (checkinLoadingMap[key]) return
  checkinLoadingMap[key] = true
  try {
    await checkinMyClubEventApi(eventSignupDialog.eventId, { userId: signup.userId, checkinType: 1 })
    feedback.value = '签到成功'
    await openSignupList({ id: eventSignupDialog.eventId, title: eventSignupDialog.eventTitle })
  } catch (error) {
    feedback.value = error?.response?.data?.message || '签到失败'
  } finally { checkinLoadingMap[key] = false }
}

const handleCancelCheckin = async (signup) => {
  const key = String(signup.userId)
  if (checkinLoadingMap[key]) return
  checkinLoadingMap[key] = true
  try {
    await cancelCheckinMyClubEventApi(eventSignupDialog.eventId, signup.userId)
    feedback.value = '已取消签到'
    await openSignupList({ id: eventSignupDialog.eventId, title: eventSignupDialog.eventTitle })
  } catch (error) {
    feedback.value = error?.response?.data?.message || '取消签到失败'
  } finally { checkinLoadingMap[key] = false }
}

const openCheckinCodeDialog = async (event) => {
  try {
    const res = await getMyClubEventDetailApi(event.id)
    const detail = getBizData(res)
    checkinCodeDialog.eventId = event.id
    checkinCodeDialog.eventTitle = event.title
    checkinCodeForm.checkinCode = detail.checkinCode || ''
    checkinCodeDialog.visible = true
  } catch (error) {
    feedback.value = error?.response?.data?.message || '活动详情加载失败'
  }
}

const handleUpdateCheckinCode = async () => {
  if (checkinCodeSubmitting.value) return
  checkinCodeSubmitting.value = true
  try {
    await updateEventCheckinCodeApi(checkinCodeDialog.eventId, { checkinCode: checkinCodeForm.checkinCode.trim() || null })
    feedback.value = '签到码已更新'
    checkinCodeDialog.visible = false
    await loadMyEvents()
  } catch (error) {
    feedback.value = error?.response?.data?.message || '签到码更新失败'
  } finally { checkinCodeSubmitting.value = false }
}

const openSummary = async (event, mode) => {
  eventSummaryDialog.eventId = event.id
  eventSummaryDialog.mode = mode
  summaryData.value = null
  summaryForm.summaryText = ''; summaryForm.feedbackScore = ''; summaryForm.issueReflection = ''
  summaryForm.attachmentUrl = ''; summaryForm.attachmentFile = null
  summaryForm.summaryImages = []; summaryForm.imageFiles = []
  if (mode === 'view' || event.hasSummary) {
    try {
      const res = await getMyClubEventSummaryApi(event.id)
      const data = getBizData(res)
      summaryData.value = data
      if (mode === 'edit' && data) {
        summaryForm.summaryText = data.summaryText || ''
        summaryForm.feedbackScore = data.feedbackScore || ''
        summaryForm.issueReflection = data.issueReflection || ''
        summaryForm.attachmentUrl = data.attachmentUrl || ''
        summaryForm.summaryImages = data.summaryImages || []
      }
    } catch (_) {}
  }
  eventSummaryDialog.visible = true
}

const handleImageSelect = (e) => {
  const files = Array.from(e.target.files || [])
  if (files.length > 0) {
    files.forEach(file => {
      summaryForm.imageFiles.push(file)
    })
  }
  e.target.value = ''
}

const getImagePreviewUrl = (file) => {
  return URL.createObjectURL(file)
}

const removeImage = (index) => {
  if (index < summaryForm.summaryImages.length) {
    summaryForm.summaryImages.splice(index, 1)
  } else {
    summaryForm.imageFiles.splice(index - summaryForm.summaryImages.length, 1)
  }
}

const handleSubmitSummary = async () => {
  if (!summaryForm.summaryText.trim()) { feedback.value = '请填写总结内容'; return }
  summarySubmitting.value = true
  try {
    let attachmentUrl = summaryForm.attachmentUrl
    if (summaryForm.attachmentFile) {
      const fd = new FormData()
      fd.append('file', summaryForm.attachmentFile)
      fd.append('bizType', 'EVENT_SUMMARY_ATTACHMENT')
      const uploadRes = await uploadClubApplyMaterialApi(fd)
      attachmentUrl = getBizData(uploadRes)?.url || ''
    }

    const uploadedImageUrls = [...summaryForm.summaryImages]
    for (const file of summaryForm.imageFiles) {
      const fd = new FormData()
      fd.append('file', file)
      fd.append('bizType', 'EVENT_SUMMARY_IMAGE')
      const uploadRes = await uploadClubApplyMaterialApi(fd)
      const url = getBizData(uploadRes)?.url
      if (url) uploadedImageUrls.push(url)
    }

    await submitMyClubEventSummaryApi(eventSummaryDialog.eventId, {
      summaryText: summaryForm.summaryText.trim(),
      summaryImages: uploadedImageUrls,
      feedbackScore: summaryForm.feedbackScore ? Number(summaryForm.feedbackScore) : null,
      issueReflection: summaryForm.issueReflection || null,
      attachmentUrl: attachmentUrl || null,
    })
    feedback.value = '活动总结已提交'
    eventSummaryDialog.visible = false
    await loadMyEvents()
  } catch (error) {
    feedback.value = error?.response?.data?.message || '提交总结失败'
  } finally { summarySubmitting.value = false }
}

// ======== 经费管理方法 ========
const loadMyBalance = async () => {
  balanceLoading.value = true
  try {
    const res = await getMyClubBalanceApi()
    const data = getBizData(res)
    myBalance.value = data?.balance ?? data ?? null
    myPendingExpense.value = data?.pendingExpense ?? null
    myAvailableBalance.value = data?.availableBalance ?? null
  } catch (error) {
    myBalance.value = null
    myPendingExpense.value = null
    myAvailableBalance.value = null
  } finally {
    balanceLoading.value = false
  }
}

const loadIncomes = async () => {
  incomeLoading.value = true
  try {
    const res = await getMyClubIncomesApi({ pageNum: incomePagination.pageNum, pageSize: incomePagination.pageSize })
    const data = getBizData(res)
    incomeList.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
    incomePagination.total = data?.total ?? incomeList.value.length
  } catch (error) {
    feedback.value = error?.response?.data?.message || '收入记录加载失败'
  } finally {
    incomeLoading.value = false
  }
}

const resetIncomeForm = () => {
  incomeForm.incomeType = 1
  incomeForm.amount = ''
  incomeForm.occurAt = ''
  incomeForm.proofUrl = ''
  incomeForm.proofFile = null
  incomeForm.sourceDesc = ''
}

const submitIncome = async () => {
  if (incomeSubmitting.value) return
  if (!incomeForm.amount || Number(incomeForm.amount) <= 0) {
    feedback.value = '请输入有效金额'
    return
  }
  if (!incomeForm.occurAt) {
    feedback.value = '请选择发生时间'
    return
  }
  incomeSubmitting.value = true
  try {
    let proofUrl = incomeForm.proofUrl
    if (incomeForm.proofFile) {
      const fd = new FormData()
      fd.append('file', incomeForm.proofFile)
      fd.append('bizType', 'FINANCE_PROOF')
      const uploadRes = await uploadClubApplyMaterialApi(fd)
      proofUrl = getBizData(uploadRes)?.url || ''
    }
    const payload = {
      incomeType: incomeForm.incomeType,
      amount: Number(incomeForm.amount),
      occurAt: incomeForm.occurAt,
      proofUrl: proofUrl || null,
      sourceDesc: incomeForm.sourceDesc.trim() || null,
    }
    const res = await createIncomeApi(payload)
    getBizData(res)
    feedback.value = '收入录入成功'
    resetIncomeForm()
    await Promise.all([loadIncomes(), loadMyBalance()])
  } catch (error) {
    feedback.value = error?.response?.data?.message || '收入录入失败'
  } finally {
    incomeSubmitting.value = false
  }
}

const loadExpenses = async () => {
  expenseLoading.value = true
  try {
    const params = { pageNum: expensePagination.pageNum, pageSize: expensePagination.pageSize }
    if (expenseStatusFilter.value) params.status = expenseStatusFilter.value
    const res = await getMyClubExpensesApi(params)
    const data = getBizData(res)
    expenseList.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
    expensePagination.total = data?.total ?? expenseList.value.length
  } catch (error) {
    feedback.value = error?.response?.data?.message || '支出记录加载失败'
  } finally {
    expenseLoading.value = false
  }
}

const resetExpenseForm = () => {
  expenseForm.category = ''
  expenseForm.amount = ''
  expenseForm.payeeName = ''
  expenseForm.payeeAccount = ''
  expenseForm.invoiceUrl = ''
  expenseForm.invoiceFile = null
  expenseForm.expenseDesc = ''
}

const openResubmitDialog = (item) => {
  resubmitExpenseId.value = item.id
  resubmitForm.category = item.category || ''
  resubmitForm.amount = item.amount != null ? String(item.amount) : ''
  resubmitForm.payeeName = item.payeeName || ''
  resubmitForm.payeeAccount = item.payeeAccount || ''
  resubmitForm.invoiceUrl = item.invoiceUrl || ''
  resubmitForm.invoiceFile = null
  resubmitForm.expenseDesc = item.expenseDesc || ''
  resubmitDialogVisible.value = true
}

const submitResubmit = async () => {
  if (!resubmitForm.category.trim()) { feedback.value = '请输入支出类别'; return }
  if (!resubmitForm.amount || Number(resubmitForm.amount) <= 0) { feedback.value = '请输入有效金额'; return }
  resubmitLoading.value = true
  try {
    let invoiceUrl = resubmitForm.invoiceUrl
    if (resubmitForm.invoiceFile) {
      const fd = new FormData()
      fd.append('file', resubmitForm.invoiceFile)
      fd.append('bizType', 'EXPENSE_INVOICE')
      const uploadRes = await uploadClubApplyMaterialApi(fd)
      invoiceUrl = getBizData(uploadRes)?.url || ''
    }
    const payload = {
      category: resubmitForm.category.trim(),
      amount: Number(resubmitForm.amount),
      payeeName: resubmitForm.payeeName.trim() || null,
      payeeAccount: resubmitForm.payeeAccount.trim() || null,
      invoiceUrl: invoiceUrl || null,
      expenseDesc: resubmitForm.expenseDesc.trim() || null,
    }
    await resubmitExpenseApi(resubmitExpenseId.value, payload)
    feedback.value = '重新提交成功'
    resubmitDialogVisible.value = false
    await Promise.all([loadExpenses(), loadMyBalance()])
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message || '重新提交失败'
    if (msg.includes('余额不足')) {
      balanceAlertMessage.value = msg
      balanceAlertVisible.value = true
    } else {
      feedback.value = msg
    }
  } finally { resubmitLoading.value = false }
}

const getResubmitAmountHint = computed(() => {
  const amt = Number(resubmitForm.amount)
  if (!amt || amt <= 0) return ''
  if (amt <= 500) return '社团自审，自动通过'
  return '需学校管理员审批'
})

const getExpenseAmountHint = computed(() => {
  const amt = Number(expenseForm.amount)
  if (!amt || amt <= 0) return ''
  if (amt <= 500) return '社团自审，自动通过'
  return '需学校管理员审核'
})

const submitExpense = async () => {
  if (expenseSubmitting.value) return
  if (!expenseForm.category.trim()) {
    feedback.value = '请输入支出类别'
    return
  }
  if (!expenseForm.amount || Number(expenseForm.amount) <= 0) {
    feedback.value = '请输入有效金额'
    return
  }
  expenseSubmitting.value = true
  try {
    let invoiceUrl = expenseForm.invoiceUrl
    if (expenseForm.invoiceFile) {
      const fd = new FormData()
      fd.append('file', expenseForm.invoiceFile)
      fd.append('bizType', 'EXPENSE_INVOICE')
      const uploadRes = await uploadClubApplyMaterialApi(fd)
      invoiceUrl = getBizData(uploadRes)?.url || ''
    }
    const payload = {
      category: expenseForm.category.trim(),
      amount: Number(expenseForm.amount),
      payeeName: expenseForm.payeeName.trim() || null,
      payeeAccount: expenseForm.payeeAccount.trim() || null,
      invoiceUrl: invoiceUrl || null,
      expenseDesc: expenseForm.expenseDesc.trim() || null,
    }
    const res = await createExpenseApi(payload)
    getBizData(res)
    feedback.value = '支出申请提交成功'
    resetExpenseForm()
    await Promise.all([loadExpenses(), loadMyBalance()])
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message || '支出申请提交失败'
    if (msg.includes('余额不足')) {
      balanceAlertMessage.value = msg
      balanceAlertVisible.value = true
    } else {
      feedback.value = msg
    }
  } finally {
    expenseSubmitting.value = false
  }
}

const loadLedger = async () => {
  ledgerLoading.value = true
  try {
    const params = { pageNum: ledgerPagination.pageNum, pageSize: ledgerPagination.pageSize }
    if (ledgerFilter.bizType) params.bizType = ledgerFilter.bizType
    if (ledgerFilter.startTime) params.startTime = ledgerFilter.startTime
    if (ledgerFilter.endTime) params.endTime = ledgerFilter.endTime
    const res = await getMyClubLedgerApi(params)
    const data = getBizData(res)
    ledgerList.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
    ledgerPagination.total = data?.total ?? ledgerList.value.length
  } catch (error) {
    feedback.value = error?.response?.data?.message || '台账明细加载失败'
  } finally {
    ledgerLoading.value = false
  }
}

const handleFinanceSubMenuChange = (sub) => {
  financeSubMenu.value = sub
  activeMenu.value = sub
  if (sub === 'finance-overview') {
    loadMyBalance()
  } else if (sub === 'finance-income') {
    loadIncomes()
  } else if (sub === 'finance-expense') {
    loadExpenses()
  } else if (sub === 'finance-ledger') {
    loadLedger()
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadData()
  loadMyCancelApplies()
  loadMyClubJoinApplyQueue()
  loadMyEvents()
})
</script>

<template>
  <div class="admin-page">
    <div class="layout-shell">
      <aside class="left-menu">
        <div class="menu-brand">
          <h2>社团管理员</h2>
          <p class="menu-sub">社团运维工作台</p>
        </div>

        <div class="menu-group">
          <button
            type="button"
            class="menu-group-btn"
            :class="{ 'group-active': ['profile', 'recruit'].includes(activeMenu) }"
            @click="clubInfoGroupExpanded = !clubInfoGroupExpanded"
          >
            <span>社团信息</span>
            <span class="menu-arrow">{{ clubInfoGroupExpanded ? '▾' : '▸' }}</span>
          </button>
          <div v-show="clubInfoGroupExpanded" class="submenu">
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'profile' }"
              @click="activeMenu = 'profile'"
            >信息维护</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'recruit' }"
              @click="activeMenu = 'recruit'"
            >招新设置</button>
          </div>
        </div>

        <div class="menu-group">
          <button
            type="button"
            class="menu-group-btn"
            :class="{ 'group-active': ['members', 'org', 'join-approval'].includes(activeMenu) }"
            @click="memberGroupExpanded = !memberGroupExpanded"
          >
            <span>成员管理</span>
            <span class="menu-arrow">{{ memberGroupExpanded ? '▾' : '▸' }}</span>
          </button>
          <div v-show="memberGroupExpanded" class="submenu">
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'members' }"
              @click="activeMenu = 'members'"
            >成员列表</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'org' }"
              @click="activeMenu = 'org'"
            >组织架构</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'join-approval' }"
              @click="activeMenu = 'join-approval'"
            >入社审批</button>
          </div>
        </div>

        <div class="menu-group">
          <button
            type="button"
            class="menu-group-btn"
            :class="{ 'group-active': ['finance-overview', 'finance-income', 'finance-expense', 'finance-ledger'].includes(activeMenu) }"
            @click="financeGroupExpanded = !financeGroupExpanded"
          >
            <span>经费管理</span>
            <span class="menu-arrow">{{ financeGroupExpanded ? '▾' : '▸' }}</span>
          </button>
          <div v-show="financeGroupExpanded" class="submenu">
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'finance-overview' }"
              @click="handleFinanceSubMenuChange('finance-overview')"
            >经费概览</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'finance-income' }"
              @click="handleFinanceSubMenuChange('finance-income')"
            >收入录入</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'finance-expense' }"
              @click="handleFinanceSubMenuChange('finance-expense')"
            >支出申请</button>
            <button
              type="button"
              class="submenu-item"
              :class="{ active: activeMenu === 'finance-ledger' }"
              @click="handleFinanceSubMenuChange('finance-ledger')"
            >台账明细</button>
          </div>
        </div>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'events' }" @click="activeMenu = 'events'">活动管理</button>
        <button type="button" class="menu-item menu-item-danger" :class="{ active: activeMenu === 'cancel' }" @click="activeMenu = 'cancel'">
          社团注销
        </button>

        <div class="menu-divider"></div>
        <button type="button" class="menu-item ghost" :disabled="loading" @click="loadData">刷新数据</button>
        <button type="button" class="menu-item logout" @click="handleLogout">退出登录</button>
      </aside>

      <main class="right-content">
        <header class="content-head">
          <p class="eyebrow">CLUB ADMIN WORKSPACE</p>
          <h1>社团管理员中心</h1>
          <p class="sub">管理本社团资料、成员与经费记录。</p>
        </header>

        <p v-if="feedback" class="message">{{ feedback }}</p>

        <section v-show="activeMenu === 'profile'" class="panel">
          <div class="section-head">
            <h3>社团信息维护</h3>
            <button type="button" class="btn" :disabled="loading" @click="saveProfile">保存信息</button>
          </div>
          <div class="form-grid">
            <label>
              社团名称
              <input v-model.trim="clubProfile.clubName" type="text" readonly class="readonly-field" title="社团管理员不可修改" />
            </label>
            <label>
              分类
              <input v-model.trim="clubProfile.category" type="text" readonly class="readonly-field" title="社团管理员不可修改" />
            </label>
            <label>
              指导老师
              <input
                v-model.trim="clubProfile.instructorName"
                type="text"
                readonly
                class="readonly-field"
                title="社团管理员不可修改"
              />
            </label>
            <label class="full">
              社团简介
              <textarea v-model.trim="clubProfile.introduction" rows="2" maxlength="300" placeholder="请输入社团简介（最多300字）" />
            </label>
            <label class="full">
              社团宗旨
              <textarea v-model.trim="clubProfile.purpose" rows="3" />
            </label>
          </div>
        </section>

        <section v-show="activeMenu === 'members'" class="panel">
          <div class="section-head">
            <h3>成员管理</h3>
            <span class="member-count">共 <strong>{{ members.length }}</strong> 人</span>
          </div>

          <div class="member-filter-bar">
            <input
              v-model="memberFilter.keyword"
              type="text"
              class="filter-input"
              placeholder="搜索姓名或学号..."
            />
            <select v-model="memberFilter.positionId" class="filter-select">
              <option value="">全部职位</option>
              <option value="__none__">无岗位</option>
              <option v-for="position in positions" :key="position.id" :value="String(position.id)">
                {{ position.positionName }}
              </option>
            </select>
            <button
              v-if="memberFilter.keyword || memberFilter.positionId"
              type="button"
              class="btn ghost btn-sm"
              @click="memberFilter.keyword = ''; memberFilter.positionId = ''"
            >重置</button>
            <span v-if="memberFilter.keyword || memberFilter.positionId" class="filter-result-tip">
              显示 {{ filteredMembers.length }} / {{ members.length }} 人
            </span>
          </div>

          <div class="member-table-wrap">
            <table class="member-table">
              <thead>
                <tr>
                  <th class="th-seq">#</th>
                  <th>成员</th>
                  <th>职位</th>
                  <th>加入时间</th>
                  <th>调整职位</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(member, index) in filteredMembers" :key="member.id">
                  <td class="td-seq">{{ index + 1 }}</td>
                  <td>
                    <div class="member-info">
                      <span class="member-name">{{ member.realName || '-' }}</span>
                      <span class="member-username">{{ member.username || '-' }}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      class="position-badge"
                      :class="`pos-level-${Math.min(getPositionLevelById(member.positionId), 4)}`"
                    >{{ member.positionName || '无岗位' }}</span>
                  </td>
                  <td class="td-secondary">{{ member.joinAt ? member.joinAt.slice(0, 10) : '-' }}</td>
                  <td>
                    <select v-model="memberPositionDraft[String(member.id)]" class="member-position-select">
                      <option value="">无岗位</option>
                      <option v-for="position in positions" :key="position.id" :value="String(position.id)">
                        {{ position.positionName }}（L{{ position.levelNo || position.level_no || 1 }}）
                      </option>
                    </select>
                  </td>
                  <td>
                    <div class="action-row">
                      <button type="button" class="btn ghost btn-sm" @click="setMemberPosition(member)">保存</button>
                      <button type="button" class="btn danger btn-sm" @click="removeMember(member)">移除</button>
                    </div>
                  </td>
                </tr>
                <tr v-if="filteredMembers.length === 0">
                  <td colspan="6" class="td-empty">
                    {{ memberFilter.keyword || memberFilter.positionId ? '无匹配成员' : '暂无成员' }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-show="activeMenu === 'join-approval'" class="panel">
          <div class="section-head">
            <h3>入社申请审批</h3>
            <button type="button" class="btn ghost" :disabled="joinApplyLoading" @click="loadMyClubJoinApplyQueue">
              {{ joinApplyLoading ? '加载中...' : '刷新队列' }}
            </button>
          </div>

          <table v-if="joinApplyQueue.length > 0" class="apply-table">
            <thead>
              <tr>
                <th>姓名</th>
                <th>学号/账号</th>
                <th>申请状态</th>
                <th>申请时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in joinApplyQueue" :key="record.id">
                <td>{{ record.realName || record.username || '申请人' }}</td>
                <td>{{ record.username || '-' }}</td>
                <td>
                  <span :class="['cancel-status-tag', getJoinApplyStatusClass(record.joinStatus)]">
                    {{ getJoinApplyStatusText(record.joinStatus) }}
                  </span>
                </td>
                <td>{{ record.createdAt || '-' }}</td>
                <td class="apply-table-actions">
                  <button
                    type="button"
                    class="btn ghost sm"
                    @click="joinDetailDialog.record = record; joinDetailDialog.visible = true"
                  >详情</button>
                  <template v-if="normalizeJoinApplyStatus(record.joinStatus) === 'PENDING'">
                    <button
                      type="button"
                      class="btn sm"
                      :disabled="joinDecisionLoadingMap[String(record.id)]"
                      @click="decideJoinApply(record, 'APPROVE')"
                    >通过</button>
                    <button
                      type="button"
                      class="btn danger sm"
                      :disabled="joinDecisionLoadingMap[String(record.id)]"
                      @click="decideJoinApply(record, 'REJECT')"
                    >驳回</button>
                  </template>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="empty-text">暂无入社申请待审批</p>
        </section>

        <section v-show="activeMenu === 'events'" class="panel">
          <div class="section-head">
            <h3>活动管理</h3>
            <div style="display:flex;gap:8px">
              <button type="button" class="btn ghost" :disabled="eventLoading" @click="loadMyEvents">
                {{ eventLoading ? '加载中...' : '刷新' }}
              </button>
              <button type="button" class="btn" @click="resetEventForm(); eventFormError = ''; eventCreateDialogVisible = true">发起活动</button>
            </div>
          </div>
          <table v-if="eventList.length > 0" class="apply-table">
            <thead><tr><th>标题</th><th>状态</th><th>时间</th><th>报名/上限</th><th>签到</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="ev in eventList" :key="ev.id">
                <td>{{ ev.title }}</td>
                <td><span :class="['cancel-status-tag', getEventStatusClass(ev.eventStatus)]">{{ getEventStatusText(ev) }}</span></td>
                <td>{{ ev.startAt ? ev.startAt.slice(0, 16).replace('T', ' ') : '-' }}</td>
                <td>{{ ev.signupCount ?? 0 }} / {{ ev.limitCount || '不限' }}</td>
                <td>{{ ev.checkinCount ?? 0 }}</td>
                <td class="apply-table-actions">
                  <button type="button" class="btn ghost sm" @click="openEventDetail(ev)">详情</button>
                  <button
                    type="button"
                    class="btn sm"
                    style="background:#f59e0b;border-color:#f59e0b;color:#fff"
                    :disabled="ev.eventStatus !== 5"
                    :style="ev.eventStatus !== 5 ? 'opacity: 0.5; cursor: not-allowed; background:#d1d5db; border-color:#d1d5db;' : 'background:#f59e0b;border-color:#f59e0b;'"
                    @click="ev.eventStatus === 5 ? openEditEvent(ev) : null"
                  >修改并提交</button>
                  <button
                    type="button"
                    class="btn ghost sm"
                    :disabled="![3, 6, 4].includes(ev.eventStatus)"
                    :style="![3, 6, 4].includes(ev.eventStatus) ? 'opacity: 0.5; cursor: not-allowed;' : ''"
                    @click="[3, 6, 4].includes(ev.eventStatus) ? openCheckinCodeDialog(ev) : null"
                  >设置签到码</button>
                  <button
                    type="button"
                    class="btn ghost sm"
                    :disabled="![3, 6, 4].includes(ev.eventStatus)"
                    :style="![3, 6, 4].includes(ev.eventStatus) ? 'opacity: 0.5; cursor: not-allowed;' : ''"
                    @click="[3, 6, 4].includes(ev.eventStatus) ? openSignupList(ev) : null"
                  >报名/签到</button>
                  <button
                    type="button"
                    class="btn ghost sm"
                    :disabled="ev.eventStatus !== 4"
                    :style="ev.eventStatus !== 4 ? 'opacity: 0.5; cursor: not-allowed;' : ''"
                    @click="ev.eventStatus === 4 ? openSummary(ev, ev.hasSummary ? 'view' : 'edit') : null"
                  >总结</button>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="empty-text">暂无活动记录</p>
        </section>

        <section v-show="activeMenu === 'org'" class="panel">
          <div class="section-head">
            <h3>组织架构维护</h3>
            <button type="button" class="btn ghost" @click="resetPositionForm">清空表单</button>
          </div>

          <div class="org-layout">
            <div class="org-form-wrap">
              <h4>{{ positionForm.id ? '编辑岗位' : '新增岗位' }}</h4>
              <div class="form-grid">
                <label>
                  岗位名称
                  <input v-model.trim="positionForm.positionName" type="text" placeholder="如：部长/副部长/干事" />
                </label>
                <label>
                  上级岗位
                  <select v-model="positionForm.parentPositionId">
                    <option value="">无（顶级岗位）</option>
                    <option
                      v-for="position in positions"
                      :key="position.id"
                      :value="String(position.id)"
                      :disabled="String(position.id) === String(positionForm.id || '')"
                    >
                      {{ position.positionName }}（L{{ position.levelNo || position.level_no || 1 }}）
                    </option>
                  </select>
                </label>
              </div>
              <div class="action-row">
                <button type="button" class="btn" :disabled="positionSaving" @click="savePosition">
                  {{ positionSaving ? '保存中...' : positionForm.id ? '更新岗位' : '创建岗位' }}
                </button>
              </div>
            </div>

            <div class="org-list-wrap">
              <h4>岗位层级</h4>
              <div v-if="positions.length > 0" class="org-tree">
                <div
                  v-for="item in flattenedPositions"
                  :key="item.node.id"
                  class="org-tree-row"
                  :class="{ 'org-tree-row--root': !item.prefix }"
                >
                  <span class="org-tree-prefix">{{ item.prefix }}</span>
                  <span class="org-tree-name">{{ item.node.positionName }}</span>
                  <span
                    class="org-level-badge"
                    :class="`level-${Math.min(item.node.levelNo || item.node.level_no || 1, 4)}`"
                  >L{{ item.node.levelNo || item.node.level_no || 1 }}</span>
                  <div class="org-tree-actions">
                    <button type="button" class="btn ghost btn-sm" @click="startEditPosition(item.node)">编辑</button>
                    <button type="button" class="btn danger btn-sm" @click="removePosition(item.node)">删除</button>
                  </div>
                </div>
              </div>
              <p v-else class="empty-text">暂无组织岗位，请先新增顶级岗位。</p>
            </div>
          </div>
        </section>

        <section v-show="activeMenu === 'recruit'" class="panel">
          <div class="section-head">
            <h3>招新动态设置</h3>
            <button type="button" class="btn" :disabled="recruitSaving" @click="saveRecruitConfig">
              {{ recruitSaving ? '保存中...' : '保存招新设置' }}
            </button>
          </div>

          <div class="form-grid">
            <label>
              招新开始时间
              <input v-model="recruitForm.recruitStartAt" type="datetime-local" />
            </label>
            <label>
              招新结束时间
              <input v-model="recruitForm.recruitEndAt" type="datetime-local" />
            </label>
            <label>
              招新人数上限
              <input v-model="recruitForm.recruitLimit" type="number" min="1" step="1" placeholder="例如 120" />
            </label>
            <label>
              招新入口状态
              <select v-model="recruitForm.recruitStatus">
                <option value="OPEN">开启</option>
                <option value="CLOSED">关闭</option>
              </select>
            </label>
          </div>
        </section>

        <!-- 经费概览 -->
        <section v-show="activeMenu === 'finance-overview'" class="panel">
          <div class="section-head">
            <h3>经费概览</h3>
            <button type="button" class="btn ghost" :disabled="balanceLoading" @click="loadMyBalance">
              {{ balanceLoading ? '加载中...' : '刷新余额' }}
            </button>
          </div>
          <div class="balance-card-group">
            <div class="balance-card">
              <span class="balance-label">当前余额</span>
              <span class="balance-value">{{ myBalance != null ? '¥ ' + Number(myBalance).toFixed(2) : '暂无数据' }}</span>
            </div>
            <div class="balance-card" v-if="myPendingExpense != null && Number(myPendingExpense) > 0">
              <span class="balance-label">待审核支出</span>
              <span class="balance-value" style="color:#d97706">¥ {{ Number(myPendingExpense).toFixed(2) }}</span>
            </div>
            <div class="balance-card">
              <span class="balance-label">可用余额</span>
              <span class="balance-value" :style="{ color: myAvailableBalance != null && Number(myAvailableBalance) <= 0 ? '#dc2626' : '#059669' }">
                {{ myAvailableBalance != null ? '¥ ' + Number(myAvailableBalance).toFixed(2) : '暂无数据' }}
              </span>
            </div>
          </div>
          <h4 style="margin-top:18px;margin-bottom:10px">经费记录</h4>
          <div class="member-table-wrap">
            <table class="member-table">
              <thead>
                <tr>
                  <th>类型</th>
                  <th>金额</th>
                  <th>状态</th>
                  <th>发生时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in financeRecords" :key="item.id">
                  <td>
                    <span :class="['cancel-status-tag', item.type === 'INCOME' ? 'done' : 'rejected']">
                      {{ item.typeName || (item.type === 'INCOME' ? '收入' : '支出') }}
                    </span>
                  </td>
                  <td :style="{ color: item.type === 'INCOME' ? '#059669' : '#dc2626', fontWeight: 600 }">
                    {{ item.type === 'INCOME' ? '+' : '-' }}{{ Number(item.amount || 0).toFixed(2) }}
                  </td>
                  <td>
                    <span :class="['cancel-status-tag', item.status === 'APPROVED' ? 'done' : item.status === 'REJECTED' ? 'rejected' : item.status === 'PAID' ? 'done' : 'pending']">
                      {{ item.status === 'APPROVED' ? '已通过' : item.status === 'REJECTED' ? '已驳回' : item.status === 'PAID' ? '已支付' : '待审核' }}
                    </span>
                  </td>
                  <td class="td-secondary">{{ item.occurAt ? item.occurAt.slice(0, 16).replace('T', ' ') : '-' }}</td>
                </tr>
                <tr v-if="financeRecords.length === 0">
                  <td colspan="4" class="td-empty">暂无经费记录</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- 收入录入 -->
        <section v-show="activeMenu === 'finance-income'" class="panel">
          <div class="section-head">
            <h3>收入录入</h3>
            <button type="button" class="btn ghost" :disabled="incomeLoading" @click="loadIncomes">
              {{ incomeLoading ? '加载中...' : '刷新列表' }}
            </button>
          </div>
          <div class="finance-form-card">
            <div class="form-grid">
              <label>
                收入类型
                <select v-model.number="incomeForm.incomeType">
                  <option :value="1">学校拨款</option>
                  <option :value="2">赞助收入</option>
                  <option :value="3">成员会费</option>
                </select>
              </label>
              <label>
                金额
                <input v-model="incomeForm.amount" type="number" min="0.01" step="0.01" placeholder="请输入金额" />
              </label>
              <label>
                发生时间
                <input v-model="incomeForm.occurAt" type="datetime-local" />
              </label>
              <label>
                来源说明
                <input v-model.trim="incomeForm.sourceDesc" type="text" placeholder="收入来源描述" />
              </label>
              <label class="full">
                凭证上传
                <input type="file" @change="e => incomeForm.proofFile = e.target.files?.[0] || null" />
              </label>
            </div>
            <div class="action-row" style="margin-top:12px">
              <button type="button" class="btn" :disabled="incomeSubmitting" @click="submitIncome">
                {{ incomeSubmitting ? '提交中...' : '提交收入' }}
              </button>
              <button type="button" class="btn ghost" @click="resetIncomeForm">重置</button>
            </div>
          </div>
          <h4 style="margin-top:18px;margin-bottom:10px">收入记录</h4>
          <div class="member-table-wrap">
            <table class="member-table">
              <thead>
                <tr>
                  <th>类型</th>
                  <th>金额</th>
                  <th>来源</th>
                  <th>发生时间</th>
                  <th>凭证</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in incomeList" :key="item.id">
                  <td>{{ INCOME_TYPE_MAP[item.incomeType] || item.incomeType || '-' }}</td>
                  <td style="color:#059669;font-weight:600">+{{ Number(item.amount || 0).toFixed(2) }}</td>
                  <td>{{ item.sourceDesc || '-' }}</td>
                  <td class="td-secondary">{{ item.occurAt ? item.occurAt.slice(0, 16).replace('T', ' ') : '-' }}</td>
                  <td>
                    <a v-if="item.proofUrl" :href="item.proofUrl" target="_blank" style="color:#0f766e;text-decoration:underline">查看</a>
                    <span v-else class="td-secondary">-</span>
                  </td>
                </tr>
                <tr v-if="incomeList.length === 0">
                  <td colspan="5" class="td-empty">暂无收入记录</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="incomePagination.total > incomePagination.pageSize" class="pagination-bar">
            <button type="button" class="btn ghost btn-sm" :disabled="incomePagination.pageNum <= 1" @click="incomePagination.pageNum--; loadIncomes()">上一页</button>
            <span class="pagination-info">第 {{ incomePagination.pageNum }} 页 / 共 {{ Math.ceil(incomePagination.total / incomePagination.pageSize) }} 页</span>
            <button type="button" class="btn ghost btn-sm" :disabled="incomePagination.pageNum >= Math.ceil(incomePagination.total / incomePagination.pageSize)" @click="incomePagination.pageNum++; loadIncomes()">下一页</button>
          </div>
        </section>

        <!-- 支出申请 -->
        <section v-show="activeMenu === 'finance-expense'" class="panel">
          <div class="section-head">
            <h3>支出申请</h3>
            <button type="button" class="btn ghost" :disabled="expenseLoading" @click="loadExpenses">
              {{ expenseLoading ? '加载中...' : '刷新列表' }}
            </button>
          </div>
          <div v-if="myAvailableBalance != null" class="available-balance-hint" :class="{ warn: Number(myAvailableBalance) <= 0 }">
            可用余额：¥ {{ Number(myAvailableBalance).toFixed(2) }}
            <span v-if="myPendingExpense != null && Number(myPendingExpense) > 0" style="color:#64748b">
              （余额 {{ Number(myBalance).toFixed(2) }} - 待审核 {{ Number(myPendingExpense).toFixed(2) }}）
            </span>
          </div>
          <div class="finance-form-card">
            <div class="form-grid">
              <label>
                支出类别
                <input v-model.trim="expenseForm.category" type="text" placeholder="如：场地租赁、物料采购" />
              </label>
              <label>
                金额
                <input v-model="expenseForm.amount" type="number" min="0.01" step="0.01" placeholder="请输入金额" />
                <span v-if="getExpenseAmountHint" class="amount-hint" :class="Number(expenseForm.amount) > 500 ? 'hint-warn' : 'hint-ok'">
                  {{ getExpenseAmountHint }}
                </span>
              </label>
              <label>
                收款人姓名
                <input v-model.trim="expenseForm.payeeName" type="text" placeholder="收款人姓名" />
              </label>
              <label>
                收款人账号
                <input v-model.trim="expenseForm.payeeAccount" type="text" placeholder="银行卡号/支付宝等" />
              </label>
              <label class="full">
                发票/凭证上传
                <input type="file" @change="e => expenseForm.invoiceFile = e.target.files?.[0] || null" />
              </label>
              <label class="full">
                支出说明
                <textarea v-model.trim="expenseForm.expenseDesc" rows="2" placeholder="详细说明支出用途"></textarea>
              </label>
            </div>
            <div class="action-row" style="margin-top:12px">
              <button type="button" class="btn" :disabled="expenseSubmitting" @click="submitExpense">
                {{ expenseSubmitting ? '提交中...' : '提交支出申请' }}
              </button>
              <button type="button" class="btn ghost" @click="resetExpenseForm">重置</button>
            </div>
          </div>
          <h4 style="margin-top:18px;margin-bottom:10px">支出记录</h4>
          <div class="member-filter-bar">
            <select v-model="expenseStatusFilter" class="filter-select" @change="expensePagination.pageNum = 1; loadExpenses()">
              <option value="">全部状态</option>
              <option value="PENDING">待审核</option>
              <option value="APPROVED">已通过</option>
              <option value="REJECTED">已驳回</option>
            </select>
          </div>
          <div class="member-table-wrap">
            <table class="member-table">
              <thead>
                <tr>
                  <th>类别</th>
                  <th>金额</th>
                  <th>收款人</th>
                  <th>状态</th>
                  <th>说明</th>
                  <th>凭证</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in expenseList" :key="item.id">
                  <td>{{ item.category || '-' }}</td>
                  <td style="color:#dc2626;font-weight:600">-{{ Number(item.amount || 0).toFixed(2) }}</td>
                  <td>{{ item.payeeName || '-' }}</td>
                  <td>
                    <span :class="['cancel-status-tag', item.expenseStatus === 2 ? 'done' : item.expenseStatus === 3 ? 'rejected' : 'pending']">
                      {{ item.expenseStatus === 2 ? '已通过' : item.expenseStatus === 3 ? '已驳回' : '待审核' }}
                    </span>
                    <div v-if="item.expenseStatus === 3 && item.rejectReason" class="reject-reason-text">
                      驳回原因：{{ item.rejectReason }}
                    </div>
                  </td>
                  <td class="td-secondary">{{ item.expenseDesc || '-' }}</td>
                  <td>
                    <a v-if="item.invoiceUrl" :href="item.invoiceUrl" target="_blank" style="color:#0f766e;text-decoration:underline">查看</a>
                    <span v-else class="td-secondary">-</span>
                  </td>
                  <td>
                    <button v-if="item.expenseStatus === 3" type="button" class="btn btn-sm" @click="openResubmitDialog(item)">修改重提</button>
                    <span v-else class="td-secondary">-</span>
                  </td>
                </tr>
                <tr v-if="expenseList.length === 0">
                  <td colspan="7" class="td-empty">暂无支出记录</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="expensePagination.total > expensePagination.pageSize" class="pagination-bar">
            <button type="button" class="btn ghost btn-sm" :disabled="expensePagination.pageNum <= 1" @click="expensePagination.pageNum--; loadExpenses()">上一页</button>
            <span class="pagination-info">第 {{ expensePagination.pageNum }} 页 / 共 {{ Math.ceil(expensePagination.total / expensePagination.pageSize) }} 页</span>
            <button type="button" class="btn ghost btn-sm" :disabled="expensePagination.pageNum >= Math.ceil(expensePagination.total / expensePagination.pageSize)" @click="expensePagination.pageNum++; loadExpenses()">下一页</button>
          </div>
        </section>

        <!-- 重新提交弹窗 -->
        <div v-if="resubmitDialogVisible" class="modal-mask" @click.self="resubmitDialogVisible = false">
          <section class="modal-dialog" style="max-width:520px">
            <div class="section-head" style="padding:16px 20px 0">
              <h3>修改并重新提交</h3>
              <button type="button" class="btn ghost btn-sm" @click="resubmitDialogVisible = false">×</button>
            </div>
            <div style="padding:12px 20px 20px">
              <div class="form-grid">
                <label>
                  支出类别
                  <input v-model.trim="resubmitForm.category" type="text" placeholder="如：场地租赁、物料采购" />
                </label>
                <label>
                  金额
                  <input v-model="resubmitForm.amount" type="number" min="0.01" step="0.01" placeholder="请输入金额" />
                  <span v-if="getResubmitAmountHint" class="amount-hint" :class="Number(resubmitForm.amount) > 500 ? 'hint-warn' : 'hint-ok'">
                    {{ getResubmitAmountHint }}
                  </span>
                </label>
                <label>
                  收款人姓名
                  <input v-model.trim="resubmitForm.payeeName" type="text" placeholder="收款人姓名" />
                </label>
                <label>
                  收款人账号
                  <input v-model.trim="resubmitForm.payeeAccount" type="text" placeholder="银行卡号/支付宝等" />
                </label>
                <label class="full">
                  发票/凭证上传（可重新上传）
                  <input type="file" @change="e => resubmitForm.invoiceFile = e.target.files?.[0] || null" />
                  <span v-if="resubmitForm.invoiceUrl && !resubmitForm.invoiceFile" class="td-secondary" style="font-size:12px">
                    已有凭证：<a :href="resubmitForm.invoiceUrl" target="_blank" style="color:#0f766e">查看</a>
                  </span>
                </label>
                <label class="full">
                  支出说明
                  <textarea v-model.trim="resubmitForm.expenseDesc" rows="2" placeholder="详细说明支出用途"></textarea>
                </label>
              </div>
              <div class="action-row" style="margin-top:14px;justify-content:flex-end">
                <button type="button" class="btn ghost" @click="resubmitDialogVisible = false">取消</button>
                <button type="button" class="btn" :disabled="resubmitLoading" @click="submitResubmit">
                  {{ resubmitLoading ? '提交中...' : '重新提交' }}
                </button>
              </div>
            </div>
          </section>
        </div>

        <!-- 余额不足弹窗 -->
        <div v-if="balanceAlertVisible" class="modal-mask" @click.self="balanceAlertVisible = false">
          <section class="modal-dialog" style="max-width:420px">
            <div class="section-head" style="padding:16px 20px 0">
              <h3 style="color:#dc2626">余额不足</h3>
              <button type="button" class="btn ghost btn-sm" @click="balanceAlertVisible = false">×</button>
            </div>
            <div style="padding:16px 20px 20px">
              <p style="line-height:1.8;color:#334155">{{ balanceAlertMessage }}</p>
              <div class="action-row" style="margin-top:16px;justify-content:flex-end">
                <button type="button" class="btn" @click="balanceAlertVisible = false">知道了</button>
              </div>
            </div>
          </section>
        </div>

        <!-- 台账明细 -->
        <section v-show="activeMenu === 'finance-ledger'" class="panel">
          <div class="section-head">
            <h3>台账明细</h3>
            <button type="button" class="btn ghost" :disabled="ledgerLoading" @click="loadLedger">
              {{ ledgerLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <div class="member-filter-bar">
            <select v-model="ledgerFilter.bizType" class="filter-select">
              <option value="">全部类型</option>
              <option value="1">收入</option>
              <option value="2">支出</option>
            </select>
            <input v-model="ledgerFilter.startTime" type="date" class="filter-input" style="min-width:140px;flex:0" placeholder="开始日期" />
            <input v-model="ledgerFilter.endTime" type="date" class="filter-input" style="min-width:140px;flex:0" placeholder="结束日期" />
            <button type="button" class="btn btn-sm" @click="ledgerPagination.pageNum = 1; loadLedger()">查询</button>
            <button
              v-if="ledgerFilter.bizType || ledgerFilter.startTime || ledgerFilter.endTime"
              type="button"
              class="btn ghost btn-sm"
              @click="ledgerFilter.bizType = ''; ledgerFilter.startTime = ''; ledgerFilter.endTime = ''; ledgerPagination.pageNum = 1; loadLedger()"
            >重置</button>
          </div>
          <div class="member-table-wrap">
            <table class="member-table">
              <thead>
                <tr>
                  <th>时间</th>
                  <th>类型</th>
                  <th>变动金额</th>
                  <th>变动后余额</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in ledgerList" :key="item.id">
                  <td class="td-secondary">{{ item.occurAt ? item.occurAt.slice(0, 16).replace('T', ' ') : '-' }}</td>
                  <td>
                    <span :class="['cancel-status-tag', item.bizType === 1 ? 'done' : 'rejected']">
                      {{ item.bizTypeName || (item.bizType === 1 ? '收入' : '支出') }}
                    </span>
                  </td>
                  <td :style="{ color: Number(item.changeAmount) >= 0 ? '#059669' : '#dc2626', fontWeight: 600 }">
                    {{ Number(item.changeAmount) >= 0 ? '+' : '' }}{{ Number(item.changeAmount || 0).toFixed(2) }}
                  </td>
                  <td style="font-weight:500">{{ Number(item.balanceAfter || 0).toFixed(2) }}</td>
                </tr>
                <tr v-if="ledgerList.length === 0">
                  <td colspan="4" class="td-empty">暂无台账记录</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="ledgerPagination.total > ledgerPagination.pageSize" class="pagination-bar">
            <button type="button" class="btn ghost btn-sm" :disabled="ledgerPagination.pageNum <= 1" @click="ledgerPagination.pageNum--; loadLedger()">上一页</button>
            <span class="pagination-info">第 {{ ledgerPagination.pageNum }} 页 / 共 {{ Math.ceil(ledgerPagination.total / ledgerPagination.pageSize) }} 页</span>
            <button type="button" class="btn ghost btn-sm" :disabled="ledgerPagination.pageNum >= Math.ceil(ledgerPagination.total / ledgerPagination.pageSize)" @click="ledgerPagination.pageNum++; loadLedger()">下一页</button>
          </div>
        </section>

        <section v-show="activeMenu === 'cancel'" class="panel">
          <div class="section-head">
            <h3>社团注销申请</h3>
            <button type="button" class="btn ghost" :disabled="cancelListLoading" @click="loadMyCancelApplies">
              {{ cancelListLoading ? '加载中...' : '刷新记录' }}
            </button>
          </div>

          <div class="cancel-form">
            <label>
              注销原因
              <textarea
                v-model="cancelForm.applyReason"
                rows="3"
                maxlength="500"
                placeholder="请填写注销原因（最多500字）"
              />
            </label>
            <label>
              资产清算报告
              <input type="file" @change="handleAssetSettlementFileChange" />
            </label>
            <button type="button" class="btn danger-outline" :disabled="cancelSubmitting" @click="submitClubCancelApply">
              {{ cancelSubmitting ? '提交中...' : '提交社团注销申请' }}
            </button>
          </div>

          <div class="cancel-history">
            <h4>我的注销申请进度</h4>
            <ul v-if="myCancelApplies.length > 0" class="card-grid">
              <li v-for="record in myCancelApplies" :key="record.id || record.cancelId" class="card-item">
                <h4>{{ record.clubName || clubProfile.clubName || '-' }}</h4>
                <p>
                  审批进度：
                  <span :class="['cancel-status-tag', getCancelStatusClass(record.cancelStatus)]">
                    {{ getCancelStatusText(record.cancelStatus) }}
                  </span>
                </p>
                <p>申请原因：{{ record.applyReason || record.reason || '-' }}</p>
                <p>提交时间：{{ record.createdAt || record.submittedAt || '-' }}</p>
                <a
                  class="btn ghost"
                  :class="{ disabled: !(record.assetSettlementUrl || record.assetSettlementFileUrl) }"
                  :href="record.assetSettlementUrl || record.assetSettlementFileUrl || undefined"
                  target="_blank"
                  rel="noopener noreferrer"
                  download
                >
                  下载资产清算报告
                </a>
              </li>
            </ul>
            <p v-else class="empty-text">暂无社团注销申请记录</p>
          </div>
        </section>
      </main>
    </div>

    <!-- 入社申请详情弹窗 -->
    <div v-if="joinDetailDialog.visible" class="modal-overlay" @click.self="joinDetailDialog.visible = false">
      <div class="modal-box">
        <div class="modal-head">
          <h3>申请详情</h3>
          <button type="button" class="modal-close" @click="joinDetailDialog.visible = false">×</button>
        </div>
        <div class="modal-content">
          <div class="detail-row"><span class="detail-label">姓名</span><span>{{ joinDetailDialog.record?.realName || '-' }}</span></div>
          <div class="detail-row"><span class="detail-label">学号/账号</span><span>{{ joinDetailDialog.record?.username || '-' }}</span></div>
          <div class="detail-row">
            <span class="detail-label">申请状态</span>
            <span :class="['cancel-status-tag', getJoinApplyStatusClass(joinDetailDialog.record?.joinStatus)]">
              {{ getJoinApplyStatusText(joinDetailDialog.record?.joinStatus) }}
            </span>
          </div>
          <div class="detail-row"><span class="detail-label">申请时间</span><span>{{ joinDetailDialog.record?.createdAt || '-' }}</span></div>
          <div class="detail-row detail-row--block">
            <span class="detail-label">个人简介</span>
            <p class="detail-text">{{ joinDetailDialog.record?.selfIntro || '未填写' }}</p>
          </div>
          <div class="detail-row detail-row--block">
            <span class="detail-label">入社理由</span>
            <p class="detail-text">{{ joinDetailDialog.record?.applyReason || '未填写' }}</p>
          </div>
          <div v-if="normalizeJoinApplyStatus(joinDetailDialog.record?.joinStatus) === 'REJECTED'" class="detail-row detail-row--block">
            <span class="detail-label">驳回原因</span>
            <p class="detail-text">{{ joinDetailDialog.record?.rejectReason || '-' }}</p>
          </div>
        </div>
        <div class="modal-foot">
          <template v-if="normalizeJoinApplyStatus(joinDetailDialog.record?.joinStatus) === 'PENDING'">
            <button
              type="button"
              class="btn"
              :disabled="joinDecisionLoadingMap[String(joinDetailDialog.record?.id)]"
              @click="decideJoinApply(joinDetailDialog.record, 'APPROVE'); joinDetailDialog.visible = false"
            >通过</button>
            <button
              type="button"
              class="btn danger"
              :disabled="joinDecisionLoadingMap[String(joinDetailDialog.record?.id)]"
              @click="decideJoinApply(joinDetailDialog.record, 'REJECT'); joinDetailDialog.visible = false"
            >驳回</button>
          </template>
          <button type="button" class="btn ghost" @click="joinDetailDialog.visible = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 发起活动弹窗 -->
    <div v-if="eventCreateDialogVisible" class="modal-overlay" @click.self="eventCreateDialogVisible = false">
      <div class="modal-box" style="width:min(600px,100%)">
        <div class="modal-head"><h3>发起活动</h3><button type="button" class="modal-close" @click="eventCreateDialogVisible = false">×</button></div>
        <div class="modal-content" style="max-height:65vh;overflow-y:auto">
          <p v-if="eventFormError" class="form-error">{{ eventFormError }}</p>
          <div class="detail-row detail-row--block"><span class="detail-label">活动标题 *</span><input v-model="eventForm.title" class="form-input" placeholder="活动标题" /></div>
          <div class="detail-row detail-row--block"><span class="detail-label">活动内容</span><textarea v-model="eventForm.content" class="form-input" rows="3" placeholder="活动详细说明"></textarea></div>
          <div class="detail-row detail-row--block"><span class="detail-label">活动地点 *</span><input v-model="eventForm.location" class="form-input" placeholder="活动地点" /></div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
            <div class="detail-row detail-row--block"><span class="detail-label">开始时间 *</span><input v-model="eventForm.startAt" type="datetime-local" class="form-input" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">结束时间 *</span><input v-model="eventForm.endAt" type="datetime-local" class="form-input" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">报名开始</span><input v-model="eventForm.signupStartAt" type="datetime-local" class="form-input" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">报名截止</span><input v-model="eventForm.signupEndAt" type="datetime-local" class="form-input" /></div>
          </div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
            <div class="detail-row detail-row--block"><span class="detail-label">人数上限 *</span><input v-model.number="eventForm.limitCount" type="number" class="form-input" min="1" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">仅社团成员</span><select v-model="eventForm.onlyMember" class="form-input"><option :value="false">否</option><option :value="true">是</option></select></div>
          </div>
          <div class="detail-row detail-row--block"><span class="detail-label">安全预案</span><input type="file" @change="e => eventForm.safetyPlanFile = e.target.files?.[0] || null" /></div>
        </div>
        <div class="modal-foot">
          <button type="button" class="btn ghost" @click="eventCreateDialogVisible = false">取消</button>
          <button type="button" class="btn" :disabled="eventSubmitting" @click="handleCreateEvent">{{ eventSubmitting ? '提交中...' : '提交审核' }}</button>
        </div>
      </div>
    </div>

    <!-- 活动详情弹窗 -->
    <div v-if="eventDetailDialog.visible" class="modal-overlay" @click.self="eventDetailDialog.visible = false">
      <div class="modal-box" style="width:min(560px,100%)">
        <div class="modal-head"><h3>活动详情</h3><button type="button" class="modal-close" @click="eventDetailDialog.visible = false">×</button></div>
        <div class="modal-content">
          <div class="detail-row"><span class="detail-label">标题</span><span>{{ eventDetailDialog.event?.title }}</span></div>
          <div class="detail-row"><span class="detail-label">状态</span><span :class="['cancel-status-tag', getEventStatusClass(eventDetailDialog.event?.eventStatus)]">{{ getEventStatusText(eventDetailDialog.event?.eventStatus) }}</span></div>
          <div class="detail-row"><span class="detail-label">地点</span><span>{{ eventDetailDialog.event?.location }}</span></div>
          <div class="detail-row"><span class="detail-label">活动时间</span><span>{{ eventDetailDialog.event?.startAt?.slice(0,16)?.replace('T',' ') }} 至 {{ eventDetailDialog.event?.endAt?.slice(0,16)?.replace('T',' ') }}</span></div>
          <div class="detail-row"><span class="detail-label">报名时间</span><span>{{ eventDetailDialog.event?.signupStartAt?.slice(0,16)?.replace('T',' ') || '-' }} 至 {{ eventDetailDialog.event?.signupEndAt?.slice(0,16)?.replace('T',' ') || '-' }}</span></div>
          <div class="detail-row"><span class="detail-label">人数上限</span><span>{{ eventDetailDialog.event?.limitCount || '不限' }}</span></div>
          <div class="detail-row"><span class="detail-label">报名/签到</span><span>{{ eventDetailDialog.event?.signupCount ?? 0 }} / {{ eventDetailDialog.event?.checkinCount ?? 0 }}</span></div>
          <div v-if="eventDetailDialog.event?.content" class="detail-row detail-row--block"><span class="detail-label">活动内容</span><p class="detail-text">{{ eventDetailDialog.event.content }}</p></div>
          <div v-if="eventDetailDialog.event?.safetyPlanUrl" class="detail-row"><span class="detail-label">安全预案</span><a :href="eventDetailDialog.event.safetyPlanUrl" target="_blank">下载查看</a></div>
          <div v-if="eventDetailDialog.event?.checkinCode" class="detail-row"><span class="detail-label">签到码</span><span style="font-weight:600;color:#10b981">{{ eventDetailDialog.event.checkinCode }}</span></div>
          <div v-if="eventDetailDialog.event?.eventStatus === 5" class="detail-row detail-row--block"><span class="detail-label">驳回原因</span><p class="detail-text" style="color:#ef4444">{{ eventDetailDialog.event.rejectReason || '-' }}</p></div>
        </div>
        <div class="modal-foot">
          <button type="button" class="btn ghost" @click="eventDetailDialog.visible = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 修改并重新提交弹窗 -->
    <div v-if="eventEditDialogVisible" class="modal-overlay" @click.self="eventEditDialogVisible = false">
      <div class="modal-box" style="width:min(600px,100%)">
        <div class="modal-head"><h3>修改活动并重新提交</h3><button type="button" class="modal-close" @click="eventEditDialogVisible = false">×</button></div>
        <div class="modal-content" style="max-height:65vh;overflow-y:auto">
          <p v-if="eventFormError" class="form-error">{{ eventFormError }}</p>
          <div class="detail-row detail-row--block"><span class="detail-label">活动标题 *</span><input v-model="eventForm.title" class="form-input" placeholder="活动标题" /></div>
          <div class="detail-row detail-row--block"><span class="detail-label">活动内容</span><textarea v-model="eventForm.content" class="form-input" rows="3" placeholder="活动详细说明"></textarea></div>
          <div class="detail-row detail-row--block"><span class="detail-label">活动地点 *</span><input v-model="eventForm.location" class="form-input" placeholder="活动地点" /></div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
            <div class="detail-row detail-row--block"><span class="detail-label">开始时间 *</span><input v-model="eventForm.startAt" type="datetime-local" class="form-input" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">结束时间 *</span><input v-model="eventForm.endAt" type="datetime-local" class="form-input" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">报名开始</span><input v-model="eventForm.signupStartAt" type="datetime-local" class="form-input" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">报名截止</span><input v-model="eventForm.signupEndAt" type="datetime-local" class="form-input" /></div>
          </div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
            <div class="detail-row detail-row--block"><span class="detail-label">人数上限 *</span><input v-model.number="eventForm.limitCount" type="number" class="form-input" min="1" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">仅社团成员</span><select v-model="eventForm.onlyMember" class="form-input"><option :value="false">否</option><option :value="true">是</option></select></div>
          </div>
          <div class="detail-row detail-row--block"><span class="detail-label">安全预案</span><input type="file" @change="e => eventForm.safetyPlanFile = e.target.files?.[0] || null" /></div>
        </div>
        <div class="modal-foot">
          <button type="button" class="btn ghost" @click="eventEditDialogVisible = false">取消</button>
          <button type="button" class="btn" :disabled="eventSubmitting" @click="handleResubmitEvent(eventEditId)">{{ eventSubmitting ? '提交中...' : '重新提交审核' }}</button>
        </div>
      </div>
    </div>

    <!-- 报名/签到列表弹窗 -->
    <div v-if="eventSignupDialog.visible" class="modal-overlay" @click.self="eventSignupDialog.visible = false">
      <div class="modal-box" style="width:min(640px,100%)">
        <div class="modal-head"><h3>{{ eventSignupDialog.eventTitle }} - 报名/签到</h3><button type="button" class="modal-close" @click="eventSignupDialog.visible = false">×</button></div>
        <div class="modal-content" style="max-height:55vh;overflow-y:auto">
          <table v-if="eventSignupList.length > 0" class="apply-table">
            <thead><tr><th>姓名</th><th>学号</th><th>报名时间</th><th>签到</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="s in eventSignupList" :key="s.id">
                <td>{{ s.realName || '-' }}</td>
                <td>{{ s.studentNo || '-' }}</td>
                <td>{{ s.signupAt?.slice(0,16)?.replace('T',' ') || '-' }}</td>
                <td><span :class="['cancel-status-tag', s.checkedIn ? 'done' : 'pending']">{{ s.checkedIn ? '已签到' : '未签到' }}</span></td>
                <td>
                  <button v-if="!s.checkedIn" type="button" class="btn sm" :disabled="checkinLoadingMap[String(s.userId)]" @click="handleCheckin(s)">签到</button>
                  <button v-else type="button" class="btn sm ghost" :disabled="checkinLoadingMap[String(s.userId)]" @click="handleCancelCheckin(s)">取消签到</button>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="empty-text">暂无报名记录</p>
        </div>
        <div class="modal-foot"><button type="button" class="btn ghost" @click="eventSignupDialog.visible = false">关闭</button></div>
      </div>
    </div>

    <!-- 活动总结弹窗 -->
    <div v-if="eventSummaryDialog.visible" class="modal-overlay" @click.self="eventSummaryDialog.visible = false">
      <div class="modal-box" style="width:min(560px,100%)">
        <div class="modal-head"><h3>活动总结</h3><button type="button" class="modal-close" @click="eventSummaryDialog.visible = false">×</button></div>
        <div class="modal-content">
          <template v-if="eventSummaryDialog.mode === 'view' && summaryData">
            <div class="detail-row detail-row--block"><span class="detail-label">总结内容</span><p class="detail-text">{{ summaryData.summaryText }}</p></div>
            <div v-if="summaryData.summaryImages && summaryData.summaryImages.length > 0" class="detail-row detail-row--block">
              <span class="detail-label">活动图片</span>
              <div style="display:flex;flex-wrap:wrap;gap:8px;margin-top:8px">
                <img v-for="(img, idx) in summaryData.summaryImages" :key="idx" :src="img" style="width:120px;height:120px;object-fit:cover;border-radius:4px;cursor:pointer;border:2px solid #e5e7eb" @click="window.open(img, '_blank')" alt="活动图片" />
              </div>
            </div>
            <div class="detail-row"><span class="detail-label">反馈评分</span><span>{{ summaryData.feedbackScore ?? '-' }}</span></div>
            <div v-if="summaryData.issueReflection" class="detail-row detail-row--block"><span class="detail-label">问题反思</span><p class="detail-text">{{ summaryData.issueReflection }}</p></div>
            <div v-if="summaryData.attachmentUrl" class="detail-row detail-row--block">
              <span class="detail-label">附件</span>
              <a :href="summaryData.attachmentUrl" target="_blank" style="color:#0f766e;text-decoration:underline">下载附件</a>
            </div>
          </template>
          <template v-else>
            <div class="detail-row detail-row--block"><span class="detail-label">总结内容 *</span><textarea v-model="summaryForm.summaryText" class="form-input" rows="4" placeholder="活动图文回顾"></textarea></div>
            <div class="detail-row detail-row--block">
              <span class="detail-label">活动图片</span>
              <div style="margin-top:4px">
                <input ref="imageFileInput" type="file" accept="image/*" multiple @change="handleImageSelect" style="display:none" />
                <button type="button" class="btn sm" @click="$refs.imageFileInput.click()">选择图片</button>
                <span v-if="summaryForm.summaryImages.length > 0 || summaryForm.imageFiles.length > 0" style="margin-left:8px;font-size:12px;color:#0f766e;font-weight:500">
                  已选择 {{ summaryForm.summaryImages.length + summaryForm.imageFiles.length }} 张
                </span>
              </div>
              <p style="font-size:12px;color:#6b7280;margin-top:4px">支持 jpg、png、gif 等格式，可多选</p>
              <div v-if="summaryForm.summaryImages.length > 0 || summaryForm.imageFiles.length > 0" style="display:flex;flex-wrap:wrap;gap:8px;margin-top:8px">
                <div v-for="(img, idx) in summaryForm.summaryImages" :key="'old-'+idx" style="position:relative;width:100px;height:100px">
                  <img :src="img" style="width:100%;height:100%;object-fit:cover;border-radius:4px;border:2px solid #e5e7eb" alt="已上传图片" />
                  <button type="button" @click="removeImage(idx)" style="position:absolute;top:2px;right:2px;width:20px;height:20px;border-radius:50%;background:#ef4444;color:#fff;border:none;cursor:pointer;font-size:12px;line-height:1;display:flex;align-items:center;justify-content:center" title="删除">×</button>
                </div>
                <div v-for="(file, idx) in summaryForm.imageFiles" :key="'new-'+idx" style="position:relative;width:100px;height:100px">
                  <img :src="getImagePreviewUrl(file)" style="width:100%;height:100%;object-fit:cover;border-radius:4px;border:2px solid #10b981" alt="待上传图片" />
                  <button type="button" @click="removeImage(summaryForm.summaryImages.length + idx)" style="position:absolute;top:2px;right:2px;width:20px;height:20px;border-radius:50%;background:#ef4444;color:#fff;border:none;cursor:pointer;font-size:12px;line-height:1;display:flex;align-items:center;justify-content:center" title="删除">×</button>
                </div>
              </div>
            </div>
            <div class="detail-row detail-row--block"><span class="detail-label">反馈评分（1-5）</span><input v-model="summaryForm.feedbackScore" type="number" min="1" max="5" step="0.1" class="form-input" placeholder="请输入1-5之间的评分" /></div>
            <div class="detail-row detail-row--block"><span class="detail-label">问题反思</span><textarea v-model="summaryForm.issueReflection" class="form-input" rows="3" placeholder="问题反思与改进"></textarea></div>
            <div class="detail-row detail-row--block">
              <span class="detail-label">附件</span>
              <input type="file" accept=".pdf,.doc,.docx" @change="e => summaryForm.attachmentFile = e.target.files?.[0] || null" />
              <p style="font-size:12px;color:#6b7280;margin-top:4px">支持 pdf、doc、docx 格式</p>
              <p v-if="summaryForm.attachmentUrl && !summaryForm.attachmentFile" style="font-size:12px;color:#0f766e;margin-top:4px">
                已有附件：<a :href="summaryForm.attachmentUrl" target="_blank" style="text-decoration:underline">查看</a>
              </p>
            </div>
          </template>
        </div>
        <div class="modal-foot">
          <template v-if="eventSummaryDialog.mode === 'view' && summaryData">
            <button type="button" class="btn" @click="eventSummaryDialog.mode = 'edit'; summaryForm.summaryText = summaryData.summaryText || ''; summaryForm.feedbackScore = summaryData.feedbackScore || ''; summaryForm.issueReflection = summaryData.issueReflection || ''; summaryForm.attachmentUrl = summaryData.attachmentUrl || ''; summaryForm.summaryImages = summaryData.summaryImages || []; summaryForm.imageFiles = []">编辑</button>
          </template>
          <template v-else>
            <button type="button" class="btn" :disabled="summarySubmitting" @click="handleSubmitSummary">{{ summarySubmitting ? '提交中...' : '提交' }}</button>
          </template>
          <button type="button" class="btn ghost" @click="eventSummaryDialog.visible = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 设置签到码弹窗 -->
    <div v-if="checkinCodeDialog.visible" class="modal-overlay" @click.self="checkinCodeDialog.visible = false">
      <div class="modal-box" style="width:min(420px,100%)">
        <div class="modal-head"><h3>设置签到码</h3><button type="button" class="modal-close" @click="checkinCodeDialog.visible = false">×</button></div>
        <div class="modal-content">
          <p style="margin-bottom:12px;color:#6b7280">活动：{{ checkinCodeDialog.eventTitle }}</p>
          <div class="detail-row detail-row--block">
            <span class="detail-label">签到码</span>
            <input v-model="checkinCodeForm.checkinCode" class="form-input" placeholder="输入签到码（留空则清除）" maxlength="20" />
          </div>
          <p style="margin-top:8px;font-size:13px;color:#6b7280">学生可使用此签到码进行签到</p>
        </div>
        <div class="modal-foot">
          <button type="button" class="btn" :disabled="checkinCodeSubmitting" @click="handleUpdateCheckinCode">{{ checkinCodeSubmitting ? '保存中...' : '保存' }}</button>
          <button type="button" class="btn ghost" @click="checkinCodeDialog.visible = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  --bg-top: #ecfdf5;
  --bg-bottom: #f8fafc;
  --panel: #ffffff;
  --line: #d6e2db;
  --text-main: #0f2f2b;
  --text-sub: #41615b;
  --brand: #0f766e;
  --danger: #b91c1c;

  min-height: 100vh;
  background: linear-gradient(170deg, var(--bg-top), var(--bg-bottom));
  padding: 18px;
}

.layout-shell {
  width: min(1240px, calc(100vw - 24px));
  margin: 0 auto;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 16px;
}

.left-menu {
  border: 1px solid #d5e8df;
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
  border-bottom: 1px solid #ecfdf5;
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
  cursor: pointer;
  padding: 0 12px;
  font-size: 14px;
  text-align: left;
  transition: background 0.15s, color 0.15s;
  width: 100%;
}

.menu-item:hover {
  background: #f0fdf8;
}

.menu-item.active {
  background: #e9fbee;
  color: #0f766e;
  font-weight: 600;
}

.menu-item-danger {
  color: #b91c1c;
}

.menu-item-danger:hover {
  background: #fff1f2;
  color: #b91c1c;
}

.menu-item-danger.active {
  background: #fff1f2;
  color: #b91c1c;
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
  background: #f0fdf8;
}

.menu-group-btn.group-active {
  color: #0f766e;
}

.menu-arrow {
  font-size: 11px;
  color: #94a3b8;
}

.submenu {
  padding-left: 8px;
  border-left: 2px solid #99f6e4;
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
  cursor: pointer;
  text-align: left;
  padding: 0 10px;
  font-size: 13px;
  width: 100%;
  transition: background 0.15s, color 0.15s;
}

.submenu-item:hover {
  background: #f0fdf8;
  color: #334155;
}

.submenu-item.active {
  background: #e9fbee;
  color: #0f766e;
  font-weight: 600;
}

.menu-divider {
  height: 1px;
  background: #f0f4f8;
  margin: 6px 4px;
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

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.full {
  grid-column: 1 / -1;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: var(--text-main);
}

input,
textarea,
select {
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 14px;
}

.readonly-field {
  background: #f8fafc;
  color: #475569;
  cursor: not-allowed;
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
  background: #ffffff;
}

.card-item p {
  margin: 8px 0;
  font-size: 13px;
  color: var(--text-sub);
}

.member-position-select {
  min-width: 180px;
  height: 36px;
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 0 10px;
  background: #fff;
  color: var(--text-main);
}

.org-layout {
  display: grid;
  grid-template-columns: minmax(280px, 360px) 1fr;
  gap: 14px;
}

.org-form-wrap,
.org-list-wrap {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.org-tree {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.org-tree-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px;
  border-radius: 8px;
  transition: background 0.12s;
}

.org-tree-row:hover {
  background: #f8fafc;
}

.org-tree-row--root {
  background: #f1f5f9;
  border-radius: 10px;
  padding: 8px 12px;
}

.org-tree-row--root:hover {
  background: #e9f0f7;
}

.org-tree-prefix {
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  color: #94a3b8;
  white-space: pre;
  letter-spacing: 0;
  flex-shrink: 0;
}

.org-tree-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
  flex: 1;
}

.org-level-badge {
  display: inline-flex;
  align-items: center;
  padding: 1px 7px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
}

.org-level-badge.level-1 {
  background: #dbeafe;
  color: #1d4ed8;
}

.org-level-badge.level-2 {
  background: #d1fae5;
  color: #065f46;
}

.org-level-badge.level-3 {
  background: #fef3c7;
  color: #92400e;
}

.org-level-badge.level-4 {
  background: #f3e8ff;
  color: #6b21a8;
}

.org-tree-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.cancel-form {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 10px;
  margin-bottom: 14px;
  align-items: end;
}

.cancel-form label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: var(--text-main);
  font-size: 13px;
}

.cancel-history h4 {
  margin: 0 0 10px;
}

.cancel-status-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
}

.cancel-status-tag.pending {
  color: #334155;
  background: #e2e8f0;
}

.cancel-status-tag.done {
  color: #166534;
  background: #dcfce7;
}

.cancel-status-tag.rejected {
  color: #b91c1c;
  background: #fee2e2;
}

.empty-text {
  font-size: 13px;
  color: #64748b;
}

.action-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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

.danger {
  background: var(--danger);
}

.danger-outline {
  border: 1px solid #fecaca;
  color: var(--danger);
  background: #fff1f2;
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

.member-filter-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
  padding: 10px 12px;
  background: #f8fafc;
  border: 1px solid var(--line);
  border-radius: 10px;
}

.filter-input {
  height: 34px;
  min-width: 180px;
  flex: 1;
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 0 10px;
  font-size: 13px;
}

.filter-select {
  height: 34px;
  min-width: 120px;
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 0 8px;
  font-size: 13px;
  background: #fff;
}

.filter-result-tip {
  font-size: 12px;
  color: #64748b;
  white-space: nowrap;
}

.member-count {
  font-size: 13px;
  color: #64748b;
}

.member-count strong {
  color: var(--text-main);
}

.member-table-wrap {
  overflow-x: auto;
  border: 1px solid var(--line);
  border-radius: 12px;
}

.member-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.member-table thead tr {
  background: #f8fafc;
  border-bottom: 1px solid var(--line);
}

.member-table th {
  padding: 10px 14px;
  text-align: left;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  white-space: nowrap;
}

.th-seq {
  width: 40px;
  text-align: center;
}

.member-table td {
  padding: 10px 14px;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}

.member-table tbody tr:last-child td {
  border-bottom: none;
}

.member-table tbody tr:hover {
  background: #f8fbff;
}

.td-seq {
  text-align: center;
  color: #94a3b8;
  font-size: 12px;
  width: 40px;
}

.td-secondary {
  color: #64748b;
  font-size: 13px;
}

.td-empty {
  text-align: center;
  color: #94a3b8;
  padding: 32px !important;
  font-size: 13px;
}

/* 成员单元格：姓名/学号 */
.member-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.member-name {
  font-weight: 600;
  color: var(--text-main);
  font-size: 14px;
  line-height: 1.3;
}

.member-username {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.3;
}

/* 职位 badge — 按层级着色 */
.position-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 9px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.pos-level-0 {
  background: #f1f5f9;
  color: #64748b;
}

.pos-level-1 {
  background: #dbeafe;
  color: #1d4ed8;
}

.pos-level-2 {
  background: #d1fae5;
  color: #065f46;
}

.pos-level-3 {
  background: #fef3c7;
  color: #92400e;
}

.pos-level-4 {
  background: #f3e8ff;
  color: #6b21a8;
}

.btn-sm {
  height: 30px;
  padding: 0 12px;
  font-size: 13px;
}

@media (max-width: 820px) {
  .layout-shell {
    grid-template-columns: 1fr;
  }

  .content-head h1 {
    font-size: 24px;
  }

  .btn {
    width: 100%;
  }

  .cancel-form {
    grid-template-columns: 1fr;
  }

  .org-layout {
    grid-template-columns: 1fr;
  }

  .org-tree-row {
    flex-wrap: wrap;
  }

  .org-tree-prefix {
    display: none;
  }
}

/* 入社申请列表表格 */
.apply-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.apply-table th,
.apply-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid #f0f4f8;
}

.apply-table th {
  font-weight: 600;
  color: #64748b;
  background: #f8fafc;
  font-size: 13px;
}

.apply-table tbody tr:hover {
  background: #f8fafc;
}

.apply-table-actions {
  display: flex;
  gap: 6px;
  align-items: center;
  flex-wrap: wrap;
}

.btn.sm {
  padding: 4px 10px;
  font-size: 13px;
  height: auto;
}

/* 详情弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 16px;
}

.modal-box {
  background: #fff;
  border-radius: 14px;
  width: min(480px, 100%);
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.18);
  display: flex;
  flex-direction: column;
}

.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px 12px;
  border-bottom: 1px solid #f0f4f8;
}

.modal-head h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.modal-close {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.modal-close:hover {
  background: #e2e8f0;
}

.modal-content {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.modal-foot {
  padding: 12px 20px;
  border-top: 1px solid #f0f4f8;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.detail-row--block {
  align-items: flex-start;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-weight: 500;
  color: #64748b;
  min-width: 72px;
  flex-shrink: 0;
}

.detail-text {
  margin: 0;
  color: #334155;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

.form-input {
  width: 100%;
  border: 1px solid #dbe7f3;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  color: #0f172a;
  box-sizing: border-box;
  font-family: inherit;
}
.form-input:focus {
  outline: none;
  border-color: #10b981;
}

.form-error {
  margin: 8px 0 0;
  padding: 8px 12px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  color: #dc2626;
  font-size: 13px;
}

/* 经费管理 */
.balance-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 20px 24px;
  border: 1px solid #99f6e4;
  border-radius: 12px;
  background: linear-gradient(135deg, #ecfdf5, #f0fdfa);
}

.balance-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.balance-value {
  font-size: 28px;
  font-weight: 700;
  color: #0f766e;
}

.finance-form-card {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 16px;
  background: #f8fafc;
}

.amount-hint {
  font-size: 12px;
  margin-top: 2px;
}

.amount-hint.hint-ok {
  color: #059669;
}

.amount-hint.hint-warn {
  color: #d97706;
}

.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 12px;
  padding: 8px 0;
}

.pagination-info {
  font-size: 13px;
  color: #64748b;
}

.modal-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.35);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-dialog {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.reject-reason-text {
  margin-top: 4px;
  font-size: 12px;
  color: #b91c1c;
  line-height: 1.4;
}

.balance-card-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.balance-card-group .balance-card {
  flex: 1;
  min-width: 160px;
}

.available-balance-hint {
  padding: 8px 14px;
  margin-bottom: 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #059669;
  background: #ecfdf5;
}

.available-balance-hint.warn {
  color: #dc2626;
  background: #fef2f2;
}
</style>
