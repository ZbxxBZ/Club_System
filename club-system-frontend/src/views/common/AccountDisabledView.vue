<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleBackLogin = () => {
  authStore.logout()
  router.replace('/login')
}
</script>

<template>
  <div class="state-page">
    <div class="state-card warning">
      <h1>账号状态受限</h1>
      <p>
        当前账号状态不是可用状态，可能因为毕业自动退社、社团注销导致冻结，或账号被学校管理员停用。
        如有疑问请联系团委管理员。
      </p>
      <button type="button" @click="handleBackLogin">重新登录</button>
    </div>
  </div>
</template>

<style scoped>
.state-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff7ed;
}

.state-card {
  width: min(560px, calc(100vw - 32px));
  border-radius: 14px;
  background: #fff;
  border: 1px solid #fed7aa;
  padding: 28px;
  text-align: center;
}

h1 {
  margin: 0;
  font-size: 24px;
  color: #9a3412;
}

p {
  margin: 14px 0 22px;
  color: #7c2d12;
  line-height: 1.7;
}

button {
  height: 40px;
  padding: 0 18px;
  border-radius: 8px;
  border: none;
  background: #ea580c;
  color: #fff;
  cursor: pointer;
}
</style>
