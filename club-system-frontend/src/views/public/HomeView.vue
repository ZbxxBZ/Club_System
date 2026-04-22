<script setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue'

const visible = ref(false)
const mouseX = ref(0)
const mouseY = ref(0)

const onMouseMove = (e) => {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 2
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 2
}

// Scroll-triggered section reveals
const journey = ref(null)
const caps = ref(null)
const ctaBand = ref(null)
const revealed = reactive({ journey: false, caps: false, ctaBand: false })

let observer = null
const setupObserver = () => {
  observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return
      const key = entry.target.dataset.reveal
      if (key && !revealed[key]) {
        revealed[key] = true
        observer.unobserve(entry.target)
      }
    })
  }, { threshold: 0.15 })

  const refs = { journey, caps, ctaBand }
  Object.entries(refs).forEach(([, elRef]) => {
    if (elRef.value) observer.observe(elRef.value)
  })
}

// Stat counter animation
const statValues = reactive({ clubs: 0, events: 0, members: 0 })
const statTargets = { clubs: 42, events: 128, members: 860 }
const statsAnimated = ref(false)

const animateCounter = (key, target, duration = 1200) => {
  const start = performance.now()
  const step = (now) => {
    const elapsed = now - start
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 4)
    statValues[key] = Math.round(eased * target)
    if (progress < 1) requestAnimationFrame(step)
  }
  requestAnimationFrame(step)
}

const startStatCounters = () => {
  if (statsAnimated.value) return
  statsAnimated.value = true
  animateCounter('clubs', statTargets.clubs, 1000)
  animateCounter('events', statTargets.events, 1200)
  animateCounter('members', statTargets.members, 1400)
}

let statsObserver = null
const statsRef = ref(null)

const formatStat = (val, key) => {
  if (key === 'members') {
    if (val >= 1000) return (val / 1000).toFixed(1) + 'k'
    return String(val)
  }
  return String(val)
}

// Marquee pause on hover
const marqueePaused = ref(false)

onMounted(() => {
  requestAnimationFrame(() => { visible.value = true })
  window.addEventListener('mousemove', onMouseMove, { passive: true })
  setupObserver()

  statsObserver = new IntersectionObserver((entries) => {
    if (entries[0]?.isIntersecting) {
      setTimeout(startStatCounters, 900)
      statsObserver.disconnect()
    }
  }, { threshold: 0.5 })
  if (statsRef.value) statsObserver.observe(statsRef.value)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onMouseMove)
  if (observer) observer.disconnect()
  if (statsObserver) statsObserver.disconnect()
})
</script>

<template>
  <div class="home" :class="{ alive: visible }">
    <!-- ── NAV ── -->
    <nav class="topbar">
      <div class="topbar-inner">
        <div class="brand">
          <span class="brand-dot"></span>
          <span class="brand-name">社团广场</span>
        </div>
        <div class="topbar-actions">
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="nav-cta">加入我们</router-link>
        </div>
      </div>
    </nav>

    <!-- ── HERO ── -->
    <section class="hero">
      <!-- Large background shapes that respond to mouse -->
      <div class="hero-shapes" aria-hidden="true">
        <div class="shape shape-1" :style="{ transform: `translate(${mouseX * -12}px, ${mouseY * -8}px)` }"></div>
        <div class="shape shape-2" :style="{ transform: `translate(${mouseX * 18}px, ${mouseY * 10}px)` }"></div>
        <div class="shape shape-3" :style="{ transform: `translate(${mouseX * -8}px, ${mouseY * 14}px)` }"></div>
        <div class="shape shape-4" :style="{ transform: `translate(${mouseX * 10}px, ${mouseY * -12}px)` }"></div>
      </div>

      <div class="hero-content">
        <div class="hero-eyebrow">
          <span class="eyebrow-line"></span>
          <span class="eyebrow-text">2026 · 招新季</span>
          <span class="eyebrow-line"></span>
        </div>

        <h1 class="hero-title">
          <span class="title-line title-line-1">找到属于你的</span>
          <span class="title-line title-line-2">
            <span class="title-highlight">热爱</span>
          </span>
        </h1>

        <p class="hero-desc">
          一个平台，连接校园里每一份创意、每一次相聚、每一段故事。
        </p>

        <div class="hero-actions">
          <router-link to="/register" class="cta-main">
            <span>开始探索</span>
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M4 9h10M10 5l4 4-4 4" />
            </svg>
          </router-link>
          <router-link to="/login" class="cta-secondary">已有账号</router-link>
        </div>

        <!-- Stats ticker -->
        <div ref="statsRef" class="hero-stats">
          <div class="stat">
            <span class="stat-num">{{ statValues.clubs }}</span>
            <span class="stat-label">活跃社团</span>
          </div>
          <span class="stat-sep"></span>
          <div class="stat">
            <span class="stat-num">{{ statValues.events }}</span>
            <span class="stat-label">本月活动</span>
          </div>
          <span class="stat-sep"></span>
          <div class="stat">
            <span class="stat-num">{{ formatStat(statValues.members, 'members') }}</span>
            <span class="stat-label">在册成员</span>
          </div>
        </div>
      </div>

      <!-- Right-side illustration cluster -->
      <div class="hero-illust" aria-hidden="true">
        <!-- Stacked "polaroid" cards with hand-drawn icons -->
        <div class="polaroid pol-1">
          <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <ellipse cx="32" cy="42" rx="20" ry="18" stroke="var(--c-coral)" />
            <circle cx="32" cy="40" r="6" stroke="var(--c-coral)" />
            <line x1="32" y1="24" x2="32" y2="6" stroke="var(--c-coral)" />
            <rect x="26" y="2" width="12" height="8" rx="2" stroke="var(--c-coral)" />
          </svg>
          <span class="pol-tag">音乐社</span>
        </div>
        <div class="polaroid pol-2">
          <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="32" cy="32" r="22" stroke="var(--c-teal)" />
            <path d="M10 32 Q32 24 54 32" stroke="var(--c-teal)" />
            <path d="M10 32 Q32 40 54 32" stroke="var(--c-teal)" />
            <line x1="32" y1="10" x2="32" y2="54" stroke="var(--c-teal)" />
          </svg>
          <span class="pol-tag">篮球社</span>
        </div>
        <div class="polaroid pol-3">
          <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M32 8 L52 24 L52 52 L12 52 L12 24 Z" stroke="var(--c-amber)" />
            <rect x="24" y="38" width="16" height="14" rx="1" stroke="var(--c-amber)" />
            <line x1="32" y1="8" x2="32" y2="2" stroke="var(--c-amber)" />
            <circle cx="32" cy="30" r="4" stroke="var(--c-amber)" />
          </svg>
          <span class="pol-tag">摄影社</span>
        </div>
        <div class="polaroid pol-4">
          <svg viewBox="0 0 64 64" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M32 10 C18 10 10 22 10 32 C10 42 18 48 22 52 L42 52 C46 48 54 42 54 32 C54 22 46 10 32 10Z" stroke="var(--c-indigo)" />
            <line x1="22" y1="56" x2="42" y2="56" stroke="var(--c-indigo)" />
            <line x1="24" y1="60" x2="40" y2="60" stroke="var(--c-indigo)" />
            <path d="M26 34 L32 26 L38 34" stroke="var(--c-indigo)" />
            <line x1="32" y1="26" x2="32" y2="44" stroke="var(--c-indigo)" />
          </svg>
          <span class="pol-tag">编程社</span>
        </div>
      </div>
    </section>

    <!-- ── MARQUEE ── -->
    <div class="marquee-strip" aria-hidden="true" @mouseenter="marqueePaused = true" @mouseleave="marqueePaused = false">
      <div class="marquee-track" :class="{ paused: marqueePaused }">
        <span v-for="n in 3" :key="n" class="marquee-group">
          <span class="mq-item">音乐</span>
          <span class="mq-dot"></span>
          <span class="mq-item">篮球</span>
          <span class="mq-dot"></span>
          <span class="mq-item">摄影</span>
          <span class="mq-dot"></span>
          <span class="mq-item">编程</span>
          <span class="mq-dot"></span>
          <span class="mq-item">辩论</span>
          <span class="mq-dot"></span>
          <span class="mq-item">戏剧</span>
          <span class="mq-dot"></span>
          <span class="mq-item">志愿者</span>
          <span class="mq-dot"></span>
          <span class="mq-item">动漫</span>
          <span class="mq-dot"></span>
        </span>
      </div>
    </div>

    <!-- ── HOW IT WORKS (replaces boring role cards) ── -->
    <section ref="journey" data-reveal="journey" class="journey" :class="{ revealed: revealed.journey }">
      <div class="journey-inner">
        <h2 class="sec-heading">三步开始</h2>
        <div class="steps">
          <div class="step">
            <span class="step-num">01</span>
            <div class="step-body">
              <h3>注册身份</h3>
              <p>用学号快速注册，系统自动识别你的角色——学生、社团干部，还是学校管理员。</p>
            </div>
          </div>
          <div class="step">
            <span class="step-num">02</span>
            <div class="step-body">
              <h3>发现社团</h3>
              <p>按兴趣筛选，查看社团简介和活动记录，一键申请加入。</p>
            </div>
          </div>
          <div class="step">
            <span class="step-num">03</span>
            <div class="step-body">
              <h3>参与活动</h3>
              <p>报名感兴趣的活动，扫码签到，积累参与记录。</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── CAPABILITIES (editorial layout, not card grid) ── -->
    <section ref="caps" data-reveal="caps" class="caps" :class="{ revealed: revealed.caps }">
      <div class="caps-inner">
        <h2 class="sec-heading">不只是管理工具</h2>
        <div class="cap-rows">
          <div class="cap-row">
            <div class="cap-label">
              <span class="cap-idx">A</span>
              <h3>全流程审批</h3>
            </div>
            <p class="cap-desc">社团成立、年审、注销——每一步都在线上完成，审批进度实时可查。不再需要跑腿盖章。</p>
          </div>
          <div class="cap-row">
            <div class="cap-label">
              <span class="cap-idx">B</span>
              <h3>活动全闭环</h3>
            </div>
            <p class="cap-desc">从策划到总结，报名、签到、反馈一条线串起。每场活动都有完整的数据沉淀。</p>
          </div>
          <div class="cap-row">
            <div class="cap-label">
              <span class="cap-idx">C</span>
              <h3>经费可追溯</h3>
            </div>
            <p class="cap-desc">每一笔收入和支出都留痕，多级审批确保合规，年终对账一键导出。</p>
          </div>
          <div class="cap-row">
            <div class="cap-label">
              <span class="cap-idx">D</span>
              <h3>数据看得见</h3>
            </div>
            <p class="cap-desc">社团规模、活动热度、经费趋势——关键数据可视化呈现，辅助决策不靠猜。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ── CTA BAND ── -->
    <section ref="ctaBand" data-reveal="ctaBand" class="cta-band" :class="{ revealed: revealed.ctaBand }">
      <div class="cta-band-inner">
        <h2>新学期，新社团</h2>
        <p>无论你想加入一个社团，还是创建一个属于自己的——从这里开始。</p>
        <div class="cta-band-actions">
          <router-link to="/register" class="cta-main cta-main-light">
            <span>注册账号</span>
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M4 9h10M10 5l4 4-4 4" />
            </svg>
          </router-link>
          <router-link to="/login" class="cta-secondary cta-secondary-light">已有账号，登录</router-link>
        </div>
      </div>
    </section>

    <!-- ── FOOTER ── -->
    <footer class="foot">
      <span class="foot-brand">社团广场</span>
      <span class="foot-sep">&middot;</span>
      <span class="foot-copy">北京信息科技大学 &copy; 2026</span>
    </footer>
  </div>
</template>

<style scoped>
.home {
  --c-coral: #e0583a;
  --c-coral-deep: #c44a2f;
  --c-teal: #1b9e8f;
  --c-teal-deep: #167f73;
  --c-amber: #d4a020;
  --c-indigo: #5b6abf;
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
  color: var(--c-ink);
  font-family: var(--font-body);
  overflow-x: hidden;
}

/* ── TOPBAR ── */
.topbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 60px;
  background: rgba(253, 250, 248, 0.82);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}

.topbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
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
  letter-spacing: 0.5px;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link {
  text-decoration: none;
  color: var(--c-ink-2);
  font-size: 14px;
  transition: color 0.2s;
}

.nav-link:hover {
  color: var(--c-ink);
}

.nav-cta {
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
  color: var(--c-coral);
  border: 1.5px solid var(--c-coral);
  border-radius: 6px;
  padding: 6px 16px;
  transition: background 0.2s, color 0.2s;
}

.nav-cta:hover {
  background: var(--c-coral);
  color: #fff;
}

/* ── HERO ── */
.hero {
  position: relative;
  min-height: 100vh;
  min-height: 100svh;
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: center;
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 32px 60px;
}

.hero-shapes {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  will-change: transform;
  transition: transform 0.8s cubic-bezier(0.22, 1, 0.36, 1);
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: rgba(224, 88, 58, 0.1);
  top: -5%;
  right: 10%;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: rgba(27, 158, 143, 0.08);
  bottom: 15%;
  left: 5%;
}

.shape-3 {
  width: 250px;
  height: 250px;
  background: rgba(212, 160, 32, 0.07);
  top: 40%;
  right: 30%;
}

.shape-4 {
  width: 200px;
  height: 200px;
  background: rgba(91, 106, 191, 0.06);
  top: 20%;
  left: 25%;
}

.hero-content {
  position: relative;
  z-index: 2;
}

.hero-eyebrow {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;
  opacity: 0;
  transform: translateY(12px);
  transition: all 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.2s;
}

.alive .hero-eyebrow {
  opacity: 1;
  transform: translateY(0);
}

.eyebrow-line {
  flex: 0 0 24px;
  height: 1px;
  background: var(--c-ink-3);
}

.eyebrow-text {
  font-size: 12px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--c-ink-3);
  font-weight: 500;
}

.hero-title {
  margin: 0 0 24px;
  font-family: var(--font-display);
  font-weight: 700;
  line-height: 1.2;
}

.title-line {
  display: block;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.7s cubic-bezier(0.22, 1, 0.36, 1);
}

.title-line-1 {
  font-size: clamp(32px, 4.5vw, 48px);
  color: var(--c-ink);
  transition-delay: 0.3s;
}

.title-line-2 {
  font-size: clamp(48px, 7vw, 80px);
  transition-delay: 0.45s;
}

.alive .title-line {
  opacity: 1;
  transform: translateY(0);
}

.title-highlight {
  color: var(--c-coral);
  position: relative;
}

.title-highlight::after {
  content: '';
  position: absolute;
  left: -4px;
  right: -4px;
  bottom: 4px;
  height: 12px;
  background: rgba(224, 88, 58, 0.12);
  border-radius: 4px;
  z-index: -1;
}

.hero-desc {
  font-size: clamp(16px, 1.6vw, 19px);
  line-height: 1.8;
  color: var(--c-ink-2);
  max-width: 440px;
  margin: 0 0 36px;
  opacity: 0;
  transform: translateY(12px);
  transition: all 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.55s;
}

.alive .hero-desc {
  opacity: 1;
  transform: translateY(0);
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 56px;
  opacity: 0;
  transform: translateY(12px);
  transition: all 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.65s;
}

.alive .hero-actions {
  opacity: 1;
  transform: translateY(0);
}

.cta-main {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  background: var(--c-coral);
  padding: 14px 28px;
  border-radius: 8px;
  transition: background 0.2s, transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 4px 16px rgba(224, 88, 58, 0.2);
}

.cta-main:hover {
  background: var(--c-coral-deep);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(224, 88, 58, 0.28);
}

.cta-main svg {
  transition: transform 0.2s;
}

.cta-main:hover svg {
  transform: translateX(3px);
}

.cta-secondary {
  text-decoration: none;
  font-size: 14px;
  color: var(--c-ink-2);
  border-bottom: 1px solid var(--c-rule);
  padding-bottom: 2px;
  transition: color 0.2s, border-color 0.2s;
}

.cta-secondary:hover {
  color: var(--c-ink);
  border-color: var(--c-ink);
}

/* Stats */
.hero-stats {
  display: flex;
  align-items: center;
  gap: 24px;
  opacity: 0;
  transition: opacity 0.6s ease 0.8s;
}

.alive .hero-stats {
  opacity: 1;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--c-ink);
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: var(--c-ink-3);
  margin-top: 4px;
}

.stat-sep {
  width: 1px;
  height: 32px;
  background: var(--c-rule);
}

/* ── POLAROID CARDS ── */
.hero-illust {
  position: relative;
  z-index: 2;
  height: 480px;
}

.polaroid {
  position: absolute;
  width: 160px;
  background: #fff;
  padding: 16px 16px 12px;
  border-radius: 4px;
  box-shadow: 0 8px 32px rgba(44, 30, 22, 0.08), 0 1px 4px rgba(44, 30, 22, 0.06);
  transition: transform 0.4s cubic-bezier(0.22, 1, 0.36, 1);
  opacity: 0;
  transform: translateY(24px) rotate(0deg);
}

.polaroid svg {
  width: 100%;
  height: auto;
  display: block;
}

.pol-tag {
  display: block;
  text-align: center;
  font-family: var(--font-display);
  font-size: 14px;
  color: var(--c-ink-2);
  margin-top: 10px;
  font-weight: 500;
}

.pol-1 {
  top: 20px;
  left: 10%;
  transition-delay: 0.4s;
}

.pol-2 {
  top: 80px;
  right: 5%;
  transition-delay: 0.55s;
}

.pol-3 {
  bottom: 60px;
  left: 25%;
  transition-delay: 0.7s;
}

.pol-4 {
  bottom: 20px;
  right: 15%;
  transition-delay: 0.85s;
}

.alive .pol-1 {
  opacity: 1;
  transform: translateY(0) rotate(-6deg);
}

.alive .pol-2 {
  opacity: 1;
  transform: translateY(0) rotate(4deg);
}

.alive .pol-3 {
  opacity: 1;
  transform: translateY(0) rotate(3deg);
}

.alive .pol-4 {
  opacity: 1;
  transform: translateY(0) rotate(-3deg);
}

.polaroid:hover {
  transform: translateY(-8px) rotate(0deg) scale(1.04) !important;
  box-shadow: 0 16px 48px rgba(44, 30, 22, 0.12), 0 2px 8px rgba(44, 30, 22, 0.08);
  z-index: 10;
}

/* ── MARQUEE ── */
.marquee-strip {
  overflow: hidden;
  padding: 20px 0;
  background: var(--c-cream);
  border-top: 1px solid var(--c-rule);
  border-bottom: 1px solid var(--c-rule);
}

.marquee-track {
  display: flex;
  width: max-content;
  animation: marquee 30s linear infinite;
}

.marquee-group {
  display: flex;
  align-items: center;
  gap: 28px;
  padding-right: 28px;
}

.mq-item {
  font-family: var(--font-display);
  font-size: 15px;
  color: var(--c-ink-2);
  white-space: nowrap;
  letter-spacing: 1px;
}

.mq-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--c-ink-3);
  flex-shrink: 0;
}

@keyframes marquee {
  to { transform: translateX(-33.333%); }
}

/* ── JOURNEY / STEPS ── */
.journey {
  padding: clamp(64px, 10vw, 120px) 0;
}

.journey-inner {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 32px;
}

.sec-heading {
  font-family: var(--font-display);
  font-size: clamp(28px, 4vw, 40px);
  font-weight: 700;
  margin: 0 0 56px;
  color: var(--c-ink);
}

.steps {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.step {
  display: grid;
  grid-template-columns: 72px 1fr;
  gap: 0;
  padding: 32px 0;
  border-top: 1px solid var(--c-rule);
}

.step:last-child {
  border-bottom: 1px solid var(--c-rule);
}

.step-num {
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 700;
  color: var(--c-coral);
  line-height: 1;
  opacity: 0.6;
}

.step-body h3 {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--c-ink);
}

.step-body p {
  font-size: 15px;
  line-height: 1.75;
  color: var(--c-ink-2);
  margin: 0;
  max-width: 50ch;
}

/* ── CAPABILITIES ── */
.caps {
  padding: clamp(64px, 10vw, 100px) 0;
  background: var(--c-cream);
}

.caps-inner {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 32px;
}

.cap-rows {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.cap-row {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 32px;
  padding: 28px 0;
  border-top: 1px solid var(--c-rule);
  align-items: baseline;
}

.cap-row:last-child {
  border-bottom: 1px solid var(--c-rule);
}

.cap-label {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.cap-idx {
  font-family: var(--font-display);
  font-size: 14px;
  font-weight: 700;
  color: var(--c-teal);
  width: 24px;
  height: 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1.5px solid var(--c-teal);
  border-radius: 4px;
  flex-shrink: 0;
}

.cap-label h3 {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: var(--c-ink);
}

.cap-desc {
  font-size: 15px;
  line-height: 1.75;
  color: var(--c-ink-2);
  margin: 0;
  max-width: 50ch;
}

/* ── CTA BAND ── */
.cta-band {
  padding: clamp(64px, 10vw, 100px) 0;
  background: var(--c-ink);
  color: #faf0ea;
}

.cta-band-inner {
  max-width: 640px;
  margin: 0 auto;
  padding: 0 32px;
  text-align: center;
}

.cta-band h2 {
  font-family: var(--font-display);
  font-size: clamp(28px, 4vw, 40px);
  font-weight: 700;
  margin: 0 0 12px;
  color: #faf0ea;
}

.cta-band p {
  font-size: 16px;
  line-height: 1.7;
  color: rgba(250, 240, 234, 0.6);
  margin: 0 0 36px;
}

.cta-band-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.cta-main-light {
  background: var(--c-coral);
  color: #fff;
  box-shadow: 0 4px 20px rgba(224, 88, 58, 0.3);
}

.cta-main-light:hover {
  background: #e86a4f;
  box-shadow: 0 8px 28px rgba(224, 88, 58, 0.4);
}

.cta-secondary-light {
  color: rgba(250, 240, 234, 0.5);
  border-bottom-color: rgba(250, 240, 234, 0.2);
}

.cta-secondary-light:hover {
  color: #faf0ea;
  border-color: #faf0ea;
}

/* ── FOOTER ── */
.foot {
  padding: 24px 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 13px;
  color: var(--c-ink-3);
  border-top: 1px solid var(--c-rule);
}

.foot-brand {
  font-family: var(--font-display);
  font-weight: 600;
  color: var(--c-ink-2);
}

.foot-sep {
  opacity: 0.4;
}

/* ── RESPONSIVE: TABLET ── */
@media (max-width: 1023px) {
  .hero {
    grid-template-columns: 1fr;
    min-height: auto;
    padding-top: 120px;
    padding-bottom: 40px;
    gap: 48px;
  }

  .hero-illust {
    height: 320px;
    max-width: 500px;
    margin: 0 auto;
  }

  .polaroid {
    width: 130px;
  }

  .cap-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}

/* ── RESPONSIVE: MOBILE ── */
@media (max-width: 640px) {
  .topbar-inner {
    padding: 0 16px;
  }

  .hero {
    padding: 100px 16px 32px;
  }

  .hero-illust {
    height: 260px;
  }

  .polaroid {
    width: 110px;
    padding: 10px 10px 8px;
  }

  .pol-tag {
    font-size: 12px;
  }

  .pol-1 { left: 0; }
  .pol-2 { right: 0; }
  .pol-3 { left: 10%; }
  .pol-4 { right: 5%; }

  .hero-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }

  .hero-stats {
    gap: 16px;
  }

  .stat-num {
    font-size: 22px;
  }

  .marquee-group {
    gap: 20px;
    padding-right: 20px;
  }

  .journey-inner,
  .caps-inner,
  .cta-band-inner {
    padding: 0 16px;
  }

  .step {
    grid-template-columns: 56px 1fr;
  }

  .step-num {
    font-size: 28px;
  }

  .sec-heading {
    margin-bottom: 40px;
  }

  .cta-band-actions {
    flex-direction: column;
  }

  .cta-main {
    width: 100%;
    justify-content: center;
  }
}

/* ── MARQUEE PAUSE ── */
.marquee-track.paused {
  animation-play-state: paused;
}

/* ── SCROLL REVEAL: JOURNEY ── */
.journey .sec-heading,
.journey .step {
  opacity: 0;
  transform: translateY(24px);
  transition: opacity 0.6s cubic-bezier(0.22, 1, 0.36, 1),
              transform 0.6s cubic-bezier(0.22, 1, 0.36, 1);
}

.journey.revealed .sec-heading {
  opacity: 1;
  transform: translateY(0);
}

.journey.revealed .step:nth-child(1) {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.15s;
}

.journey.revealed .step:nth-child(2) {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.3s;
}

.journey.revealed .step:nth-child(3) {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.45s;
}

.journey .step-num {
  transition: color 0.3s, transform 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

.journey .step:hover .step-num {
  color: var(--c-coral-deep);
  transform: scale(1.12);
}

/* ── SCROLL REVEAL: CAPABILITIES ── */
.caps .sec-heading,
.caps .cap-row {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s cubic-bezier(0.22, 1, 0.36, 1),
              transform 0.6s cubic-bezier(0.22, 1, 0.36, 1);
}

.caps.revealed .sec-heading {
  opacity: 1;
  transform: translateY(0);
}

.caps.revealed .cap-row:nth-child(1) {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.1s;
}

.caps.revealed .cap-row:nth-child(2) {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.2s;
}

.caps.revealed .cap-row:nth-child(3) {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.3s;
}

.caps.revealed .cap-row:nth-child(4) {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.4s;
}

.caps .cap-idx {
  transition: background 0.25s, color 0.25s;
}

.caps .cap-row:hover .cap-idx {
  background: var(--c-teal);
  color: #fdfaf8;
}

/* ── SCROLL REVEAL: CTA BAND ── */
.cta-band h2,
.cta-band p,
.cta-band-actions {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s cubic-bezier(0.22, 1, 0.36, 1),
              transform 0.6s cubic-bezier(0.22, 1, 0.36, 1);
}

.cta-band.revealed h2 {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.1s;
}

.cta-band.revealed p {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.25s;
}

.cta-band.revealed .cta-band-actions {
  opacity: 1;
  transform: translateY(0);
  transition-delay: 0.4s;
}

/* ── STAT NUMBER TABULAR RENDERING ── */
.stat-num {
  font-variant-numeric: tabular-nums;
  min-width: 2.5ch;
}

/* ── REDUCED MOTION ── */
@media (prefers-reduced-motion: reduce) {
  .title-line,
  .hero-eyebrow,
  .hero-desc,
  .hero-actions,
  .hero-stats,
  .polaroid {
    opacity: 1;
    transform: none;
    transition: none;
  }

  .shape {
    transition: none;
  }

  .marquee-track {
    animation: none;
  }

  .journey .sec-heading,
  .journey .step,
  .caps .sec-heading,
  .caps .cap-row,
  .cta-band h2,
  .cta-band p,
  .cta-band-actions {
    opacity: 1;
    transform: none;
    transition: none;
  }
}
</style>
