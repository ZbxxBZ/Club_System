<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '../../api/auth'

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

const loading = ref(false)
const errorMessage = ref('')
const router = useRouter()

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
  <div class="register-page">
    <div class="register-card">
      <h1>用户注册</h1>
      <p class="sub-title">创建账号后即可登录系统</p>

      <form class="register-form" @submit.prevent="handleRegister">
        <label>
          <span>账号</span>
          <input v-model.trim="form.username" type="text" placeholder="请输入用户名" />
        </label>

        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" placeholder="请输入密码" />
        </label>

        <label>
          <span>确认密码</span>
          <input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
        </label>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

        <button type="submit" :disabled="loading">{{ loading ? '注册中...' : '注 册' }}</button>
        <button type="button" class="home-btn" @click="router.push('/')">返回主页</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.register-card {
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

.register-form {
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

.home-btn:hover {
  background: #f2f3f5;
}
</style>
