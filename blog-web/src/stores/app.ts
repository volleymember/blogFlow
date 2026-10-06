import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const isDark = ref(false)
  const siteConfig = ref<any>(null)

  // 切换主题
  function toggleTheme() {
    isDark.value = !isDark.value
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')

    // 立即应用到 html 元素
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // 初始化主题
  function initTheme() {
    const saved = localStorage.getItem('theme')
    if (saved === 'dark') {
      isDark.value = true
      document.documentElement.classList.add('dark')
    } else if (saved === 'light') {
      isDark.value = false
      document.documentElement.classList.remove('dark')
    } else {
      // 跟随系统
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
      if (isDark.value) {
        document.documentElement.classList.add('dark')
      }
    }
  }

  // 设置网站配置
  function setSiteConfig(config: any) {
    siteConfig.value = config
  }

  return {
    isDark,
    siteConfig,
    toggleTheme,
    initTheme,
    setSiteConfig
  }
})