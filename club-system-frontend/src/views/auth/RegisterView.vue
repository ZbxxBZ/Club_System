<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '../../api/auth'

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

const loading = ref(false)
const errorMessage = ref('')
const visible = ref(false)
const router = useRouter()

onMounted(() => {
  requestAnimationFrame(() => { visible.value = true })
})

const checkBizResult = (response) => {
  if (response && typeof response.code !== 'undefined' && response.code !== 0) {
    throw new Error(response.message || '注册失败')
  }
}

const handleRegister = async () => {
  if (!form.username || !form.password || !form.confirmPassword) {
    errorMessage.value = '请填写完整注册信息'
    return
  }
  if (form.password !== form.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await registerApi({
      username: form.username,
      password: form.password,
    })
    checkBizResult(response)
    window.alert('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    errorMessage.value = error?.response?.data?.message || error?.message || '注册失败，请稍后重试'
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
                <path d="M32 8 L52 24 L52 52 L12 52 L12 24 Z" stroke="var(--c-teal)" />
                <rect x="24" y="38" width="16" height="14" rx="1" stroke="var(--c-teal)" />
                <line x1="32" y1="8" x2="32" y2="2" stroke="var(--c-teal)" />
                <circle cx="32" cy="30" r="4" stroke="var(--c-teal)" />
              </svg>
            </div>
            <div class="illust-card card-2">
              <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="24" cy="20" r="10" stroke="var(--c-coral)" />
                <path d="M8 54 C8 42 16 36 24 36" stroke="var(--c-coral)" />
                <circle cx="42" cy="24" r="8" stroke="var(--c-coral)" />
                <path d="M56 54 C56 44 50 38 42 38" stroke="var(--c-coral)" />
              </svg>
            </div>
            <div class="illust-card card-3">
              <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 44 L12 16 L36 8 L36 36" stroke="var(--c-amber)" />
                <circle cx="12" cy="44" r="6" stroke="var(--c-amber)" />
                <circle cx="36" cy="36" r="6" stroke="var(--c-amber)" />
                <path d="M36 8 L52 14 L52 28" stroke="var(--c-amber)" />
                <circle cx="52" cy="28" r="4" stroke="var(--c-amber)" />
              </svg>
            </div>
          </div>

          <p class="aside-quote">开启你的<strong>社团之旅</strong></p>
        </div>
      </div>

      <!-- Right: form -->
      <div class="auth-main">
        <div class="form-wrap">
          <div class="form-header">
            <h1>创建账号</h1>
            <p>注册后即可浏览社团、报名活动</p>
          </div>

          <form class="auth-form" @submit.prevent="handleRegister">
            <div class="field">
              <label for="reg-user">账号</label>
              <input
                id="reg-user"
                v-model.trim="form.username"
                type="text"
                placeholder="请输入用户名"
                autocomplete="username"
              />
            </div>

            <div class="field">
              <label for="reg-pass">密码</label>
              <input
                id="reg-pass"
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                autocomplete="new-password"
              />
            </div>

            <div class="field">
              <label for="reg-confirm">确认密码</label>
              <input
                id="reg-confirm"
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                autocomplete="new-password"
              />
            </div>

            <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>

            <button type="submit" class="btn-submit" :disabled="loading">
              {{ loading ? '注册中...' : '注 册' }}
            </button>
          </form>

          <div class="form-footer">
            <span class="footer-text">已有账号？</span>
            <router-link to="/login" class="footer-link">去登录</router-link>
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
  width: 320px;
  height: 320px;
  background: rgba(27, 158, 143, 0.07);
  top: -8%;
  left: 20%;
}

.bg-shape-2 {
  width: 260px;
  height: 260px;
  background: rgba(224, 88, 58, 0.06);
  bottom: 12%;
  right: 15%;
}

.bg-shape-3 {
  width: 180px;
  height: 180px;
  background: rgba(212, 160, 32, 0.05);
  top: 60%;
  left: 50%;
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
  transform: translateY(16px) rotate(4deg);
  transition-delay: 0.2s;
}

.card-2 {
  top: 30px;
  right: 20px;
  transform: translateY(16px) rotate(-5deg);
  transition-delay: 0.35s;
}

.card-3 {
  bottom: 10px;
  left: 50%;
  margin-left: -60px;
  transform: translateY(16px) rotate(-2deg);
  transition-delay: 0.5s;
}

.alive .card-1 {
  opacity: 1;
  transform: translateY(0) rotate(4deg);
}

.alive .card-2 {
  opacity: 1;
  transform: translateY(0) rotate(-5deg);
}

.alive .card-3 {
  opacity: 1;
  transform: translateY(0) rotate(-2deg);
}

.aside-quote {
  font-family: var(--font-display);
  font-size: 22px;
  color: var(--c-ink-2);
  margin: 0;
  line-height: 1.5;
}

.aside-quote strong {
  color: var(--c-teal);
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

  .btn-submit {
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
