<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../../stores/auth'
import {
  getClubPublicListApi,
  getMyJoinClubApplyListApi,
  getMyJoinedClubsApi,
  getMyClubApplyListApi,
  getOpenEventsApi,
  getEventDetailApi,
  cancelEventSignupApi,
  checkinByCodeApi,
  joinClubApi,
  signupEventApi,
  submitClubApplyApi,
  uploadClubApplyMaterialApi,
  getProfileApi,
  updateBasicInfoApi,
  updatePasswordApi,
  updateContactApi,
} from '../../../api/user-permission'

const router = useRouter()
const authStore = useAuthStore()
const clubs = ref([])
const events = ref([])
const eventQuery = reactive({ keyword: '', onlyMyClubs: false })
const myApplies = ref([])
const myJoinApplies = ref([])
const myJoinedClubs = ref([])
const loading = ref(false)
const applyListLoading = ref(false)
const joinApplyListLoading = ref(false)
const actionMessage = ref('')
const actionIsError = ref(false)

const setMsg = (msg, isError = false) => {
  actionMessage.value = msg
  actionIsError.value = isError
}
const activeMenu = ref('clubs')
const detailClub = ref(null)   // 当前弹窗展示的社团，null 表示关闭
const profileLoading = ref(false)
const basicSubmitting = ref(false)
const passwordSubmitting = ref(false)
const contactSubmitting = ref(false)
const profileSubMenu = ref('basic')
const basicForm = reactive({ realName: '', studentNo: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const contactForm = reactive({ phone: '', email: '' })
const applySubmitting = ref(false)
const joinSubmittingMap = reactive({})
const joinApplyDialog = reactive({ visible: false, club: null })
const joinApplyForm = reactive({ selfIntro: '', applyReason: '' })
const eventDetailDialog = reactive({ visible: false, event: null })
const checkinDialog = reactive({ visible: false, eventId: null })
const checkinForm = reactive({ checkinCode: '' })
const checkinSubmitting = ref(false)
const clubCategoryOptions = ['文化类', '体育类', '学术类']
const clubQuery = reactive({
  keyword: '',
  category: '',
})
const applyForm = reactive({
  clubName: '',
  category: '',
  purpose: '',
  instructorName: '',
  remark: '',
  charterFile: null,
  instructorProofFile: null,
})

const getBizData = (response) => {
  if (response && typeof response.code !== 'undefined') {
    if (response.code !== 0) {
      throw new Error(response.message || '请求失败')
    }
    return response.data
  }
  return response
}

const APPLY_STATUS_TEXT_MAP = {
  1: '待初审',
  2: '答辩中',
  3: '公示中',
  4: '通过',
  5: '驳回',
}

const JOIN_APPLY_STATUS_TEXT_MAP = {
  NONE: '未申请',
  PENDING: '待批准',
  JOINED: '已加入',
  REJECTED: '已驳回',
}

const getApplyStatusText = (status) => {
  const parsed = Number(status)
  if (Number.isNaN(parsed)) return '未知'
  return APPLY_STATUS_TEXT_MAP[parsed] || '未知'
}

const getApplyStatusClass = (status) => {
  const parsed = Number(status)
  if (parsed === 4) return 'done'
  if (parsed === 5) return 'rejected'
  return 'pending'
}

const normalizeJoinApplyStatus = (status) => {
  const normalized = String(status || '').trim().toUpperCase()
  if (['NONE', 'PENDING', 'JOINED', 'REJECTED'].includes(normalized)) return normalized
  const numeric = Number(status)
  if (!Number.isNaN(numeric)) {
    if (numeric === 0) return 'NONE'
    if (numeric === 1) return 'PENDING'
    if (numeric === 2) return 'JOINED'
    if (numeric === 3) return 'REJECTED'
  }
  return 'NONE'
}

const getJoinApplyStatusText = (status) => JOIN_APPLY_STATUS_TEXT_MAP[normalizeJoinApplyStatus(status)] || '未申请'

const getJoinApplyStatusClass = (status) => {
  const normalized = normalizeJoinApplyStatus(status)
  if (normalized === 'JOINED') return 'done'
  if (normalized === 'REJECTED') return 'rejected'
  if (normalized === 'NONE') return 'none'
  return 'pending'
}

const formatDateValue = (value) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatDateTimeValue = (value) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

const getClubSearchParams = () => ({
  pageNum: 1,
  pageSize: 12,
  keyword: clubQuery.keyword.trim() || undefined,
  category: clubQuery.category || undefined,
  onlyApproved: true,
  applyStatus: 4,
  status: 'ACTIVE',
})

const parseTimeValue = (value) => {
  if (!value) return NaN
  const ts = new Date(value).getTime()
  return Number.isNaN(ts) ? NaN : ts
}

const isClubRecruitingNow = (club) => {
  if (club?.joinEntryEnabled === true) return true
  if (club?.joinEntryEnabled === false) return false

  if (typeof club?.isRecruiting === 'boolean') {
    return club.isRecruiting
  }

  const recruitStatus = String(club?.recruitStatus || club?.recruit_status || '').toUpperCase()
  if (recruitStatus === 'CLOSED') return false

  const startTs = parseTimeValue(club?.recruitStartAt || club?.recruit_start_at)
  const endTs = parseTimeValue(club?.recruitEndAt || club?.recruit_end_at)
  if (Number.isNaN(startTs) || Number.isNaN(endTs)) return false

  const now = Date.now()
  return now >= startTs && now <= endTs
}

const getJoinButtonMeta = (club) => {
  const joinStatus = normalizeJoinApplyStatus(club?.joinStatus ?? club?.memberJoinStatus ?? club?.joinApplyStatus)
  const clubKey = String(club?.id || '')

  if (clubKey && joinSubmittingMap[clubKey]) {
    return {
      enabled: false,
      label: '提交中...',
      className: 'recruit-pending',
    }
  }

  if (joinStatus === 'JOINED') {
    return {
      enabled: false,
      label: '已加入',
      className: 'recruit-joined',
    }
  }

  if (joinStatus === 'PENDING') {
    return {
      enabled: false,
      label: '待批准',
      className: 'recruit-pending',
    }
  }

  const joinEntryEnabled = club?.joinEntryEnabled
  const isRecruiting = typeof club?.isRecruiting === 'boolean' ? club.isRecruiting : isClubRecruitingNow(club)

  if (joinEntryEnabled === true) {
    return {
      enabled: true,
      label: joinStatus === 'REJECTED' ? '重新申请' : '申请加入',
      className: 'recruit-open',
    }
  }

  if (joinEntryEnabled === false && isRecruiting) {
    return {
      enabled: false,
      label: '已招满',
      className: 'recruit-closed',
    }
  }

  if (joinEntryEnabled === false && !isRecruiting) {
    return {
      enabled: false,
      label: '当前未招新',
      className: 'recruit-closed',
    }
  }

  if (isRecruiting) {
    return {
      enabled: true,
      label: joinStatus === 'REJECTED' ? '重新申请' : '申请加入',
      className: 'recruit-open',
    }
  }

  return {
    enabled: false,
    label: '当前未招新',
    className: 'recruit-closed',
  }
}

const loadClubList = async () => {
  const clubResponse = await getClubPublicListApi(getClubSearchParams())
  const clubData = getBizData(clubResponse)
  clubs.value = Array.isArray(clubData?.records) ? clubData.records : Array.isArray(clubData) ? clubData : []
}

const loadEvents = async () => {
  try {
    const params = { pageNum: 1, pageSize: 20 }
    if (eventQuery.keyword.trim()) params.keyword = eventQuery.keyword.trim()
    if (eventQuery.onlyMyClubs) params.onlyMyClubs = true
    const eventResponse = await getOpenEventsApi(params)
    const eventData = getBizData(eventResponse)
    events.value = Array.isArray(eventData?.records) ? eventData.records : Array.isArray(eventData) ? eventData : []
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '活动加载失败', true)
  }
}

const loadData = async () => {
  loading.value = true
  setMsg('')
  try {
    await Promise.all([loadClubList(), loadEvents()])
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '数据加载失败', true)
  } finally {
    loading.value = false
  }
}

const handleSearchClub = async () => {
  loading.value = true
  setMsg('')
  try {
    await loadClubList()
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '社团列表加载失败', true)
  } finally {
    loading.value = false
  }
}

const resetClubFilter = async () => {
  clubQuery.keyword = ''
  clubQuery.category = ''
  await handleSearchClub()
}

const loadMyApplies = async () => {
  applyListLoading.value = true
  try {
    const response = await getMyClubApplyListApi({ pageNum: 1, pageSize: 20 })
    const data = getBizData(response)
    myApplies.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '我的申报记录加载失败', true)
  } finally {
    applyListLoading.value = false
  }
}

const loadMyJoinApplies = async () => {
  joinApplyListLoading.value = true
  try {
    const response = await getMyJoinClubApplyListApi({ pageNum: 1, pageSize: 20 })
    const data = getBizData(response)
    myJoinApplies.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '我的入社申请记录加载失败', true)
  } finally {
    joinApplyListLoading.value = false
  }
}

const joinedClubsLoading = ref(false)
const loadMyJoinedClubs = async () => {
  joinedClubsLoading.value = true
  try {
    const response = await getMyJoinedClubsApi({ pageNum: 1, pageSize: 20 })
    const data = getBizData(response)
    myJoinedClubs.value = Array.isArray(data?.records) ? data.records : Array.isArray(data) ? data : []
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '我的社团加载失败', true)
  } finally {
    joinedClubsLoading.value = false
  }
}

const handleJoinClub = (club) => {
  const joinMeta = getJoinButtonMeta(club)
  if (!joinMeta.enabled) return
  joinApplyForm.selfIntro = ''
  joinApplyForm.applyReason = ''
  joinApplyDialog.club = club
  joinApplyDialog.visible = true
}

const submitJoinApply = async () => {
  const club = joinApplyDialog.club
  const clubId = club?.id
  if (!clubId) return

  const clubKey = String(clubId)
  joinSubmittingMap[clubKey] = true
  try {
    const response = await joinClubApi(clubId, {
      selfIntro: joinApplyForm.selfIntro.trim() || null,
      applyReason: joinApplyForm.applyReason.trim() || null,
    })
    getBizData(response)
    joinApplyDialog.visible = false
    setMsg('加社申请已提交')
    await Promise.all([loadClubList(), loadMyJoinApplies(), loadMyJoinedClubs()])
  } catch (error) {
    const responseCode = error?.response?.data?.code
    const responseMessage = String(error?.response?.data?.message || error?.message || '')
    if (responseCode === 1006 && /quota is full|名额已满|已满/i.test(responseMessage)) {
      setMsg('当前社团招新名额已满，请稍后关注是否释放名额', true)
    } else {
      setMsg(responseMessage || '提交失败', true)
    }
    await loadClubList()
  } finally {
    joinSubmittingMap[clubKey] = false
  }
}

const handleSignupEvent = async (eventId) => {
  try {
    const response = await signupEventApi(eventId)
    getBizData(response)
    setMsg('活动报名成功')
    await loadEvents()
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '报名失败', true)
  }
}

const openEventDetail = async (event) => {
  try {
    const res = await getEventDetailApi(event.id)
    eventDetailDialog.event = getBizData(res)
    eventDetailDialog.visible = true
  } catch (error) {
    setMsg(error?.response?.data?.message || '活动详情加载失败', true)
  }
}

const handleCancelSignup = async (event) => {
  try {
    await cancelEventSignupApi(event.id)
    setMsg('取消报名成功')
    await loadEvents()
  } catch (error) {
    setMsg(error?.response?.data?.message || '取消报名失败', true)
  }
}

const openCheckinDialog = (eventId) => {
  checkinForm.checkinCode = ''
  checkinDialog.eventId = eventId ?? null
  checkinDialog.visible = true
}

const handleCheckinByCode = async () => {
  if (!checkinForm.checkinCode.trim()) {
    setMsg('请输入签到码', true)
    return
  }
  checkinSubmitting.value = true
  checkinDialog.visible = false
  try {
    await checkinByCodeApi({ eventId: checkinDialog.eventId, checkinCode: checkinForm.checkinCode.trim(), checkinType: 1 })
    setMsg('签到成功')
    loadEvents().catch(() => {})
  } catch (error) {
    setMsg(error?.response?.data?.message || '签到失败', true)
  } finally {
    checkinSubmitting.value = false
  }
}

const isSignupClosed = (event) => {
  // 后端标记不可报名
  if (event.canSignup === false) return true
  // 1. 剩余名额为0
  if (event.remainingSlots !== null && event.remainingSlots !== undefined && event.remainingSlots <= 0) {
    return true
  }
  // 2. 报名截止时间已过
  if (event.signupEndAt) {
    const now = new Date()
    const endTime = new Date(event.signupEndAt)
    if (now > endTime) {
      return true
    }
  }
  return false
}

const isEventStarted = (event) => {
  if (!event.startAt) return false
  return new Date() >= new Date(event.startAt)
}

const getEventDisplayStatus = (event) => {
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
      if (event.remainingSlots !== null && event.remainingSlots !== undefined && event.remainingSlots <= 0) {
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

  return '未知'
}

const getEventStatusClass = (event) => {
  const status = getEventDisplayStatus(event)
  if (status === '报名中') return 'done'
  if (status === '活动中') return 'done'
  if (status === '已结束') return 'pending'
  if (status === '报名已满') return 'rejected'
  if (status === '报名已结束') return 'pending'
  if (status === '未开始') return 'pending'
  return 'pending'
}

const getSignupButtonText = (event) => {
  if (isSignupClosed(event)) {
    const status = getEventDisplayStatus(event)
    if (status === '报名已满') return '报名已满'
    if (status === '报名已结束') return '报名已结束'
    if (status === '活动中') return '活动进行中'
    if (status === '已结束') return '活动已结束'
    return '报名已结束'
  }
  return '报名活动'
}

const handleCharterFileChange = (event) => {
  const file = event?.target?.files?.[0] || null
  applyForm.charterFile = file
}

const handleInstructorProofFileChange = (event) => {
  const file = event?.target?.files?.[0] || null
  applyForm.instructorProofFile = file
}

const resetApplyForm = () => {
  applyForm.clubName = ''
  applyForm.category = ''
  applyForm.purpose = ''
  applyForm.instructorName = ''
  applyForm.remark = ''
  applyForm.charterFile = null
  applyForm.instructorProofFile = null
}

const submitClubApply = async () => {
  if (!applyForm.clubName.trim()) {
    setMsg('请填写社团名称', true)
    return
  }
  if (!applyForm.category.trim()) {
    setMsg('请选择社团分类', true)
    return
  }
  if (!applyForm.instructorName.trim()) {
    setMsg('请填写指导教师姓名', true)
    return
  }
  if (!applyForm.charterFile) {
    setMsg('请上传社团章程文件', true)
    return
  }
  if (!applyForm.instructorProofFile) {
    setMsg('请上传指导教师证明文件', true)
    return
  }

  applySubmitting.value = true
  setMsg('')
  try {
    const materialFormData = new FormData()
    materialFormData.append('file', applyForm.charterFile, applyForm.charterFile.name)
    materialFormData.append('multipartFile', applyForm.charterFile, applyForm.charterFile.name)
    materialFormData.append('bizType', 'CLUB_APPLY_CHARTER')
    const uploadRes = await uploadClubApplyMaterialApi(materialFormData)
    const uploadData = getBizData(uploadRes) || {}
    const charterUrl = uploadData.url || uploadData.fileUrl || uploadData.path || ''

    if (!charterUrl) {
      throw new Error('章程文件上传成功但未返回访问地址')
    }

    const proofFormData = new FormData()
    proofFormData.append('file', applyForm.instructorProofFile, applyForm.instructorProofFile.name)
    proofFormData.append('multipartFile', applyForm.instructorProofFile, applyForm.instructorProofFile.name)
    proofFormData.append('bizType', 'CLUB_APPLY_INSTRUCTOR_PROOF')
    const proofUploadRes = await uploadClubApplyMaterialApi(proofFormData)
    const proofUploadData = getBizData(proofUploadRes) || {}
    const instructorProofUrl = proofUploadData.url || proofUploadData.fileUrl || proofUploadData.path || ''

    if (!instructorProofUrl) {
      throw new Error('指导教师证明文件上传成功但未返回访问地址')
    }

    const applyPayload = {
      clubName: applyForm.clubName.trim(),
      category: applyForm.category.trim(),
      purpose: applyForm.purpose.trim(),
      instructorName: applyForm.instructorName.trim(),
      charterUrl,
      instructorProofUrl,
      remark: applyForm.remark.trim(),
    }

    const applyRes = await submitClubApplyApi(applyPayload)
    getBizData(applyRes)

    setMsg('新建社团申请已提交，请等待学校管理员审批')
    resetApplyForm()
    await loadMyApplies()
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '社团申报提交失败', true)
  } finally {
    applySubmitting.value = false
  }
}

const loadProfile = async () => {
  profileLoading.value = true
  try {
    const res = await getProfileApi()
    const data = getBizData(res)
    if (data) {
      basicForm.realName = data.realName || ''
      basicForm.studentNo = data.studentNo || ''
      contactForm.phone = data.phone || ''
      contactForm.email = data.email || ''
    }
  } catch (error) {
    setMsg(error?.response?.data?.message || '加载个人信息失败', true)
  } finally {
    profileLoading.value = false
  }
}

const handleUpdateBasicInfo = async () => {
  if (!basicForm.realName.trim()) {
    setMsg('真实姓名不能为空', true)
    return
  }
  basicSubmitting.value = true
  setMsg('')
  try {
    await updateBasicInfoApi({ realName: basicForm.realName.trim(), studentNo: basicForm.studentNo.trim() || null })
    setMsg('基本信息更新成功')
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '更新失败', true)
  } finally {
    basicSubmitting.value = false
  }
}

const handleUpdatePassword = async () => {
  if (!passwordForm.oldPassword.trim()) {
    setMsg('请输入旧密码', true)
    return
  }
  if (!passwordForm.newPassword.trim()) {
    setMsg('请输入新密码', true)
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    setMsg('两次输入的新密码不一致', true)
    return
  }
  if (!/^(?=.*[A-Za-z])(?=.*\d).{8,64}$/.test(passwordForm.newPassword)) {
    setMsg('新密码至少8位，且同时包含字母和数字', true)
    return
  }
  passwordSubmitting.value = true
  setMsg('')
  try {
    await updatePasswordApi({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    authStore.logout()
    router.push('/login')
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '密码修改失败', true)
  } finally {
    passwordSubmitting.value = false
  }
}

const handleUpdateContact = async () => {
  contactSubmitting.value = true
  setMsg('')
  try {
    await updateContactApi({ phone: contactForm.phone.trim() || null, email: contactForm.email.trim() || null })
    setMsg('联系方式更新成功')
  } catch (error) {
    setMsg(error?.response?.data?.message || error?.message || '更新失败', true)
  } finally {
    contactSubmitting.value = false
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadData()
  loadMyApplies()
  loadMyJoinApplies()
  loadMyJoinedClubs()
})

watch(activeMenu, (val) => {
  if (val === 'profile') loadProfile()
})
</script>

<template>
  <div class="dashboard-page">
    <div class="layout-shell">
      <aside class="left-menu">
        <div class="menu-brand">
          <h2>学生中心</h2>
          <p class="menu-sub">参与社团与活动</p>
        </div>

        <button type="button" class="menu-item" :class="{ active: activeMenu === 'clubs' }" @click="activeMenu = 'clubs'">
          社团广场
        </button>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'myClubs' }" @click="activeMenu = 'myClubs'">
          我的社团
        </button>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'events' }" @click="activeMenu = 'events'">
          活动报名
        </button>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'apply' }" @click="activeMenu = 'apply'">
          社团申报
        </button>

        <div class="menu-divider"></div>
        <button type="button" class="menu-item" :class="{ active: activeMenu === 'profile' }" @click="activeMenu = 'profile'; profileSubMenu = 'basic'">
          个人信息
        </button>
        <template v-if="activeMenu === 'profile'">
          <button type="button" class="menu-item menu-sub-item" :class="{ active: profileSubMenu === 'basic' }" @click="profileSubMenu = 'basic'">基本信息</button>
          <button type="button" class="menu-item menu-sub-item" :class="{ active: profileSubMenu === 'password' }" @click="profileSubMenu = 'password'">修改密码</button>
          <button type="button" class="menu-item menu-sub-item" :class="{ active: profileSubMenu === 'contact' }" @click="profileSubMenu = 'contact'">联系方式</button>
        </template>
        <button type="button" class="menu-item ghost" :disabled="loading" @click="loadData">刷新数据</button>
        <button type="button" class="menu-item logout" @click="handleLogout">退出登录</button>
      </aside>

      <main class="right-content">
        <header class="content-head">
          <h1>学生中心</h1>
          <p>可浏览社团、加入社团并报名活动。</p>
        </header>

        <p v-if="actionMessage" :class="['message', actionIsError ? 'message-error' : '']">{{ actionMessage }}</p>

        <section v-show="activeMenu === 'clubs'" class="content-panel">
          <h3>社团广场</h3>
          <div class="club-search-bar">
            <label>
              关键词
              <input v-model.trim="clubQuery.keyword" type="text" placeholder="输入关键词，如音乐、科技" @keyup.enter="handleSearchClub" />
            </label>
            <label>
              分类
              <select v-model="clubQuery.category">
                <option value="">全部分类</option>
                <option v-for="option in clubCategoryOptions" :key="option" :value="option">{{ option }}</option>
              </select>
            </label>
            <div class="club-search-actions">
              <button type="button" class="ghost-btn" :disabled="loading" @click="resetClubFilter">重置筛选</button>
              <button type="button" :disabled="loading" @click="handleSearchClub">搜索</button>
            </div>
          </div>

          <ul class="list-grid">
            <li v-for="club in clubs" :key="club.id" class="list-item">
              <h4>{{ club.clubName || club.name }}</h4>
              <p>分类：{{ club.category || '未分类' }}</p>
              <p>成立时间：{{ formatDateValue(club.establishedDate || club.established_at) }}</p>
              <p>指导老师：{{ club.instructorName || '-' }}</p>
              <p>社团宗旨：{{ club.purpose || '暂无' }}</p>
              <p>
                招新动态：
                {{ formatDateTimeValue(club.recruitStartAt || club.recruit_start_at) }}
                至
                {{ formatDateTimeValue(club.recruitEndAt || club.recruit_end_at) }}
              </p>
              <p>报名人数：{{ club.signedUpCount ?? '-' }} / {{ club.recruitLimit ?? club.recruit_limit ?? '-' }}</p>
              <button
                type="button"
                class="square-join-btn"
                :class="getJoinButtonMeta(club).className"
                :disabled="!getJoinButtonMeta(club).enabled"
                @click="handleJoinClub(club)"
              >
                {{ getJoinButtonMeta(club).label }}
              </button>
            </li>
          </ul>
          <p v-if="clubs.length === 0" class="empty-tip">暂无匹配社团，请调整关键词或分类筛选。</p>

          <div class="apply-history">
            <div class="history-head">
              <h4>我的入社申请</h4>
              <button type="button" class="ghost-btn" :disabled="joinApplyListLoading" @click="loadMyJoinApplies">
                {{ joinApplyListLoading ? '加载中...' : '刷新记录' }}
              </button>
            </div>
            <ul v-if="myJoinApplies.length > 0" class="list-grid">
              <li v-for="record in myJoinApplies" :key="record.id || `${record.clubId}-${record.createdAt}`" class="list-item">
                <h4>{{ record.clubName || '-' }}</h4>
                <p>
                  申请状态：
                  <span class="status-pill" :class="getJoinApplyStatusClass(record.joinStatus)">
                    {{ getJoinApplyStatusText(record.joinStatus) }}
                  </span>
                </p>
                <p>提交时间：{{ record.createdAt || '-' }}</p>
                <p v-if="normalizeJoinApplyStatus(record.joinStatus) === 'REJECTED'">驳回原因：{{ record.rejectReason || '-' }}</p>
              </li>
            </ul>
            <p v-else class="empty-tip">暂无入社申请记录</p>
          </div>
        </section>

        <section v-show="activeMenu === 'events'" class="content-panel">
          <h3>活动报名</h3>
          <div class="event-search-bar">
            <input
              v-model="eventQuery.keyword"
              type="text"
              placeholder="搜索活动标题或社团名称..."
              class="event-search-input"
              @keyup.enter="loadEvents"
            />
            <label class="event-filter-check">
              <input type="checkbox" v-model="eventQuery.onlyMyClubs" @change="loadEvents" />
              只看已加入社团的活动
            </label>
            <button type="button" @click="loadEvents">搜索</button>
            <button type="button" class="ghost-btn" @click="eventQuery.keyword = ''; eventQuery.onlyMyClubs = false; loadEvents()">重置</button>
          </div>
          <ul class="list-grid">
            <li v-for="event in events" :key="event.id" class="list-item event-card">
              <h4 class="event-title-link" @click="openEventDetail(event)">{{ event.title }}</h4>
              <p>社团：{{ event.clubName || '-' }}</p>
              <p>地点：{{ event.location || '-' }}</p>
              <p>活动时间：{{ formatDateTimeValue(event.startAt) }} 至 {{ formatDateTimeValue(event.endAt) }}</p>
              <p v-if="event.signupStartAt && event.signupEndAt">报名时间：{{ formatDateTimeValue(event.signupStartAt) }} 至 {{ formatDateTimeValue(event.signupEndAt) }}</p>
              <p>剩余名额：{{ event.remainingSlots ?? '-' }} / {{ event.limitCount ?? '-' }}</p>
              <p><span :class="['status-pill', getEventStatusClass(event)]">{{ getEventDisplayStatus(event) }}</span></p>
              <p v-if="event.onlyMember"><span class="status-pill pending">仅社团成员</span></p>
              <div class="event-actions">
                <template v-if="event.signupStatus === 1 && event.checkedIn">
                  <span class="status-pill done">已签到</span>
                </template>
                <template v-else-if="event.signupStatus === 1">
                  <span class="status-pill done">已报名</span>
                  <button type="button" class="ghost-btn" :disabled="isEventStarted(event)" :style="isEventStarted(event) ? 'opacity:0.5;cursor:not-allowed;' : ''" @click="!isEventStarted(event) && handleCancelSignup(event)">{{ isEventStarted(event) ? '活动已开始' : '取消报名' }}</button>
                  <button type="button" @click="openCheckinDialog(event.id)">签到</button>
                </template>
                <template v-else>
                  <button
                    type="button"
                    :disabled="isSignupClosed(event)"
                    :style="isSignupClosed(event) ? 'opacity: 0.5; cursor: not-allowed;' : ''"
                    @click="handleSignupEvent(event.id)"
                  >
                    {{ getSignupButtonText(event) }}
                  </button>
                </template>
              </div>
            </li>
          </ul>
          <p v-if="events.length === 0" class="empty-tip">暂无开放报名的活动</p>
        </section>

        <section v-show="activeMenu === 'myClubs'" class="content-panel">
          <div class="history-head">
            <h3>我的社团</h3>
            <button type="button" class="ghost-btn" :disabled="joinedClubsLoading" @click="loadMyJoinedClubs">
              {{ joinedClubsLoading ? '加载中...' : '刷新' }}
            </button>
          </div>
          <ul v-if="myJoinedClubs.length > 0" class="list-grid">
            <li v-for="record in myJoinedClubs" :key="record.clubId" class="list-item joined-club-item">
              <div class="joined-club-head">
                <h4>{{ record.clubName || '-' }}</h4>
                <span class="position-tag">{{ record.positionName || '成员' }}</span>
              </div>
              <p class="joined-meta">{{ record.category || '' }}</p>
              <button type="button" class="detail-btn" @click="detailClub = record">详情</button>
            </li>
          </ul>
          <p v-else class="empty-tip">暂未加入任何社团</p>
        </section>

        <section v-show="activeMenu === 'apply'" class="content-panel">
          <h3>新建社团申报</h3>
          <div class="apply-form">
            <label>
              社团名称
              <input v-model="applyForm.clubName" type="text" placeholder="请输入社团名称" />
            </label>
            <label>
              社团分类
              <select v-model="applyForm.category">
                <option value="" disabled>请选择社团分类</option>
                <option v-for="option in clubCategoryOptions" :key="option" :value="option">{{ option }}</option>
              </select>
            </label>
            <label class="full">
              社团宗旨
              <textarea v-model="applyForm.purpose" rows="3" placeholder="简要说明社团宗旨" />
            </label>
            <label>
              指导教师姓名
              <input v-model="applyForm.instructorName" type="text" placeholder="请输入指导教师姓名" />
            </label>
            <label>
              章程文件
              <input type="file" @change="handleCharterFileChange" />
            </label>
            <label>
              指导教师证明文件
              <input type="file" @change="handleInstructorProofFileChange" />
            </label>
            <label class="full">
              备注
              <textarea v-model="applyForm.remark" rows="4" maxlength="1000" placeholder="可填写补充说明（最多1000字）" />
            </label>
            <button type="button" class="submit-btn" :disabled="applySubmitting" @click="submitClubApply">
              {{ applySubmitting ? '提交中...' : '提交新建社团申请' }}
            </button>
          </div>

          <div class="apply-history">
            <div class="history-head">
              <h4>我的社团申报</h4>
              <button type="button" class="ghost-btn" :disabled="applyListLoading" @click="loadMyApplies">
                {{ applyListLoading ? '加载中...' : '刷新记录' }}
              </button>
            </div>
            <ul v-if="myApplies.length > 0" class="list-grid">
              <li v-for="record in myApplies" :key="record.id || record.clubId" class="list-item">
                <h4>{{ record.clubName || '-' }}</h4>
                <p>
                  申请状态：
                  <span class="status-pill" :class="getApplyStatusClass(record.applyStatus)">
                    {{ getApplyStatusText(record.applyStatus) }}
                  </span>
                </p>
                <p>提交时间：{{ record.createdAt || '-' }}</p>
              </li>
            </ul>
            <p v-else class="empty-tip">暂无申报记录</p>
          </div>
        </section>
        <section v-show="activeMenu === 'profile'" class="content-panel">
          <h3>个人信息</h3>
          <div v-if="profileLoading" class="empty-tip">加载中...</div>
          <div v-else class="profile-sections">

            <!-- 基本信息 -->
            <div v-if="profileSubMenu === 'basic'" class="profile-card">
              <h4 class="profile-card-title">基本信息</h4>
              <div class="profile-readonly">
                <span class="profile-label">用户名</span>
                <span class="profile-value">{{ authStore.user?.username || '-' }}</span>
              </div>
              <label class="profile-field">
                <span class="profile-label">真实姓名</span>
                <input v-model="basicForm.realName" type="text" placeholder="请输入真实姓名" maxlength="50" />
              </label>
              <label class="profile-field">
                <span class="profile-label">学号</span>
                <input v-model="basicForm.studentNo" type="text" placeholder="请输入学号" maxlength="30" />
              </label>
              <button type="button" class="submit-btn" :disabled="basicSubmitting" @click="handleUpdateBasicInfo">
                {{ basicSubmitting ? '保存中...' : '保存基本信息' }}
              </button>
            </div>

            <!-- 修改密码 -->
            <div v-if="profileSubMenu === 'password'" class="profile-card">
              <h4 class="profile-card-title">修改密码</h4>
              <label class="profile-field">
                <span class="profile-label">旧密码</span>
                <input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" autocomplete="current-password" />
              </label>
              <label class="profile-field">
                <span class="profile-label">新密码</span>
                <input v-model="passwordForm.newPassword" type="password" placeholder="至少8位，含字母和数字" autocomplete="new-password" />
              </label>
              <label class="profile-field">
                <span class="profile-label">确认新密码</span>
                <input v-model="passwordForm.confirmPassword" type="password" placeholder="再次输入新密码" autocomplete="new-password" />
              </label>
              <p class="profile-hint">修改密码后将自动退出登录</p>
              <button type="button" class="submit-btn" :disabled="passwordSubmitting" @click="handleUpdatePassword">
                {{ passwordSubmitting ? '提交中...' : '修改密码' }}
              </button>
            </div>

            <!-- 联系方式 -->
            <div v-if="profileSubMenu === 'contact'" class="profile-card">
              <h4 class="profile-card-title">联系方式</h4>
              <label class="profile-field">
                <span class="profile-label">电话号码</span>
                <input v-model="contactForm.phone" type="tel" placeholder="请输入电话号码" maxlength="20" />
              </label>
              <label class="profile-field">
                <span class="profile-label">邮箱</span>
                <input v-model="contactForm.email" type="email" placeholder="请输入邮箱地址" maxlength="100" />
              </label>
              <button type="button" class="submit-btn" :disabled="contactSubmitting" @click="handleUpdateContact">
                {{ contactSubmitting ? '保存中...' : '保存联系方式' }}
              </button>
            </div>

          </div>
        </section>
      </main>
    </div>

    <!-- 社团详情弹窗 -->
    <div v-if="detailClub" class="modal-mask" @click.self="detailClub = null">
      <div class="modal-box">
        <div class="modal-head">
          <h3>{{ detailClub.clubName || '-' }}</h3>
          <button type="button" class="modal-close" @click="detailClub = null">✕</button>
        </div>
        <div class="modal-body">
          <div v-if="detailClub.category" class="modal-meta-row">
            <span class="modal-label">分类</span>
            <span>{{ detailClub.category }}</span>
          </div>
          <div class="modal-meta-row">
            <span class="modal-label">我的职位</span>
            <span class="position-tag">{{ detailClub.positionName || '成员' }}</span>
          </div>
          <div class="modal-meta-row">
            <span class="modal-label">加入时间</span>
            <span>{{ formatDateTimeValue(detailClub.joinAt) }}</span>
          </div>
          <div class="modal-divider"></div>
          <div class="modal-section-title">社团简介</div>
          <p class="modal-intro">{{ detailClub.introduction || detailClub.purpose || '暂无简介' }}</p>
        </div>
      </div>
    </div>

    <!-- 活动详情弹窗 -->
    <div v-if="eventDetailDialog.visible" class="modal-overlay" @click.self="eventDetailDialog.visible = false">
      <div class="modal-box" style="width:min(520px,100%)">
        <div class="modal-header">
          <h3>活动详情</h3>
          <button type="button" class="modal-close-btn" @click="eventDetailDialog.visible = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-field"><label class="form-label">活动标题</label><p>{{ eventDetailDialog.event?.title }}</p></div>
          <div class="form-field"><label class="form-label">所属社团</label><p>{{ eventDetailDialog.event?.clubName }}</p></div>
          <div class="form-field"><label class="form-label">地点</label><p>{{ eventDetailDialog.event?.location }}</p></div>
          <div class="form-field"><label class="form-label">活动时间</label><p>{{ eventDetailDialog.event?.startAt?.slice(0,16)?.replace('T',' ') }} 至 {{ eventDetailDialog.event?.endAt?.slice(0,16)?.replace('T',' ') }}</p></div>
          <div class="form-field"><label class="form-label">报名时间</label><p>{{ eventDetailDialog.event?.signupStartAt?.slice(0,16)?.replace('T',' ') || '-' }} 至 {{ eventDetailDialog.event?.signupEndAt?.slice(0,16)?.replace('T',' ') || '-' }}</p></div>
          <div class="form-field"><label class="form-label">人数上限</label><p>{{ eventDetailDialog.event?.limitCount || '不限' }} | 报名: {{ eventDetailDialog.event?.signupCount ?? 0 }} | 签到: {{ eventDetailDialog.event?.checkinCount ?? 0 }}</p></div>
          <div v-if="eventDetailDialog.event?.content" class="form-field"><label class="form-label">活动内容</label><p style="white-space:pre-wrap">{{ eventDetailDialog.event.content }}</p></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn ghost" @click="eventDetailDialog.visible = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 入社申请弹窗 -->
    <div v-if="joinApplyDialog.visible" class="modal-overlay" @click.self="joinApplyDialog.visible = false">
      <div class="modal-box join-apply-modal">
        <div class="modal-header">
          <h3>申请加入「{{ joinApplyDialog.club?.clubName }}」</h3>
          <button type="button" class="modal-close-btn" @click="joinApplyDialog.visible = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-field">
            <label class="form-label">个人简介</label>
            <textarea
              v-model="joinApplyForm.selfIntro"
              class="form-textarea"
              placeholder="请简单介绍一下自己（技能、爱好、经历等）"
              maxlength="500"
              rows="4"
            ></textarea>
            <span class="char-count">{{ joinApplyForm.selfIntro.length }}/500</span>
          </div>
          <div class="form-field">
            <label class="form-label">入社理由</label>
            <textarea
              v-model="joinApplyForm.applyReason"
              class="form-textarea"
              placeholder="请说明您希望加入本社团的理由和期望"
              maxlength="500"
              rows="4"
            ></textarea>
            <span class="char-count">{{ joinApplyForm.applyReason.length }}/500</span>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn ghost" @click="joinApplyDialog.visible = false">取消</button>
          <button
            type="button"
            class="btn"
            :disabled="joinSubmittingMap[String(joinApplyDialog.club?.id)]"
            @click="submitJoinApply"
          >
            {{ joinSubmittingMap[String(joinApplyDialog.club?.id)] ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 签到码签到弹窗 -->
    <div v-if="checkinDialog.visible" class="modal-overlay" @click.self="checkinDialog.visible = false">
      <section class="modal-box" style="max-width:400px">
        <div class="modal-header">
          <h3>活动签到</h3>
          <button type="button" class="modal-close" @click="checkinDialog.visible = false">×</button>
        </div>
        <div class="modal-body" style="padding:24px">
          <label style="display:block;margin-bottom:8px;font-weight:500">请输入签到码</label>
          <input v-model="checkinForm.checkinCode" type="text" placeholder="输入签到码" maxlength="20"
                 style="width:100%;padding:10px;border:1px solid #d1d5db;border-radius:6px;font-size:14px"
                 @keyup.enter="handleCheckinByCode" />
        </div>
        <div class="modal-footer" style="display:flex;gap:12px;justify-content:flex-end;padding:16px 24px;border-top:1px solid #e8ddd6">
          <button type="button" class="btn ghost" @click="checkinDialog.visible = false">取消</button>
          <button type="button" class="btn" :disabled="checkinSubmitting" @click="handleCheckinByCode">
            {{ checkinSubmitting ? '签到中...' : '确认签到' }}
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  --c-brand: #e0583a;
  --c-brand-hover: #c44a2f;
  --c-brand-soft: rgba(224, 88, 58, 0.08);
  --c-teal: #1b9e8f;
  --c-ink: #2c1e16;
  --c-ink-2: #7a6b62;
  --c-ink-3: #b5a89f;
  --c-rule: #e8ddd6;
  --c-surface: #fdfaf8;
  --c-cream: #f6efe9;
  --font-display: "LXGW WenKai", "Songti SC", "Noto Serif SC", serif;
  --font-body: "Helvetica Neue", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;

  min-height: 100vh;
  background: linear-gradient(165deg, var(--c-surface) 0%, var(--c-cream) 65%, #ffffff 100%);
  font-family: var(--font-body);
  padding: 18px;
}

.layout-shell {
  width: min(1200px, calc(100vw - 24px));
  margin: 0 auto;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 16px;
}

.left-menu {
  background: #ffffff;
  border: 1px solid #e8ddd6;
  border-radius: 16px;
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
  border-bottom: 1px solid #e8ddd6;
  margin-bottom: 6px;
}

.menu-brand h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #2c1e16;
  font-family: var(--font-display);
}

.menu-sub {
  margin: 4px 0 0;
  font-size: 12px;
  color: #7a6b62;
}

.menu-item {
  display: flex;
  align-items: center;
  height: 38px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #5a4e48;
  cursor: pointer;
  padding: 0 12px;
  font-size: 14px;
  text-align: left;
  transition: background 0.15s, color 0.15s;
  width: 100%;
}

.menu-item:hover {
  background: rgba(224, 88, 58, 0.06);
}

.menu-item.active {
  background: rgba(224, 88, 58, 0.08);
  color: #e0583a;
  font-weight: 600;
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
  color: #5a4e48;
  cursor: pointer;
  padding: 0 12px;
  font-size: 14px;
  font-weight: 600;
  transition: background 0.15s;
}

.menu-group-btn:hover {
  background: rgba(224, 88, 58, 0.06);
}

.menu-group-btn.group-active {
  color: #e0583a;
}

.menu-arrow {
  font-size: 11px;
  color: #b5a89f;
}

.submenu {
  padding-left: 8px;
  border-left: 2px solid rgba(27, 158, 143, 0.06);
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
  color: #7a6b62;
  cursor: pointer;
  text-align: left;
  padding: 0 10px;
  font-size: 13px;
  width: 100%;
  transition: background 0.15s, color 0.15s;
}

.submenu-item:hover {
  background: rgba(224, 88, 58, 0.06);
  color: #5a4e48;
}

.submenu-item.active {
  background: rgba(224, 88, 58, 0.08);
  color: #e0583a;
  font-weight: 600;
}

.menu-divider {
  height: 1px;
  background: #e8ddd6;
  margin: 6px 4px;
}

.menu-item.ghost {
  color: #7a6b62;
  font-size: 13px;
}

.menu-item.logout {
  color: #b91c1c;
}

.menu-item.logout:hover {
  background: #fff1f2;
}

.menu-sub-item {
  padding-left: 28px;
  height: 34px;
  font-size: 13px;
  color: #7a6b62;
}

.menu-sub-item.active {
  background: rgba(224, 88, 58, 0.08);
  color: #e0583a;
  font-weight: 600;
}

.menu-sub-item::before {
  content: '·';
  margin-right: 6px;
  color: #b5a89f;
}

.right-content {
  background: #ffffff;
  border: 1px solid #e8ddd6;
  border-radius: 14px;
  padding: 20px;
}

.content-head h1 {
  margin: 0;
  font-size: 28px;
  color: #2c1e16;
  font-family: var(--font-display);
}

.content-head p {
  margin: 8px 0 0;
  color: #7a6b62;
}

.content-panel {
  margin-top: 16px;
}

.club-search-bar {
  display: grid;
  grid-template-columns: minmax(220px, 1fr) minmax(160px, 220px) auto;
  gap: 12px;
  margin-bottom: 12px;
  border: 1px solid #e8ddd6;
  border-radius: 12px;
  padding: 12px;
  background: #fdfaf8;
}

.club-search-bar label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: #5a4e48;
}

.club-search-bar input,
.club-search-bar select {
  height: 36px;
  border: 1px solid #e8ddd6;
  border-radius: 8px;
  padding: 0 10px;
  font-size: 14px;
}

.club-search-actions {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.content-panel h3 {
  margin: 0 0 12px;
  font-size: 20px;
  color: #2c1e16;
  font-family: var(--font-display);
}

h4 {
  margin: 0;
  font-size: 16px;
}

.list-grid {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}

.list-item {
  border: 1px solid #d9e2ec;
  border-radius: 10px;
  padding: 14px;
  text-align: left;
  background: #fdfaf8;
}

.list-item p {
  margin: 8px 0;
  font-size: 13px;
}

button {
  height: 36px;
  padding: 0 16px;
  border: none;
  border-radius: 8px;
  background: #e0583a;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

button:hover {
  background: #c44a2f;
}

.square-join-btn.recruit-open {
  background: #e0583a;
}

.square-join-btn.recruit-open:hover {
  background: #c44a2f;
}

.square-join-btn.recruit-closed,
.square-join-btn.recruit-closed:hover,
.square-join-btn.recruit-closed:disabled {
  background: #b5a89f;
  cursor: not-allowed;
}

.square-join-btn.recruit-pending,
.square-join-btn.recruit-pending:hover,
.square-join-btn.recruit-pending:disabled {
  background: #f59e0b;
  cursor: not-allowed;
}

.square-join-btn.recruit-joined,
.square-join-btn.recruit-joined:hover,
.square-join-btn.recruit-joined:disabled {
  background: #1b9e8f;
  cursor: not-allowed;
}

.message {
  margin: 14px 0;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #99f6e4;
  background: #f0fdfa;
  color: #1b9e8f;
  font-size: 14px;
}

.message-error {
  border-color: #fca5a5;
  background: #fef2f2;
  color: #b91c1c;
}

.apply-form {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
  border: 1px solid #e8ddd6;
  border-radius: 12px;
  padding: 14px;
  background: #fdfaf8;
}

.apply-form label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: #5a4e48;
}

.apply-form small {
  color: #7a6b62;
  font-size: 12px;
}

.apply-form input,
.apply-form select,
.apply-form textarea {
  border: 1px solid #e8ddd6;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
}

.apply-form .full {
  grid-column: 1 / -1;
}

.submit-btn {
  grid-column: 1 / -1;
  width: fit-content;
}

.apply-history {
  margin-top: 14px;
  border: 1px solid #e8ddd6;
  border-radius: 12px;
  background: #ffffff;
  padding: 14px;
}

.history-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.ghost-btn {
  height: 32px;
  border: 1px solid #e8ddd6;
  border-radius: 8px;
  background: #ffffff;
  color: #5a4e48;
}

.ghost-btn:hover {
  background: #fdfaf8;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  line-height: 1.6;
}

.status-pill.pending {
  background: #eff6ff;
  color: #e0583a;
}

.status-pill.done {
  background: rgba(27, 158, 143, 0.08);
  color: #167f73;
}

.status-pill.rejected {
  background: #fff1f2;
  color: #be123c;
}

.status-pill.none {
  background: #f6efe9;
  color: #7a6b62;
}

.joined-club-item {
  border-color: #bbf7d0;
  background: #f0fdf4;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.joined-club-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.joined-club-head h4 {
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.position-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
  background: #d1fae5;
  color: #065f46;
  flex-shrink: 0;
}

.joined-meta {
  margin: 0;
  font-size: 12px;
  color: #7a6b62;
}

.detail-btn {
  margin-top: 6px;
  height: 28px;
  padding: 0 12px;
  font-size: 12px;
  border: 1px solid #6ee7b7;
  border-radius: 6px;
  background: #ffffff;
  color: #059669;
  cursor: pointer;
  align-self: flex-start;
  transition: background 0.15s;
}

.detail-btn:hover {
  background: #ecfdf5;
}

/* 弹窗 */
.modal-mask {
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
  background: #ffffff;
  border-radius: 16px;
  width: min(480px, 100%);
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}

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

.join-apply-modal {
  width: min(520px, 100%);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px 14px;
  border-bottom: 1px solid #e8ddd6;
}

.modal-header h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #2c1e16;
}

.modal-close-btn {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 8px;
  background: #f6efe9;
  color: #7a6b62;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  flex-shrink: 0;
}

.modal-close-btn:hover {
  background: #e8ddd6;
}

.modal-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.modal-footer {
  padding: 14px 20px;
  border-top: 1px solid #e8ddd6;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #5a4e48;
}

.form-textarea {
  width: 100%;
  border: 1px solid #e8ddd6;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #2c1e16;
  resize: vertical;
  box-sizing: border-box;
  font-family: inherit;
  transition: border-color 0.15s;
}

.form-textarea:focus {
  outline: none;
  border-color: #e0583a;
}

.char-count {
  font-size: 12px;
  color: #b5a89f;
  text-align: right;
}

.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px 14px;
  border-bottom: 1px solid #e8ddd6;
}

.modal-head h3 {
  margin: 0;
  font-size: 18px;
  color: #2c1e16;
}

.modal-close {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 8px;
  background: #f6efe9;
  color: #7a6b62;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  flex-shrink: 0;
}

.modal-close:hover {
  background: #e8ddd6;
}

.modal-body {
  padding: 16px 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.modal-meta-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #5a4e48;
}

.modal-label {
  font-size: 12px;
  color: #b5a89f;
  width: 60px;
  flex-shrink: 0;
}

.modal-divider {
  height: 1px;
  background: #e8ddd6;
  margin: 4px 0;
}

.modal-section-title {
  font-size: 12px;
  font-weight: 600;
  color: #b5a89f;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.modal-intro {
  margin: 0;
  font-size: 14px;
  color: #5a4e48;
  line-height: 1.7;
  white-space: pre-wrap;
}

.event-search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.event-search-input {
  flex: 1;
  min-width: 180px;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #e8ddd6;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
}
.event-search-input:focus { border-color: #e0583a; }

.event-filter-check {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #7a6b62;
  cursor: pointer;
  white-space: nowrap;
}

.event-card {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.event-title-link {
  cursor: pointer;
  color: #e0583a;
  transition: color 0.15s;
}

.event-title-link:hover {
  color: #c44a2f;
  text-decoration: underline;
}

.event-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.empty-tip {
  margin: 0;
  color: #7a6b62;
  font-size: 13px;
}

.profile-form {
  max-width: 480px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  border: 1px solid #e8ddd6;
  border-radius: 12px;
  padding: 20px;
  background: #fdfaf8;
}

.profile-sections {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 520px;
}

.profile-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  border: 1px solid #e8ddd6;
  border-radius: 12px;
  padding: 20px;
  background: #fdfaf8;
}

.profile-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #5a4e48;
  margin: 0 0 4px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8ddd6;
}

.profile-hint {
  font-size: 12px;
  color: #f59e0b;
  margin: 0;
}

.profile-readonly {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #e8ddd6;
}

.profile-field {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #5a4e48;
}

.profile-label {
  width: 80px;
  flex-shrink: 0;
  font-size: 13px;
  color: #7a6b62;
}

.profile-value {
  font-size: 14px;
  color: #2c1e16;
  font-weight: 600;
}

.profile-field input {
  flex: 1;
  height: 38px;
  border: 1px solid #e8ddd6;
  border-radius: 8px;
  padding: 0 10px;
  font-size: 14px;
}

.profile-field input:focus {
  outline: none;
  border-color: #e0583a;
  box-shadow: 0 0 0 2px rgba(224, 88, 58, 0.15);
}

.profile-divider {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #b5a89f;
  font-weight: 600;
  padding: 4px 0;
  border-top: 1px solid #e8ddd6;
  margin-top: 2px;
}

@media (max-width: 900px) {
  .layout-shell {
    grid-template-columns: 1fr;
  }

  .club-search-bar {
    grid-template-columns: 1fr;
  }

  .club-search-actions {
    width: 100%;
  }

  .club-search-actions button {
    flex: 1;
  }

  .left-menu {
    gap: 8px;
  }

  .submit-btn {
    width: 100%;
  }
}
</style>
