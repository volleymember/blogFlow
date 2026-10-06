<template>
  <div>
    <!-- Category Header -->
    <section class="relative overflow-hidden py-16 md:py-20">
      <div class="absolute inset-0 grid-lines opacity-30"></div>
      <div class="absolute inset-0 bg-gradient-to-b from-transparent via-transparent to-[var(--color-bg)]"></div>
      <div class="container-blog relative">
        <div class="animate-fade-in-up">
          <div class="flex items-center gap-2 mb-3">
            <router-link to="/" class="text-sm text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] transition-colors">首页</router-link>
            <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3 text-[var(--color-text-tertiary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
            <span class="text-sm text-[var(--color-text-tertiary)]">分类</span>
          </div>
          <h1 class="font-display text-display-sm md:text-display text-[var(--color-text-primary)]">
            {{ category?.name || '分类' }}
          </h1>
          <p v-if="category?.description" class="text-[var(--color-text-secondary)] mt-2 max-w-lg">
            {{ category.description }}
          </p>
        </div>
      </div>
    </section>

    <!-- Content -->
    <section class="pb-20">
      <div class="container-blog">
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-8">
          <!-- Articles -->
          <div class="lg:col-span-8">
            <!-- Loading -->
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
                  <span class="text-xs text-[var(--color-text-tertiary)] flex items-center gap-1">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                    {{ article.viewCount }}
                  </span>
                </div>
                <h2 class="text-lg font-display font-semibold text-[var(--color-text-primary)] mb-2 group-hover:text-[var(--color-accent)] transition-colors">
                  {{ article.title }}
                </h2>
                <p class="text-sm text-[var(--color-text-secondary)] line-clamp-2">{{ article.summary || '暂无摘要' }}</p>
                <div v-if="article.tags && article.tags.length" class="flex flex-wrap gap-1.5 mt-3">
                  <span v-for="tag in article.tags" :key="tag.id" class="text-[10px] font-mono px-2 py-0.5 rounded-md bg-[var(--color-border-subtle)] text-[var(--color-text-tertiary)]">
                    #{{ tag.name }}
                  </span>
                </div>
              </article>

              <div v-if="articles.length === 0" class="text-center py-16">
                <p class="text-[var(--color-text-tertiary)] text-sm">该分类下暂无文章</p>
              </div>
            </div>

            <!-- Pagination -->
            <div v-if="total > pageSize" class="flex justify-center mt-10">
              <div class="flex items-center gap-2">
                <button
                  @click="changePage(pageNum - 1)"
                  :disabled="pageNum === 1"
                  class="btn btn-ghost text-sm disabled:opacity-30 disabled:cursor-not-allowed"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" /></svg>
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
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
                </button>
              </div>
            </div>
          </div>

          <!-- Sidebar: Categories -->
          <aside class="lg:col-span-4 space-y-6">
            <div class="card">
              <h3 class="section-title mb-5">全部分类</h3>
              <div class="space-y-1">
                <router-link
                  v-for="cat in categories"
                  :key="cat.id"
                  :to="`/category/${cat.slug}`"
                  class="flex items-center justify-between px-3 py-2.5 rounded-xl transition-all duration-300 group"
                  :class="[
                    cat.slug === route.params.slug
                      ? 'bg-[var(--color-accent-soft)] text-[var(--color-accent)]'
                      : 'text-[var(--color-text-secondary)] hover:text-[var(--color-text-primary)] hover:bg-[var(--color-accent-soft)]'
                  ]"
                >
                  <span class="text-sm font-medium">{{ cat.name }}</span>
                  <span class="text-xs font-mono px-2 py-0.5 rounded-md"
                    :class="cat.slug === route.params.slug ? 'bg-[var(--color-accent)] text-white' : 'bg-[var(--color-border-subtle)] text-[var(--color-text-tertiary)]'"
                  >
                    {{ cat.articleCount || 0 }}
                  </span>
                </router-link>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { getCategoryArticles, getCategoryList } from '@/api/category'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const category = ref<any>(null)
const articles = ref<any[]>([])
const categories = ref<any[]>([])

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

watch(() => route.params.slug, () => {
  pageNum.value = 1
  fetchCategory()
  fetchArticles()
})

watch(() => [authStore.token, authStore.isAdmin], () => {
  pageNum.value = 1
  fetchCategories()
  fetchCategory()
  fetchArticles()
})

onMounted(() => {
  fetchCategories()
  fetchCategory()
  fetchArticles()
})

function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD')
}

function changePage(page: number) {
  pageNum.value = page
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToArticle(id: number) {
  router.push(`/article/${id}`)
}

async function fetchCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

async function fetchCategory() {
  const cat = categories.value.find(c => c.slug === route.params.slug)
  if (cat) {
    category.value = cat
  } else {
    try {
      const res = await getCategoryList()
      category.value = res.data.find((c: any) => c.slug === route.params.slug)
    } catch (error) {
      console.error('获取分类失败', error)
    }
  }
}

async function fetchArticles() {
  loading.value = true
  try {
    const res = await getCategoryArticles(route.params.slug as string, {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    articles.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取文章失败', error)
  } finally {
    loading.value = false
  }
}
</script>
