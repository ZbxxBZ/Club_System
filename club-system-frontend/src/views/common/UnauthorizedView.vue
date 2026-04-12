<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="state-page">
    <div class="state-card">
      <h1>无权限访问</h1>
      <p>当前账号不具备该功能权限，请联系学校管理员分配角色或权限。</p>
      <button type="button" @click="router.push('/')">返回首页</button>
    </div>
  </div>
</template>

<style scoped>
.state-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f6f8fb;
}

.state-card {
  width: min(520px, calc(100vw - 32px));
  border-radius: 14px;
  background: #fff;
  border: 1px solid #e5e8ef;
  padding: 28px;
  text-align: center;
}

h1 {
  margin: 0;
  font-size: 24px;
  color: #1f2937;
}

p {
  margin: 14px 0 22px;
  color: #4b5563;
  line-height: 1.7;
}

button {
  height: 40px;
  padding: 0 18px;
  border-radius: 8px;
  border: none;
  background: #2563eb;
  color: #fff;
  cursor: pointer;
}
</style>
