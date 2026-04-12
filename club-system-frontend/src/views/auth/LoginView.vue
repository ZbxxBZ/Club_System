<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCurrentUserApi, loginApi, ssoLoginApi } from '../../api/auth'
import { useAuthStore } from '../../stores/auth'
import { getDefaultDashboardPath, resolveRoleFromLoginData } from '../../utils/role'

const form = reactive({
  username: '',
  password: '',
  remember: false,
})

const loading = ref(false)
const errorMessage = ref('')
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const getBizData = (response) => {
  if (response && typeof response.code !== 'undefined') {
    if (response.code !== 0) {
      throw new Error(response.message || '登录失败')
    }
    return response.data || {}
  }
  return response || {}
}

const buildProfileFromData = (data = {}) => ({
  userId: data.userId,
  username: data.username,
  realName: data.realName,
  status: data.status || 'ACTIVE',
  clubId: data.clubId,
})

const handleLoginSuccess = async (data, remember) => {
  const token = data.token || data.accessToken
  if (!token) {
    throw new Error('登录成功但未返回token')
  }

  const role = resolveRoleFromLoginData(data)
  if (!role) {
    throw new Error('登录成功但未返回角色信息')
  }

  let profile = buildProfileFromData(data)

  // Prefer /auth/me for authoritative user status and profile when backend supports it.
  try {
    const meResponse = await getCurrentUserApi()
    const meData = getBizData(meResponse)
    profile = {
      ...profile,
      ...buildProfileFromData(meData),
    }
  } catch {
    // Fallback to login payload only.
  }

  authStore.setSession({
    accessToken: token,
    userRole: role,
    userProfile: profile,
    remember,
  })

  const redirectPath = typeof route.query.redirect === 'string' ? route.query.redirect : getDefaultDashboardPath(role)
  router.push(redirectPath)
}

const handleLogin = async () => {
  if (!form.username || !form.password) {
    errorMessage.value = '请输入账号和密码'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await loginApi({
      username: form.username,
      password: form.password,
    })
    const data = getBizData(response)
    await handleLoginSuccess(data, form.remember)
    window.alert('登录成功')
  } catch (error) {
    errorMessage.value = error?.response?.data?.message || error?.message || '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const handleSsoRedirect = () => {
  const ssoLoginUrl = import.meta.env.VITE_SSO_LOGIN_URL
  if (!ssoLoginUrl) {
    errorMessage.value = '未配置校园统一认证地址（VITE_SSO_LOGIN_URL）'
    return
  }
  window.location.href = ssoLoginUrl
}

const handleSsoTicket = async (ticket) => {
  loading.value = true
  errorMessage.value = ''
  try {
    const response = await ssoLoginApi({ ticket })
    const data = getBizData(response)
    await handleLoginSuccess(data, true)
    window.alert('校园统一认证登录成功')
  } catch (error) {
    errorMessage.value = error?.response?.data?.message || error?.message || '统一认证登录失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const ssoTicket = typeof route.query.ticket === 'string' ? route.query.ticket : ''
  if (ssoTicket) {
    handleSsoTicket(ssoTicket)
  }
})
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h1>学校社团管理系统</h1>
      <p class="sub-title">登录后进入系统</p>

      <form class="login-form" @submit.prevent="handleLogin">
        <label>
          <span>账号</span>
          <input v-model.trim="form.username" type="text" placeholder="请输入用户名" />
        </label>

        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" placeholder="请输入密码" />
        </label>

        <label class="remember-row">
          <input v-model="form.remember" type="checkbox" />
          <span>记住我</span>
        </label>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

        <button type="submit" :disabled="loading">{{ loading ? '登录中...' : '登 录' }}</button>
        <button type="button" class="sso-btn" :disabled="loading" @click="handleSsoRedirect">校园统一认证登录</button>
        <button type="button" class="home-btn" @click="router.push('/')">返回主页</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.login-card {
  width: 380px;
  padding: 28px 24px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(31, 35, 41, 0.08);
}

h1 {
  margin: 0;
  font-size: 24px;
  color: #1f2329;
  text-align: center;
}

.sub-title {
  margin: 10px 0 20px;
  text-align: center;
  font-size: 14px;
  color: #646a73;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: #1f2329;
}

input[type='text'],
input[type='password'] {
  height: 40px;
  border: 1px solid #d0d3d9;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
  outline: none;
}

input[type='text']:focus,
input[type='password']:focus {
  border-color: #409eff;
}

.remember-row {
  flex-direction: row;
  align-items: center;
  gap: 8px;
  color: #646a73;
}

.error-message {
  margin: 0;
  font-size: 13px;
  color: #f56c6c;
}

button {
  margin-top: 4px;
  height: 42px;
  border: none;
  border-radius: 8px;
  background: #409eff;
  color: #fff;
  font-size: 15px;
  cursor: pointer;
}

button:hover {
  background: #337ecc;
}

button:disabled {
  background: #9ac8ff;
  cursor: not-allowed;
}

.home-btn {
  margin-top: 0;
  border: 1px solid #d0d3d9;
  background: #fff;
  color: #1f2329;
}

.sso-btn {
  margin-top: 0;
  background: #0f766e;
}

.sso-btn:hover {
  background: #0d5f59;
}

.home-btn:hover {
  background: #f2f3f5;
}
</style>
