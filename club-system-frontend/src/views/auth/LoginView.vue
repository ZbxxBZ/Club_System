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
const visible = ref(false)
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

onMounted(() => {
  requestAnimationFrame(() => { visible.value = true })
  const ssoTicket = typeof route.query.ticket === 'string' ? route.query.ticket : ''
  if (ssoTicket) {
    handleSsoTicket(ssoTicket)
  }
})

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
  } catch (error) {
    errorMessage.value = error?.response?.data?.message || error?.message || '统一认证登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page" :class="{ alive: visible }">
    <!-- Background shapes -->
    <div class="bg-shapes" aria-hidden="true">
      <div class="bg-shape bg-shape-1"></div>
      <div class="bg-shape bg-shape-2"></div>
      <div class="bg-shape bg-shape-3"></div>
    </div>

    <div class="auth-layout">
      <!-- Left: decorative panel -->
      <div class="auth-aside">
        <div class="aside-content">
          <router-link to="/" class="aside-brand">
            <span class="brand-dot"></span>
            <span class="brand-name">社团广场</span>
          </router-link>

          <div class="aside-illust" aria-hidden="true">
            <div class="illust-card card-1">
              <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <ellipse cx="32" cy="42" rx="20" ry="18" stroke="var(--c-coral)" />
                <circle cx="32" cy="40" r="6" stroke="var(--c-coral)" />
                <line x1="32" y1="24" x2="32" y2="6" stroke="var(--c-coral)" />
                <rect x="26" y="2" width="12" height="8" rx="2" stroke="var(--c-coral)" />
              </svg>
            </div>
            <div class="illust-card card-2">
              <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="32" cy="32" r="22" stroke="var(--c-teal)" />
                <path d="M10 32 Q32 24 54 32" stroke="var(--c-teal)" />
                <path d="M10 32 Q32 40 54 32" stroke="var(--c-teal)" />
                <line x1="32" y1="10" x2="32" y2="54" stroke="var(--c-teal)" />
              </svg>
            </div>
            <div class="illust-card card-3">
              <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M32 10 C18 10 10 22 10 32 C10 42 18 48 22 52 L42 52 C46 48 54 42 54 32 C54 22 46 10 32 10Z" stroke="var(--c-amber)" />
                <line x1="22" y1="56" x2="42" y2="56" stroke="var(--c-amber)" />
                <line x1="24" y1="60" x2="40" y2="60" stroke="var(--c-amber)" />
                <path d="M26 34 L32 26 L38 34" stroke="var(--c-amber)" />
                <line x1="32" y1="26" x2="32" y2="44" stroke="var(--c-amber)" />
              </svg>
            </div>
          </div>

          <p class="aside-quote">找到属于你的<strong>热爱</strong></p>
        </div>
      </div>

      <!-- Right: form -->
      <div class="auth-main">
        <div class="form-wrap">
          <div class="form-header">
            <h1>欢迎回来</h1>
            <p>登录后进入你的社团空间</p>
          </div>

          <form class="auth-form" @submit.prevent="handleLogin">
            <div class="field">
              <label for="login-user">账号</label>
              <input
                id="login-user"
                v-model.trim="form.username"
                type="text"
                placeholder="请输入用户名"
                autocomplete="username"
              />
            </div>

            <div class="field">
              <label for="login-pass">密码</label>
              <input
                id="login-pass"
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                autocomplete="current-password"
              />
            </div>

            <div class="field-row">
              <label class="checkbox-label">
                <input v-model="form.remember" type="checkbox" />
                <span>记住我</span>
              </label>
            </div>

            <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>

            <button type="submit" class="btn-submit" :disabled="loading">
              {{ loading ? '登录中...' : '登 录' }}
            </button>

            <button type="button" class="btn-sso" :disabled="loading" @click="handleSsoRedirect">
              校园统一认证登录
            </button>
          </form>

          <div class="form-footer">
            <span class="footer-text">还没有账号？</span>
            <router-link to="/register" class="footer-link">立即注册</router-link>
            <span class="footer-sep">&middot;</span>
            <router-link to="/" class="footer-link">返回主页</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  --c-coral: #e0583a;
  --c-coral-deep: #c44a2f;
  --c-teal: #1b9e8f;
  --c-amber: #d4a020;
  --c-surface: #fdfaf8;
  --c-cream: #f6efe9;
  --c-ink: #2c1e16;
  --c-ink-2: #7a6b62;
  --c-ink-3: #b5a89f;
  --c-rule: #e8ddd6;

  --font-display: "LXGW WenKai", "Songti SC", "Noto Serif SC", serif;
  --font-body: "Helvetica Neue", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;

  min-height: 100vh;
  background: var(--c-surface);
  font-family: var(--font-body);
  color: var(--c-ink);
  position: relative;
  overflow: hidden;
}

/* ── Background shapes ── */
.bg-shapes {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.bg-shape-1 {
  width: 360px;
  height: 360px;
  background: rgba(224, 88, 58, 0.07);
  top: -10%;
  right: 15%;
}

.bg-shape-2 {
  width: 280px;
  height: 280px;
  background: rgba(27, 158, 143, 0.06);
  bottom: 10%;
  left: 10%;
}

.bg-shape-3 {
  width: 200px;
  height: 200px;
  background: rgba(212, 160, 32, 0.05);
  top: 50%;
  left: 40%;
}

/* ── Layout ── */
.auth-layout {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

/* ── Left aside ── */
.auth-aside {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  background: var(--c-cream);
  border-right: 1px solid var(--c-rule);
}

.aside-content {
  max-width: 340px;
  text-align: center;
}

.aside-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  margin-bottom: 48px;
}

.brand-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--c-coral);
}

.brand-name {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  color: var(--c-ink);
  letter-spacing: 0.5px;
}

/* Illustration cards */
.aside-illust {
  position: relative;
  height: 280px;
  margin-bottom: 40px;
}

.illust-card {
  position: absolute;
  width: 120px;
  background: #fff;
  padding: 14px 14px 10px;
  border-radius: 4px;
  box-shadow: 0 6px 24px rgba(44, 30, 22, 0.07), 0 1px 3px rgba(44, 30, 22, 0.05);
  opacity: 0;
  transition: all 0.6s cubic-bezier(0.22, 1, 0.36, 1);
}

.illust-card svg {
  width: 100%;
  display: block;
}

.card-1 {
  top: 10px;
  left: 20px;
  transform: translateY(16px) rotate(-7deg);
  transition-delay: 0.2s;
}

.card-2 {
  top: 40px;
  right: 20px;
  transform: translateY(16px) rotate(5deg);
  transition-delay: 0.35s;
}

.card-3 {
  bottom: 10px;
  left: 50%;
  margin-left: -60px;
  transform: translateY(16px) rotate(2deg);
  transition-delay: 0.5s;
}

.alive .card-1 {
  opacity: 1;
  transform: translateY(0) rotate(-7deg);
}

.alive .card-2 {
  opacity: 1;
  transform: translateY(0) rotate(5deg);
}

.alive .card-3 {
  opacity: 1;
  transform: translateY(0) rotate(2deg);
}

.aside-quote {
  font-family: var(--font-display);
  font-size: 22px;
  color: var(--c-ink-2);
  margin: 0;
  line-height: 1.5;
}

.aside-quote strong {
  color: var(--c-coral);
  font-weight: 700;
}

/* ── Right form panel ── */
.auth-main {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
}

.form-wrap {
  width: 100%;
  max-width: 380px;
  opacity: 0;
  transform: translateY(16px);
  transition: all 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.15s;
}

.alive .form-wrap {
  opacity: 1;
  transform: translateY(0);
}

.form-header {
  margin-bottom: 36px;
}

.form-header h1 {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
  color: var(--c-ink);
}

.form-header p {
  font-size: 15px;
  color: var(--c-ink-2);
  margin: 0;
}

/* Form */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field label {
  font-size: 13px;
  font-weight: 500;
  color: var(--c-ink);
}

.field input {
  height: 44px;
  border: 1.5px solid var(--c-rule);
  border-radius: 8px;
  padding: 0 14px;
  font-size: 15px;
  font-family: var(--font-body);
  background: #fff;
  color: var(--c-ink);
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.field input::placeholder {
  color: var(--c-ink-3);
}

.field input:focus {
  border-color: var(--c-coral);
  box-shadow: 0 0 0 3px rgba(224, 88, 58, 0.08);
}

.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--c-ink-2);
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: var(--c-coral);
}

.error-msg {
  margin: 0;
  font-size: 13px;
  color: #d94040;
  background: rgba(217, 64, 64, 0.06);
  padding: 8px 12px;
  border-radius: 6px;
}

.btn-submit {
  height: 46px;
  border: none;
  border-radius: 8px;
  background: var(--c-coral);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  font-family: var(--font-body);
  cursor: pointer;
  transition: background 0.2s, transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 10px rgba(224, 88, 58, 0.15);
}

.btn-submit:hover {
  background: var(--c-coral-deep);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(224, 88, 58, 0.22);
}

.btn-submit:disabled {
  background: #d4a89a;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-sso {
  height: 46px;
  border: 1.5px solid var(--c-teal);
  border-radius: 8px;
  background: transparent;
  color: var(--c-teal);
  font-size: 14px;
  font-weight: 500;
  font-family: var(--font-body);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.btn-sso:hover {
  background: var(--c-teal);
  color: #fff;
}

.btn-sso:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Footer links */
.form-footer {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid var(--c-rule);
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.footer-text {
  color: var(--c-ink-3);
}

.footer-link {
  color: var(--c-coral);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.footer-link:hover {
  color: var(--c-coral-deep);
}

.footer-sep {
  color: var(--c-ink-3);
  opacity: 0.4;
}

/* ── Responsive ── */
@media (max-width: 900px) {
  .auth-layout {
    grid-template-columns: 1fr;
  }

  .auth-aside {
    display: none;
  }

  .auth-main {
    min-height: 100vh;
    padding: 32px 20px;
  }

  .form-wrap {
    max-width: 400px;
  }

  .form-header h1 {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .form-header h1 {
    font-size: 24px;
  }

  .field input {
    height: 42px;
    font-size: 14px;
  }

  .btn-submit,
  .btn-sso {
    height: 44px;
  }
}

/* ── Reduced motion ── */
@media (prefers-reduced-motion: reduce) {
  .form-wrap,
  .illust-card {
    opacity: 1;
    transform: none;
    transition: none;
  }
}
</style>
