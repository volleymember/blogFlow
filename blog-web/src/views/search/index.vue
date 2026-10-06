<template>
  <div>
    <!-- Search Header -->
    <section class="relative overflow-hidden py-16 md:py-24">
      <div class="absolute inset-0 grid-lines opacity-30"></div>
      <div class="absolute inset-0 bg-gradient-to-b from-transparent via-transparent to-[var(--color-bg)]"></div>
      <div class="container-blog relative">
        <div class="max-w-2xl mx-auto">
          <!-- Search Input -->
          <div class="relative animate-fade-in-up">
            <div class="relative group">
              <svg xmlns="http://www.w3.org/2000/svg" class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[var(--color-text-tertiary)] group-focus-within:text-[var(--color-accent)] transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                v-model="keyword"
                type="text"
                placeholder="搜索文章标题、摘要..."
                class="w-full pl-12 pr-28 py-4 rounded-2xl bg-[var(--color-surface)] border border-[var(--color-border)] text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:border-[var(--color-accent)] focus:ring-2 focus:ring-[var(--color-accent-glow)] transition-all duration-300 text-base font-body"
                @keyup.enter="search"
              />
              <button
                @click="search"
                class="absolute right-2 top-1/2 -translate-y-1/2 px-5 py-2 rounded-xl bg-[var(--color-accent)] text-white text-sm font-medium hover:opacity-90 transition-all duration-300 shadow-glow"
              >
                搜索
              </button>
            </div>
          </div>

          <!-- Search hint -->
          <p v-if="searched && !loading" class="text-sm text-[var(--color-text-tertiary)] mt-4 text-center animate-fade-in">
            找到 <span class="text-[var(--color-accent)] font-mono font-semibold">{{ total }}</span> 篇与 "<span class="text-[var(--color-text-secondary)]">{{ lastKeyword }}</span>" 相关的文章
          </p>
        </div>
      </div>
    </section>

    <!-- Results -->
    <section class="pb-20">
      <div class="container-blog">
        <div class="max-w-2xl mx-auto">
          <!-- Loading -->
          <div v-if="loading" class="space-y-5">
            <div v-for="i in 3" :key="i" class="card">
              <div class="skeleton h-4 w-24 rounded-full mb-3"></div>
              <div class="skeleton h-6 w-3/4 mb-2"></div>
              <div class="skeleton h-4 w-full"></div>
            </div>
          </div>

          <!-- Results -->
          <div v-else-if="searched" class="space-y-4">
            <article
              v-for="(article, index) in articles"
              :key="article.id"
              class="card group cursor-pointer animate-fade-in-up"
              :style="{ animationDelay: `${index * 0.05}s`, opacity: 0 }"
              @click="goToArticle(article.id)"
            >
              <div class="flex items-center gap-2 mb-3">
                <span
                  v-if="article.visibility === 1"
                  class="text-[10px] font-mono px-2 py-0.5 rounded-md bg-[var(--color-accent-soft)] text-[var(--color-accent)]"
                >
                  私密
                </span>
                <span class="text-xs font-mono text-[var(--color-text-tertiary)]">{{ formatDate(article.createTime) }}</span>
                <span class="text-[var(--color-text-tertiary)] text-xs">&middot;</span>
                <span class="text-xs text-[var(--color-text-tertiary)]">{{ article.viewCount }} 阅读</span>
              </div>
              <h2 class="text-lg font-display font-semibold text-[var(--color-text-primary)] mb-2 group-hover:text-[var(--color-accent)] transition-colors">
                {{ article.title }}
              </h2>
              <p class="text-sm text-[var(--color-text-secondary)] line-clamp-2 leading-relaxed">
                {{ article.summary || '暂无摘要' }}
              </p>
            </article>

            <!-- No Results -->
            <div v-if="articles.length === 0" class="text-center py-16">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-16 h-16 mx-auto mb-4 text-[var(--color-text-tertiary)] opacity-30" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p class="text-[var(--color-text-tertiary)] text-sm">没有找到相关文章</p>
              <p class="text-[var(--color-text-tertiary)] text-xs mt-1">试试其他关键词？</p>
            </div>
          </div>

          <!-- Initial State -->
          <div v-else class="text-center py-20 animate-fade-in">
            <div class="w-20 h-20 mx-auto mb-6 rounded-2xl bg-[var(--color-accent-soft)] flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-9 h-9 text-[var(--color-accent)]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <p class="text-[var(--color-text-secondary)] text-sm font-medium mb-1">搜索文章</p>
            <p class="text-[var(--color-text-tertiary)] text-xs">输入关键词，探索感兴趣的内容</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import dayjs from 'dayjs'
import { getArticleList } from '@/api/article'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const keyword = ref('')
const lastKeyword = ref('')
const searched = ref(false)
const loading = ref(false)
const articles = ref<any[]>([])
const total = ref(0)

onMounted(() => {
  if (route.query.q) {
    keyword.value = route.query.q as string
    search()
  }
})

watch(() => [authStore.token, authStore.isAdmin], () => {
  if (searched.value) {
    search()
  }
})

function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD')
}

function goToArticle(id: number) {
  router.push(`/article/${id}`)
}

async function search() {
  if (!keyword.value.trim()) return

  searched.value = true
  loading.value = true
  lastKeyword.value = keyword.value

  router.replace({ query: { q: keyword.value } })

  try {
    const res = await getArticleList({
      keyword: keyword.value,
      pageNum: 1,
      pageSize: 20
    })
    articles.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('搜索失败', error)
  } finally {
    loading.value = false
  }
}
</script>
