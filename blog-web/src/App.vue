<template>
  <div class="min-h-screen bg-background-light dark:bg-background-dark text-text-light dark:text-text-dark transition-colors duration-300">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import { watch, onMounted } from 'vue'

const appStore = useAppStore()

// 监听主题变化，应用到 html 元素
watch(() => appStore.isDark, (isDark) => {
  if (isDark) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}, { immediate: true })

onMounted(() => {
  appStore.initTheme()
})
</script>