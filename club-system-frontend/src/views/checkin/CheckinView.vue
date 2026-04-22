<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAccessToken } from '../../utils/token'
import { checkinByCodeApi } from '../../api/user-permission'

const route = useRoute()
const router = useRouter()

const status = ref('loading') // loading | success | already | error | no-code
const message = ref('')
const code = route.query.code
const eventId = route.query.eventId ? Number(route.query.eventId) : undefined

const doCheckin = async () => {
  if (!code) {
    status.value = 'no-code'
    return
  }
  const token = getAccessToken()
  if (!token) {
    router.replace({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  try {
    await checkinByCodeApi({ checkinCode: code, checkinType: 1, eventId })
    status.value = 'success'
    message.value = '签到成功！'
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '签到失败'
    if (msg.includes('已签到') || msg.includes('already')) {
      status.value = 'already'
      message.value = '您已签到过了'
    } else {
      status.value = 'error'
      message.value = msg
    }
  }
}

onMounted(doCheckin)
</script>

<template>
  <div style="min-height:100vh;display:flex;align-items:center;justify-content:center;background:#f0f9ff">
    <div style="background:#fff;border-radius:16px;padding:40px 32px;text-align:center;box-shadow:0 4px 24px rgba(0,0,0,0.08);max-width:360px;width:90%">
      <div v-if="status === 'loading'" style="color:#64748b;font-size:16px">签到中...</div>

      <div v-else-if="status === 'success'">
        <div style="font-size:64px;margin-bottom:12px">✅</div>
        <h2 style="color:#059669;margin:0 0 8px">签到成功</h2>
        <p style="color:#64748b;margin:0">您已成功签到本次活动</p>
      </div>

      <div v-else-if="status === 'already'">
        <div style="font-size:64px;margin-bottom:12px">✔️</div>
        <h2 style="color:#0284c7;margin:0 0 8px">已签到</h2>
        <p style="color:#64748b;margin:0">您已经签到过本次活动了</p>
      </div>

      <div v-else-if="status === 'error'">
        <div style="font-size:64px;margin-bottom:12px">❌</div>
        <h2 style="color:#dc2626;margin:0 0 8px">签到失败</h2>
        <p style="color:#64748b;margin:0">{{ message }}</p>
        <button @click="doCheckin" style="margin-top:16px;padding:8px 24px;background:#1d4ed8;color:#fff;border:none;border-radius:8px;cursor:pointer;font-size:14px">重试</button>
      </div>

      <div v-else-if="status === 'no-code'">
        <div style="font-size:64px;margin-bottom:12px">⚠️</div>
        <h2 style="color:#d97706;margin:0 0 8px">无效链接</h2>
        <p style="color:#64748b;margin:0">签到码缺失，请重新扫码</p>
      </div>
    </div>
  </div>
</template>
