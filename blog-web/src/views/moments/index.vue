<template>
  <div>
    <section class="relative overflow-hidden py-16 md:py-20">
      <div class="absolute inset-0 grid-lines opacity-30"></div>
      <div class="absolute inset-0 bg-gradient-to-b from-transparent via-transparent to-[var(--color-bg)]"></div>
      <div class="container-blog relative">
        <div class="max-w-3xl animate-fade-in-up">
          <div class="flex items-center gap-2 mb-3">
            <router-link to="/" class="text-sm text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] transition-colors">首页</router-link>
            <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3 text-[var(--color-text-tertiary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
            <span class="text-sm text-[var(--color-text-tertiary)]">随心记</span>
          </div>
          <h1 class="font-display text-display-sm md:text-display text-[var(--color-text-primary)] mb-3">
            随心记
          </h1>
          <p class="text-[var(--color-text-tertiary)] text-sm">
            仅展示带“随心记”标签的文章
          </p>
        </div>
      </div>
    </section>

    <section class="pb-20">
      <div class="container-blog">
        <div v-if="!authStore.isAdmin" class="max-w-xl mx-auto card text-center py-14">
          <div class="w-14 h-14 mx-auto mb-5 rounded-2xl bg-[var(--color-accent-soft)] flex items-center justify-center text-[var(--color-accent)]">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h2 class="font-display text-2xl text-[var(--color-text-primary)] mb-3">仅管理员可访问</h2>
          <p class="text-sm text-[var(--color-text-secondary)]">随心记内容不会对游客和普通用户开放。</p>
        </div>

        <div v-else class="max-w-3xl mx-auto">
          <div v-if="loading" class="space-y-5">
            <div v-for="i in 3" :key="i" class="card">
              <div class="skeleton h-4 w-24 rounded-full mb-3"></div>
              <div class="skeleton h-6 w-3/4 mb-2"></div>
              <div class="skeleton h-4 w-full"></div>
            </div>
          </div>

          <div v-else class="space-y-5">
            <article
              v-for="(article, index) in articles"
              :key="article.id"
              class="card group cursor-pointer animate-fade-in-up"
              :style="{ animationDelay: `${index * 0.05}s`, opacity: 0 }"
              @click="router.push(`/article/${article.id}`)"
            >
              <div class="flex items-center gap-2 mb-3">
                <span class="tag text-[10px]">随心记</span>
                <span v-if="article.visibility === 1" class="tag text-[10px]">私密</span>
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

            <div v-if="articles.length === 0" class="card text-center py-16">
              <p class="text-[var(--color-text-tertiary)] text-sm">暂无随心记文章</p>
            </div>
          </div>

          <div v-if="total > pageSize" class="flex justify-center mt-10">
            <div class="flex items-center gap-2">
              <button
                @click="changePage(pageNum - 1)"
                :disabled="pageNum === 1"
                class="btn btn-ghost text-sm disabled:opacity-30 disabled:cursor-not-allowed"
              >
                上一页
              </button>
              <span class="px-4 py-1 text-sm text-[var(--color-text-secondary)] font-mono">
                {{ pageNum }} / {{ totalPages }}
              </span>
              <button
                @click="changePage(pageNum + 1)"
                :disabled="pageNum >= totalPages"
                class="btn btn-ghost text-sm disabled:opacity-30 disabled:cursor-not-allowed"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { getMomentsArticles } from '@/api/article'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const articles = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(() => {
  if (authStore.isAdmin) {
    fetchArticles()
  }
})

watch(() => authStore.isAdmin, isAdmin => {
  articles.value = []
  total.value = 0
  pageNum.value = 1
  if (isAdmin) {
    fetchArticles()
  }
})

function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD')
}

function changePage(page: number) {
  pageNum.value = page
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function fetchArticles() {
  if (!authStore.isAdmin) return
  loading.value = true
  try {
    const res = await getMomentsArticles({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    articles.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取随心记失败', error)
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
</script>
