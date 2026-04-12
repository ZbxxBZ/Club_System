<template>
  <div class="home-page">
    <header class="home-header">
      <div class="logo">学校社团管理系统</div>
      <nav class="auth-actions">
        <router-link to="/login" class="link-btn">登录</router-link>
        <router-link to="/register" class="primary-btn">注册</router-link>
      </nav>
    </header>

    <main class="hero-wrap">
      <section class="hero">
        <span class="hero-tag">数字化社团治理平台</span>
        <h1>让社团管理更高效，让校园活动更精彩</h1>
        <p>覆盖社团全生命周期、活动组织与经费监管，面向学生、社团管理员、学校管理员的一体化管理系统。</p>

        <div class="hero-actions">
          <router-link to="/register" class="primary-btn large">立即注册</router-link>
          <router-link to="/login" class="link-btn large">已有账号，去登录</router-link>
        </div>
      </section>

      <section class="hero-panel">
        <div class="panel-card">
          <h3>核心能力</h3>
          <ul>
            <li>社团成立 / 年审 / 注销流程化管理</li>
            <li>活动发起、报名、签到、总结全闭环</li>
            <li>经费收入支出可追溯与审计监管</li>
          </ul>
        </div>
      </section>
    </main>

    <section class="features">
      <article class="feature-item">
        <h4>流程规范</h4>
        <p>审批节点清晰透明，减少线下反复沟通。</p>
      </article>
      <article class="feature-item">
        <h4>数据可视</h4>
        <p>统一沉淀社团、活动、经费数据，支持统计分析。</p>
      </article>
      <article class="feature-item">
        <h4>权限清晰</h4>
        <p>按角色严格控制访问范围，保障系统安全合规。</p>
      </article>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f3f7ff 0%, #f8fafc 58%, #ffffff 100%);
}

.home-header {
  height: 70px;
  padding: 0 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #e8edf5;
}

.logo {
  font-size: 22px;
  color: #1f2a44;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.auth-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.link-btn,
.primary-btn {
  text-decoration: none;
  font-size: 14px;
  border-radius: 10px;
  padding: 9px 16px;
  transition: all 0.2s ease;
}

.link-btn {
  color: #409eff;
  border: 1px solid #409eff;
  background: #fff;
}

.primary-btn {
  color: #fff;
  background: #409eff;
  border: 1px solid #409eff;
}

.link-btn:hover {
  background: #ecf5ff;
}

.primary-btn:hover {
  background: #337ecc;
  border-color: #337ecc;
}

.hero-wrap {
  max-width: 1180px;
  margin: 44px auto 0;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 28px;
}

.hero {
  background: #ffffff;
  border: 1px solid #e8edf5;
  border-radius: 18px;
  padding: 42px 40px;
  box-shadow: 0 14px 36px rgba(31, 50, 81, 0.08);
}

.hero-tag {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 999px;
  color: #206fe7;
  font-size: 13px;
  background: #eaf2ff;
  margin-bottom: 14px;
}

.hero h1 {
  margin: 0;
  font-size: 40px;
  line-height: 1.25;
  color: #1f2a44;
}

.hero p {
  margin: 16px 0 0;
  font-size: 16px;
  line-height: 1.75;
  color: #5c6372;
}

.hero-actions {
  margin-top: 28px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.large {
  padding: 11px 18px;
}

.hero-panel {
  display: flex;
}

.panel-card {
  width: 100%;
  border-radius: 18px;
  padding: 28px;
  color: #fff;
  background: linear-gradient(145deg, #3a88ff, #2463d9);
  box-shadow: 0 14px 36px rgba(33, 95, 204, 0.28);
}

.panel-card h3 {
  margin: 0;
  font-size: 24px;
}

.panel-card ul {
  margin: 16px 0 0;
  padding-left: 18px;
  line-height: 1.9;
  font-size: 15px;
}

.features {
  max-width: 1180px;
  margin: 26px auto 0;
  padding: 0 24px 34px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.feature-item {
  border: 1px solid #e8edf5;
  border-radius: 14px;
  background: #fff;
  padding: 20px 18px;
}

.feature-item h4 {
  margin: 0;
  font-size: 18px;
  color: #24324b;
}

.feature-item p {
  margin: 10px 0 0;
  color: #5f6778;
  line-height: 1.65;
  font-size: 14px;
}

@media (max-width: 980px) {
  .hero-wrap {
    grid-template-columns: 1fr;
  }

  .features {
    grid-template-columns: 1fr;
  }

  .hero h1 {
    font-size: 30px;
  }
}
</style>
