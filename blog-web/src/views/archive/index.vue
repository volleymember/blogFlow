<template>
  <div>
    <!-- Header -->
    <section class="relative overflow-hidden py-16 md:py-20">
      <div class="absolute inset-0 grid-lines opacity-30"></div>
      <div class="absolute inset-0 bg-gradient-to-b from-transparent via-transparent to-[var(--color-bg)]"></div>
      <div class="container-blog relative text-center">
        <h1 class="font-display text-display-sm md:text-display text-[var(--color-text-primary)] mb-3 animate-fade-in-up">
          文章归档
        </h1>
        <p class="text-[var(--color-text-tertiary)] animate-fade-in-up" style="animation-delay: 0.1s; opacity: 0;">
          共 <span class="text-[var(--color-accent)] font-mono font-semibold">{{ totalCount }}</span> 篇文章，持续更新中
        </p>
      </div>
    </section>

    <!-- Timeline -->
    <section class="pb-20">
      <div class="container-blog">
        <div class="max-w-3xl mx-auto">
          <!-- Loading -->
          <div v-if="loading" class="space-y-8">
            <div v-for="i in 3" :key="i">
              <div class="skeleton h-8 w-32 mb-4"></div>
              <div class="space-y-3 pl-8 border-l-2 border-[var(--color-border)]">
                <div v-for="j in 3" :key="j" class="skeleton h-5 w-3/4 rounded"></div>
              </div>
            </div>
          </div>

          <!-- Timeline Content -->
          <div v-else class="relative">
            <!-- Timeline line -->
            <div class="absolute left-[11px] top-2 bottom-2 w-px bg-gradient-to-b from-[var(--color-logo)] via-[var(--color-logo)]/20 to-transparent"></div>

            <div v-for="(group, yearMonth, groupIndex) in groupedArticles" :key="yearMonth" class="mb-10 last:mb-0 animate-fade-in-up" :style="{ animationDelay: `${groupIndex * 0.1}s`, opacity: 0 }">
              <!-- Year-Month Badge -->
              <div class="flex items-center gap-4 mb-5 relative">
                <div class="w-[23px] h-[23px] rounded-full bg-[var(--color-logo)] flex items-center justify-center flex-shrink-0 z-10">
                  <div class="w-2 h-2 rounded-full bg-white"></div>
                </div>
                <div class="flex items-center gap-3">
                  <h2 class="font-display text-xl font-bold text-[var(--color-text-primary)]">{{ yearMonth }}</h2>
                  <span class="text-xs font-mono px-2 py-0.5 rounded-full bg-[var(--color-accent-soft)] text-[var(--color-accent)]">
                    {{ group.length }} 篇
                  </span>
                </div>
              </div>

              <!-- Articles -->
              <div class="pl-10 space-y-1">
                <router-link
                  v-for="article in group"
                  :key="article.id"
                  :to="`/article/${article.id}`"
                  class="group flex items-center gap-4 py-3 px-4 -mx-2 rounded-xl transition-all duration-300 hover:bg-[var(--color-accent-soft)]"
                >
                  <span class="text-xs font-mono text-[var(--color-text-tertiary)] flex-shrink-0 w-14">
                    {{ formatDate(article.createTime) }}
                  </span>
                  <span class="text-sm text-[var(--color-text-secondary)] group-hover:text-[var(--color-accent)] transition-colors line-clamp-1">
                    <span v-if="article.visibility === 1" class="mr-2 text-[var(--color-accent)]">私密</span>
                    {{ article.title }}
                  </span>
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-3.5 h-3.5 text-[var(--color-text-tertiary)] group-hover:text-[var(--color-accent)] flex-shrink-0 ml-auto opacity-0 -translate-x-2 group-hover:opacity-100 group-hover:translate-x-0 transition-all duration-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
                  </svg>
                </router-link>
              </div>
            </div>

            <!-- Empty State -->
            <div v-if="Object.keys(groupedArticles).length === 0" class="text-center py-16">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-16 h-16 mx-auto mb-4 text-[var(--color-text-tertiary)] opacity-30" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
              </svg>
              <p class="text-[var(--color-text-tertiary)] text-sm">暂无文章</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import dayjs from 'dayjs'
import { getArchiveList } from '@/api/article'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const articles = ref<any[]>([])

const totalCount = computed(() => articles.value.length)

const groupedArticles = computed(() => {
  const groups: Record<string, any[]> = {}
  articles.value.forEach(article => {
    const yearMonth = dayjs(article.createTime).format('YYYY年MM月')
    if (!groups[yearMonth]) {
      groups[yearMonth] = []
    }
    groups[yearMonth].push(article)
  })
  return groups
})

onMounted(() => {
  fetchArticles()
})

watch(() => [authStore.token, authStore.isAdmin], () => {
  fetchArticles()
})

function formatDate(date: string) {
  return dayjs(date).format('MM-DD')
}

async function fetchArticles() {
  loading.value = true
  try {
    const res = await getArchiveList()
    articles.value = res.data
  } catch (error) {
    console.error('获取归档失败', error)
  } finally {
    loading.value = false
  }
}
</script>
