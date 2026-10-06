<template>
  <div class="min-h-screen flex flex-col relative noise-overlay">
    <!-- Subtle reading-paper background -->
    <div class="fixed inset-0 pointer-events-none overflow-hidden z-0">
      <div class="site-paper-texture"></div>
    </div>

    <!-- Navigation -->
    <header
      class="sticky top-0 z-50 transition-all duration-500"
      :class="[
        scrolled
          ? 'bg-[#f6f8fb]/70 dark:bg-[#0f1318]/70 backdrop-blur-2xl shadow-[0_1px_0_0_var(--color-border)]'
          : 'bg-transparent'
      ]"
    >
      <div class="container-blog">
        <div class="flex items-center justify-between h-16 md:h-[72px]">
          <!-- Logo -->
          <router-link to="/" class="site-brand group relative z-10">
            <img :src="avatarImg" alt="avatar" class="w-8 h-8 rounded-full object-cover" />
            <span>{{ config.title || 'Capybara' }}</span>
          </router-link>

          <!-- Desktop Navigation -->
          <nav class="hidden md:flex items-center gap-1">
            <router-link
              v-for="item in navItems"
              :key="item.path"
              :to="item.path"
              class="nav-link"
              :class="{ active: isNavActive(item.path) }"
            >
              <span class="flex items-center gap-1.5">
                <component :is="item.icon" class="w-4 h-4 opacity-70" />
                {{ item.label }}
              </span>
            </router-link>
          </nav>

          <!-- Right Actions -->
          <div class="flex items-center gap-1">
            <!-- Search -->
            <router-link
              to="/search"
              class="w-9 h-9 rounded-xl flex items-center justify-center transition-all duration-300 text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] hover:bg-[var(--color-accent-soft)]"
              title="搜索"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="w-[18px] h-[18px]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </router-link>

            <!-- Theme Toggle -->
            <button
              @click="toggleTheme"
              class="w-9 h-9 rounded-xl flex items-center justify-center transition-all duration-300 text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] hover:bg-[var(--color-accent-soft)]"
              :title="isDark ? '切换亮色模式' : '切换暗色模式'"
            >
              <transition name="spin-swap" mode="out-in">
                <!-- Sun icon (shown in dark mode) -->
                <svg v-if="isDark" key="sun" xmlns="http://www.w3.org/2000/svg" class="w-[18px] h-[18px]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
                <!-- Moon icon (shown in light mode) -->
                <svg v-else key="moon" xmlns="http://www.w3.org/2000/svg" class="w-[18px] h-[18px]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
                </svg>
              </transition>
            </button>

            <!-- GitHub Link -->
            <a
              v-if="config.github"
              :href="config.github"
              target="_blank"
              rel="noopener noreferrer"
              class="w-9 h-9 rounded-xl flex items-center justify-center transition-all duration-300 text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] hover:bg-[var(--color-accent-soft)]"
              title="GitHub"
            >
              <svg class="w-[18px] h-[18px]" fill="currentColor" viewBox="0 0 24 24">
                <path fill-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" clip-rule="evenodd" />
              </svg>
            </a>

            <div class="hidden md:flex items-center gap-2 ml-1">
              <template v-if="authStore.isLoggedIn">
                <span class="auth-pill">{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
                <button class="auth-text-btn" @click="handleLogout">退出</button>
              </template>
              <template v-else>
                <button class="auth-text-btn" @click="openAuth('login')">登录</button>
                <button class="auth-primary-btn" @click="openAuth('register')">注册</button>
              </template>
            </div>

            <!-- Mobile Menu Toggle -->
            <button
              @click="showMobileMenu = !showMobileMenu"
              class="md:hidden w-9 h-9 rounded-xl flex items-center justify-center transition-all duration-300 text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] hover:bg-[var(--color-accent-soft)]"
            >
              <svg v-if="!showMobileMenu" xmlns="http://www.w3.org/2000/svg" class="w-[18px] h-[18px]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-[18px] h-[18px]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Mobile Menu -->
        <transition name="mobile-menu">
          <div v-if="showMobileMenu" class="md:hidden pb-4">
            <nav class="flex flex-col gap-1 glass-panel p-3">
              <router-link
                v-for="item in navItems"
                :key="item.path"
                :to="item.path"
                class="flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-300"
                :class="[
                  isNavActive(item.path)
                    ? 'bg-[var(--color-accent-soft)] text-[var(--color-accent)]'
                    : 'text-[var(--color-text-secondary)] hover:text-[var(--color-text-primary)] hover:bg-[var(--color-accent-soft)]'
                ]"
                @click="showMobileMenu = false"
              >
                <component :is="item.icon" class="w-4 h-4" />
                {{ item.label }}
              </router-link>

              <div class="mobile-auth-actions">
                <template v-if="authStore.isLoggedIn">
                  <span>{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
                  <button @click="handleLogout(); showMobileMenu = false">退出登录</button>
                </template>
                <template v-else>
                  <button @click="openAuth('login'); showMobileMenu = false">登录</button>
                  <button @click="openAuth('register'); showMobileMenu = false">注册</button>
                </template>
              </div>
            </nav>
          </div>
        </transition>
      </div>
    </header>

    <teleport to="body">
      <transition name="auth-modal">
        <div v-if="showAuthModal" class="auth-modal-mask" @click.self="closeAuth">
          <form class="auth-modal" @submit.prevent="submitAuth">
            <div class="auth-modal-header">
              <div>
                <h2>{{ authMode === 'login' ? '登录' : '注册' }}</h2>
                <p>{{ authMode === 'login' ? '登录后可按权限访问文章' : '注册后默认普通用户权限' }}</p>
              </div>
              <button type="button" class="auth-close" @click="closeAuth" aria-label="关闭">×</button>
            </div>

            <label class="auth-field">
              <span>用户名</span>
              <input v-model="authForm.username" type="text" autocomplete="username" required />
            </label>

            <label class="auth-field">
              <span>密码</span>
              <input v-model="authForm.password" type="password" autocomplete="current-password" required />
            </label>

            <template v-if="authMode === 'register'">
              <label class="auth-field">
                <span>昵称</span>
                <input v-model="authForm.nickname" type="text" autocomplete="nickname" />
              </label>
              <label class="auth-field">
                <span>邮箱</span>
                <input v-model="authForm.email" type="email" autocomplete="email" />
              </label>
            </template>

            <p v-if="authError" class="auth-error">{{ authError }}</p>

            <button class="auth-submit" type="submit" :disabled="authLoading">
              {{ authLoading ? '处理中...' : (authMode === 'login' ? '登录' : '注册并登录') }}
            </button>

            <button
              type="button"
              class="auth-switch"
              @click="authMode = authMode === 'login' ? 'register' : 'login'; authError = ''"
            >
              {{ authMode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
            </button>
          </form>
        </div>
      </transition>
    </teleport>

    <!-- Main Content -->
    <main class="flex-1 relative z-10">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer class="relative z-10 border-t border-[var(--color-border)]">
      <div class="container-blog">
        <!-- Top Section -->
        <div class="py-12 md:py-16 grid grid-cols-1 md:grid-cols-3 gap-8 md:gap-12">
          <!-- Brand -->
          <div class="md:col-span-1">
            <div class="flex items-center gap-2.5 mb-4">
              <img :src="avatarImg" alt="avatar" class="w-8 h-8 rounded-lg object-cover" />
              <span class="text-lg font-display font-bold text-gradient">{{ config.title || 'Capybara' }}</span>
            </div>
            <p class="text-sm text-[var(--color-text-tertiary)] leading-relaxed max-w-xs">
              {{ config.subtitle || '用代码构建世界，用文字记录思考' }}
            </p>
          </div>

          <!-- Quick Links -->
          <div>
            <h4 class="text-sm font-semibold text-[var(--color-text-primary)] uppercase tracking-wider mb-4 font-display">导航</h4>
            <nav class="flex flex-col gap-2.5">
              <router-link
                v-for="item in navItems"
                :key="item.path"
                :to="item.path"
                class="text-sm text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] transition-colors link-animated w-fit"
              >
                {{ item.label }}
              </router-link>
            </nav>
          </div>

          <!-- Contact -->
          <div>
            <h4 class="text-sm font-semibold text-[var(--color-text-primary)] uppercase tracking-wider mb-4 font-display">联系</h4>
            <div class="flex flex-col gap-2.5">
              <a
                v-if="config.github"
                :href="config.github"
                target="_blank"
                rel="noopener noreferrer"
                class="flex items-center gap-2 text-sm text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] transition-colors link-animated w-fit"
              >
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path fill-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" clip-rule="evenodd" /></svg>
                GitHub
              </a>
              <a
                v-if="config.email"
                :href="`mailto:${config.email}`"
                class="flex items-center gap-2 text-sm text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] transition-colors link-animated w-fit"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
                {{ config.email }}
              </a>
            </div>
          </div>
        </div>

        <!-- Bottom Bar -->
        <div class="py-6 border-t border-[var(--color-border)] grid grid-cols-1 sm:grid-cols-3 items-center gap-3 text-center">
          <p class="text-xs text-[var(--color-text-tertiary)] sm:text-left">
            &copy; {{ new Date().getFullYear() }} {{ config.title || 'Capybara' }}
          </p>
          <a
            href="https://beian.miit.gov.cn/#/Integrated/index"
            target="_blank"
            rel="noopener noreferrer"
            class="text-xs text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] transition-colors"
          >
            备案号：皖ICP备2025084903号
          </a>
          <p class="text-xs text-[var(--color-text-tertiary)] sm:text-right">
            Built with Vue & Spring Boot
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, onUnmounted, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import { getSiteConfig } from '@/api/config'
import avatarImg from '@/static/WechatIMG1062.jpg'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()
const isDark = computed(() => appStore.isDark)
const config = computed(() => appStore.siteConfig || {})

const showMobileMenu = ref(false)
const scrolled = ref(false)
const showAuthModal = ref(false)
const authMode = ref<'login' | 'register'>('login')
const authLoading = ref(false)
const authError = ref('')
const authForm = reactive({
  username: '',
  password: '',
  nickname: '',
  email: ''
})

// SVG icon components for navigation
const HomeIcon = {
  render() {
    return h('svg', { xmlns: 'http://www.w3.org/2000/svg', fill: 'none', viewBox: '0 0 24 24', 'stroke-width': '2', stroke: 'currentColor' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' })
    ])
  }
}

const ArchiveIcon = {
  render() {
    return h('svg', { xmlns: 'http://www.w3.org/2000/svg', fill: 'none', viewBox: '0 0 24 24', 'stroke-width': '2', stroke: 'currentColor' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: 'M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10' })
    ])
  }
}

const AboutIcon = {
  render() {
    return h('svg', { xmlns: 'http://www.w3.org/2000/svg', fill: 'none', viewBox: '0 0 24 24', 'stroke-width': '2', stroke: 'currentColor' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z' })
    ])
  }
}

const MomentsIcon = {
  render() {
    return h('svg', { xmlns: 'http://www.w3.org/2000/svg', fill: 'none', viewBox: '0 0 24 24', 'stroke-width': '2', stroke: 'currentColor' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: 'M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5' }),
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: 'M18.5 2.5a2.121 2.121 0 113 3L12 15l-4 1 1-4 9.5-9.5z' })
    ])
  }
}

const navItems = computed(() => [
  { path: '/', label: '首页', icon: HomeIcon },
  { path: '/archive', label: '归档', icon: ArchiveIcon },
  ...(authStore.isAdmin ? [{ path: '/moments', label: '随心记', icon: MomentsIcon }] : []),
  { path: '/about', label: '关于', icon: AboutIcon },
])

function isNavActive(path: string) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function toggleTheme() {
  appStore.toggleTheme()
}

function openAuth(mode: 'login' | 'register') {
  authMode.value = mode
  authError.value = ''
  showAuthModal.value = true
}

function closeAuth() {
  showAuthModal.value = false
  authError.value = ''
}

function handleLogout() {
  authStore.logout()
  if (route.path.startsWith('/moments')) {
    router.push('/')
  }
}

async function submitAuth() {
  authLoading.value = true
  authError.value = ''
  try {
    if (authMode.value === 'login') {
      await authStore.login(authForm.username, authForm.password)
    } else {
      await authStore.register({
        username: authForm.username,
        password: authForm.password,
        nickname: authForm.nickname,
        email: authForm.email
      })
    }
    authForm.password = ''
    closeAuth()
  } catch (error: any) {
    authError.value = error.message || '操作失败'
  } finally {
    authLoading.value = false
  }
}

function handleScroll() {
  scrolled.value = window.scrollY > 20
}

onMounted(async () => {
  appStore.initTheme()
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll()

  try {
    const res = await getSiteConfig()
    appStore.setSiteConfig(res.data)
  } catch (error) {
    console.error('获取配置失败', error)
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* Page transition */
.page-fade-enter-active {
  transition: opacity 0.3s ease, transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.page-fade-leave-active {
  transition: opacity 0.15s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-fade-leave-to {
  opacity: 0;
}

/* Theme toggle spin swap */
.spin-swap-enter-active,
.spin-swap-leave-active {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.spin-swap-enter-from {
  opacity: 0;
  transform: rotate(-90deg) scale(0.8);
}

.spin-swap-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.8);
}

/* Mobile menu transition */
.mobile-menu-enter-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.mobile-menu-leave-active {
  transition: all 0.2s ease;
}

.mobile-menu-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}

.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.auth-pill {
  max-width: 112px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 7px 10px;
  border-radius: 10px;
  background: var(--color-accent-soft);
  color: var(--color-accent);
  font-size: 12px;
  font-weight: 600;
}

.auth-text-btn,
.auth-primary-btn {
  height: 34px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid var(--color-border);
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.auth-text-btn {
  color: var(--color-text-secondary);
  background: transparent;
}

.auth-text-btn:hover {
  color: var(--color-accent);
  background: var(--color-accent-soft);
}

.auth-primary-btn {
  color: #fff;
  border-color: var(--color-accent);
  background: var(--color-accent);
}

.mobile-auth-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  padding: 8px 4px 0;
  border-top: 1px solid var(--color-border);
  color: var(--color-text-secondary);
  font-size: 13px;
}

.mobile-auth-actions span {
  grid-column: 1 / -1;
  padding: 8px 10px;
  border-radius: 10px;
  background: var(--color-accent-soft);
  color: var(--color-accent);
  font-weight: 600;
}

.mobile-auth-actions button {
  padding: 10px;
  border-radius: 10px;
  border: 1px solid var(--color-border);
  color: var(--color-text-secondary);
}

.auth-modal-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(10, 16, 24, 0.42);
  backdrop-filter: blur(10px);
}

.auth-modal {
  width: min(420px, 100%);
  padding: 22px;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  box-shadow: 0 24px 80px rgba(15, 23, 42, 0.22);
}

.auth-modal-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.auth-modal-header h2 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 22px;
  line-height: 1.25;
}

.auth-modal-header p {
  margin: 4px 0 0;
  color: var(--color-text-tertiary);
  font-size: 13px;
}

.auth-close {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  color: var(--color-text-tertiary);
  font-size: 22px;
  line-height: 1;
}

.auth-close:hover {
  background: var(--color-accent-soft);
  color: var(--color-accent);
}

.auth-field {
  display: grid;
  gap: 7px;
  margin-bottom: 13px;
}

.auth-field span {
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 600;
}

.auth-field input {
  height: 42px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background: var(--color-bg);
  color: var(--color-text-primary);
  outline: none;
}

.auth-field input:focus {
  border-color: var(--color-accent);
  box-shadow: 0 0 0 3px var(--color-accent-soft);
}

.auth-error {
  margin: 0 0 12px;
  color: #dc2626;
  font-size: 13px;
}

.auth-submit {
  width: 100%;
  height: 42px;
  border-radius: 8px;
  color: #fff;
  background: var(--color-accent);
  font-weight: 700;
}

.auth-submit:disabled {
  opacity: 0.65;
}

.auth-switch {
  width: 100%;
  margin-top: 10px;
  color: var(--color-text-tertiary);
  font-size: 13px;
}

.auth-switch:hover {
  color: var(--color-accent);
}

.auth-modal-enter-active,
.auth-modal-leave-active {
  transition: opacity 0.2s ease;
}

.auth-modal-enter-from,
.auth-modal-leave-to {
  opacity: 0;
}
</style>
