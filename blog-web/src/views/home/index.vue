<template>
  <div class="home-page">
    <section class="home-hero">
      <div class="container-blog">
        <div class="hero-grid">
          <div class="hero-copy">
            <span class="eyebrow">{{ config.title || '个人博客' }} · NOTES & IDEAS</span>
            <h1>记录技术，也记录<br /><em>思考发生的过程。</em></h1>
            <p>{{ config.subtitle || '关于开发、产品和持续成长的个人笔记。' }}</p>
            <div class="hero-actions">
              <a href="#latest" class="btn btn-primary">开始阅读</a>
              <router-link to="/about" class="text-link">关于作者 <span>↗</span></router-link>
            </div>
          </div>

          <div class="hero-note" aria-label="博客简介">
            <div class="hero-note-top">
              <span class="status-dot"></span>
              <span>持续写作中</span>
              <span class="hero-note-date">{{ currentYear }}</span>
            </div>
            <blockquote>“好的文字不是答案，而是一条通往答案的清晰路径。”</blockquote>
            <div class="hero-stats">
              <div><strong>{{ total }}</strong><span>文章</span></div>
              <div><strong>{{ categories.length }}</strong><span>分类</span></div>
              <div><strong>{{ tags.length }}</strong><span>标签</span></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="latest" class="home-content">
      <div class="container-blog">
        <div class="content-heading">
          <div>
            <span class="section-kicker">LATEST WRITING</span>
            <h2>最近文章</h2>
          </div>
          <router-link to="/archive" class="text-link">查看全部文章 <span>→</span></router-link>
        </div>

        <div class="category-tabs scrollbar-hide">
          <button :class="{ active: selectedCategory === null }" @click="selectCategory(null)">全部</button>
          <button
            v-for="cat in categories"
            :key="cat.id"
            :class="{ active: selectedCategory === cat.id }"
            @click="selectCategory(cat.id)"
          >
            {{ cat.name }}
          </button>
        </div>

        <div class="home-grid">
          <main class="article-list">
            <template v-if="loading">
              <div v-for="i in 3" :key="i" class="article-row skeleton-row">
                <div class="skeleton h-4 w-28"></div>
                <div class="skeleton h-7 w-3/4"></div>
                <div class="skeleton h-4 w-full"></div>
              </div>
            </template>

            <template v-else-if="articles.length">
              <article
                v-for="(article, index) in articles"
                :key="article.id"
                class="article-row"
                @click="goToArticle(article.id)"
              >
                <div class="article-index">{{ String((pageNum - 1) * pageSize + index + 1).padStart(2, '0') }}</div>
                <div class="article-row-body">
                  <div class="article-meta">
                    <span>{{ article.categoryName || '随笔' }}</span>
                    <time>{{ formatDate(article.createTime) }}</time>
                    <span>{{ estimateReadingTime(article.summary) }} 分钟阅读</span>
                    <span v-if="article.visibility === 1" class="private-label">私密</span>
                  </div>
                  <h3>{{ article.title }}</h3>
                  <p>{{ article.summary || '这篇文章暂时没有摘要，点击阅读全文。' }}</p>
                  <div v-if="article.tags?.length" class="article-tags">
                    <span v-for="tag in article.tags.slice(0, 3)" :key="tag.id"># {{ tag.name }}</span>
                  </div>
                </div>
                <img v-if="article.coverImage" :src="article.coverImage" :alt="article.title" class="article-cover" />
                <span v-else class="article-arrow">↗</span>
              </article>
            </template>

            <div v-else class="empty-state">
              <span>✦</span>
              <h3>还没有公开文章</h3>
              <p>新的内容正在路上，稍后再来看看。</p>
            </div>

            <nav v-if="total > pageSize" class="pagination" aria-label="文章分页">
              <button :disabled="pageNum === 1" @click="changePage(pageNum - 1)">← 上一页</button>
              <span>{{ pageNum }} / {{ totalPages }}</span>
              <button :disabled="pageNum >= totalPages" @click="changePage(pageNum + 1)">下一页 →</button>
            </nav>
          </main>

          <aside class="home-sidebar">
            <section class="side-block">
              <div class="side-title"><span>热门阅读</span><small>POPULAR</small></div>
              <div v-if="hotLoading" class="space-y-3">
                <div v-for="i in 4" :key="i" class="skeleton h-10 w-full"></div>
              </div>
              <ol v-else class="popular-list">
                <li v-for="(article, index) in hotArticles" :key="article.id">
                  <span>{{ String(index + 1).padStart(2, '0') }}</span>
                  <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
                </li>
              </ol>
              <p v-if="!hotLoading && !hotArticles.length" class="side-empty">暂无热门文章</p>
            </section>

            <section class="side-block">
              <div class="side-title"><span>主题索引</span><small>TOPICS</small></div>
              <div v-if="tagsLoading" class="flex flex-wrap gap-2">
                <div v-for="i in 6" :key="i" class="skeleton h-8 w-16"></div>
              </div>
              <div v-else class="topic-list">
                <span v-for="tag in tags" :key="tag.id">{{ tag.name }}</span>
              </div>
              <p v-if="!tagsLoading && !tags.length" class="side-empty">标签将在发布文章后出现</p>
            </section>
          </aside>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import dayjs from 'dayjs'
import { getArticleList, getHotArticles } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { getTagList } from '@/api/tag'

const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()
const config = computed(() => appStore.siteConfig || {})
const currentYear = new Date().getFullYear()
const loading = ref(false)
const hotLoading = ref(false)
const tagsLoading = ref(false)
const articles = ref<any[]>([])
const categories = ref<any[]>([])
const hotArticles = ref<any[]>([])
const tags = ref<any[]>([])
const selectedCategory = ref<number | null>(null)
const pageNum = ref(1)
const pageSize = ref(8)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(() => Promise.all([fetchCategories(), fetchArticles(), fetchHotArticles(), fetchTags()]))

watch(selectedCategory, () => {
  pageNum.value = 1
  fetchArticles()
})

watch(() => [authStore.token, authStore.isAdmin], () => {
  pageNum.value = 1
  Promise.all([fetchCategories(), fetchArticles(), fetchHotArticles(), fetchTags()])
})

function formatDate(date: string) { return dayjs(date).format('YYYY.MM.DD') }
function estimateReadingTime(text = '') { return Math.max(1, Math.ceil(text.length / 300)) }
function selectCategory(id: number | null) { selectedCategory.value = id }
function goToArticle(id: number) { router.push(`/article/${id}`) }
function changePage(page: number) {
  pageNum.value = page
  fetchArticles()
  document.querySelector('#latest')?.scrollIntoView({ behavior: 'smooth' })
}

async function fetchCategories() {
  try { categories.value = (await getCategoryList()).data } catch (error) { console.error('获取分类失败', error) }
}
async function fetchArticles() {
  loading.value = true
  try {
    const res = await getArticleList({ categoryId: selectedCategory.value || undefined, pageNum: pageNum.value, pageSize: pageSize.value })
    articles.value = res.data.list
    total.value = res.data.total
  } catch (error) { console.error('获取文章失败', error) } finally { loading.value = false }
}
async function fetchHotArticles() {
  hotLoading.value = true
  try { hotArticles.value = (await getHotArticles(5)).data } catch (error) { console.error('获取热门文章失败', error) } finally { hotLoading.value = false }
}
async function fetchTags() {
  tagsLoading.value = true
  try { tags.value = (await getTagList()).data } catch (error) { console.error('获取标签失败', error) } finally { tagsLoading.value = false }
}
</script>

<style scoped>
.home-page { overflow: hidden; }
.home-hero { padding: 3.5rem 0 3rem; border-bottom: 1px solid var(--color-border); }
.hero-grid { display: grid; grid-template-columns: minmax(0, 1.45fr) minmax(300px, .75fr); gap: clamp(2.5rem, 6vw, 5rem); align-items: end; }
.eyebrow, .section-kicker { display: block; margin-bottom: 1rem; font: 600 .72rem/1.2 var(--font-mono); color: var(--color-accent); letter-spacing: .14em; }
.hero-copy h1 { max-width: 640px; margin: 0; font-size: clamp(1.75rem, 3vw, 2.4rem); line-height: 1.4; letter-spacing: 0; font-weight: 650; }
.hero-copy h1 em { color: var(--color-accent); font-style: normal; }
.hero-copy > p { max-width: 560px; margin: 1rem 0 0; font-size: 1rem; line-height: 1.8; }
.hero-actions { display: flex; align-items: center; gap: 1.5rem; margin-top: 1.75rem; }
.text-link { display: inline-flex; align-items: center; gap: .5rem; color: var(--color-text-primary); font-size: .9rem; font-weight: 600; }
.text-link:hover { color: var(--color-accent); }
.hero-note { padding: 1.25rem 0 0; border-top: 2px solid var(--color-text-primary); }
.hero-note-top { display: flex; align-items: center; gap: .55rem; color: var(--color-text-tertiary); font: 500 .72rem/1.2 var(--font-mono); letter-spacing: .04em; }
.status-dot { width: 7px; height: 7px; background: #4ca66b; border-radius: 50%; box-shadow: 0 0 0 4px rgba(76,166,107,.1); }
.hero-note-date { margin-left: auto; }
.hero-note blockquote { margin: 1.1rem 0 1.5rem; color: var(--color-text-secondary); font-size: .95rem; line-height: 1.8; font-weight: 500; }
.hero-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; }
.hero-stats div { display: flex; flex-direction: column; gap: .2rem; }
.hero-stats strong { font: 650 1.35rem/1 var(--font-display); color: var(--color-text-primary); }
.hero-stats span { color: var(--color-text-tertiary); font-size: .75rem; }
.home-content { padding: 3rem 0 5rem; }
.content-heading { display: flex; align-items: end; justify-content: space-between; gap: 2rem; margin-bottom: 1.5rem; }
.content-heading .section-kicker { margin-bottom: .55rem; }
.content-heading h2 { margin: 0; font-size: 1.5rem; font-weight: 650; letter-spacing: 0; }
.category-tabs { display: flex; gap: 1.6rem; overflow-x: auto; border-bottom: 1px solid var(--color-border); }
.category-tabs button { position: relative; flex-shrink: 0; padding: 0 0 .8rem; color: var(--color-text-tertiary); font-size: .9rem; font-weight: 600; }
.category-tabs button::after { position: absolute; content: ''; left: 0; right: 0; bottom: -1px; height: 2px; background: var(--color-accent); transform: scaleX(0); transition: transform .2s ease; }
.category-tabs button.active { color: var(--color-text-primary); }
.category-tabs button.active::after { transform: scaleX(1); }
.home-grid { display: grid; grid-template-columns: minmax(0, 1fr) 280px; gap: clamp(2.5rem, 5vw, 4.5rem); margin-top: .5rem; }
.article-list { min-width: 0; }
.article-row { display: grid; grid-template-columns: 36px minmax(0, 1fr) auto; gap: 1.25rem; align-items: center; padding: 1.5rem 0; border-bottom: 1px solid var(--color-border); cursor: pointer; }
.article-index { align-self: start; padding-top: .25rem; color: var(--color-text-tertiary); font: 500 .7rem/1 var(--font-mono); }
.article-meta { display: flex; flex-wrap: wrap; gap: .4rem 1rem; color: var(--color-text-tertiary); font-size: .8rem; }
.article-meta span:first-child { color: var(--color-accent); font-weight: 650; }
.article-row h3 { margin: .45rem 0 .4rem; font-size: 1.2rem; font-weight: 600; line-height: 1.5; letter-spacing: 0; transition: color .2s ease; }
.article-row:hover h3 { color: var(--color-accent); }
.article-row p { display: -webkit-box; max-width: 660px; margin: 0; overflow: hidden; font-size: .92rem; line-height: 1.75; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.article-tags { display: flex; flex-wrap: wrap; gap: 1rem; margin-top: .7rem; color: var(--color-text-tertiary); font: 500 .74rem/1.2 var(--font-mono); }
.private-label { color: #b7791f !important; }
.article-cover { width: 128px; height: 86px; border-radius: 10px; object-fit: cover; }
.article-arrow { align-self: start; color: var(--color-text-tertiary); font-size: 1.2rem; transition: transform .2s ease, color .2s ease; }
.article-row:hover .article-arrow { color: var(--color-accent); transform: translate(3px, -3px); }
.skeleton-row { display: flex; flex-direction: column; align-items: flex-start; cursor: default; }
.empty-state { padding: 6rem 1rem; border-bottom: 1px solid var(--color-border); text-align: center; }
.empty-state > span { color: var(--color-accent); font-size: 1.5rem; }
.empty-state h3 { margin: .8rem 0 .4rem; font-size: 1.2rem; }
.empty-state p, .side-empty { color: var(--color-text-tertiary); font-size: .82rem; }
.pagination { display: flex; align-items: center; justify-content: space-between; padding: 1.75rem 0; }
.pagination button { color: var(--color-text-primary); font-size: .82rem; font-weight: 600; }
.pagination button:disabled { color: var(--color-text-tertiary); opacity: .4; }
.pagination span { color: var(--color-text-tertiary); font: .72rem var(--font-mono); }
.home-sidebar { padding-top: 1rem; }
.side-block { padding: 1.4rem 0; border-top: 2px solid var(--color-text-primary); }
.side-block + .side-block { margin-top: 1.5rem; }
.side-title { display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.1rem; color: var(--color-text-primary); font-size: .95rem; font-weight: 650; }
.side-title small { color: var(--color-text-tertiary); font: .6rem var(--font-mono); letter-spacing: .12em; }
.popular-list { display: flex; flex-direction: column; gap: .85rem; }
.popular-list li { display: grid; grid-template-columns: 24px 1fr; gap: .6rem; align-items: start; }
.popular-list li > span { padding-top: .12rem; color: var(--color-text-tertiary); font: .62rem var(--font-mono); }
.popular-list a { color: var(--color-text-secondary); font-size: .88rem; line-height: 1.6; }
.popular-list a:hover { color: var(--color-accent); }
.topic-list { display: flex; flex-wrap: wrap; gap: .5rem; }
.topic-list span { padding: .38rem .65rem; border: 1px solid var(--color-border); border-radius: 6px; color: var(--color-text-secondary); font-size: .8rem; }
@media (max-width: 900px) {
  .hero-grid, .home-grid { grid-template-columns: 1fr; }
  .hero-note { max-width: 560px; margin-top: 1rem; }
  .home-sidebar { display: grid; grid-template-columns: repeat(2, 1fr); gap: 2rem; }
  .side-block + .side-block { margin-top: 0; }
}
@media (max-width: 640px) {
  .home-hero { padding: 2.5rem 0 2.25rem; }
  .hero-grid { gap: 2.5rem; }
  .hero-copy h1 { font-size: 1.6rem; }
  .home-content { padding: 2.25rem 0 4rem; }
  .content-heading { align-items: flex-start; flex-direction: column; gap: .8rem; }
  .article-row { grid-template-columns: 24px minmax(0, 1fr); padding: 1.25rem 0; gap: .75rem; }
  .article-cover, .article-arrow { display: none; }
  .article-meta span:nth-child(3) { display: none; }
  .home-sidebar { grid-template-columns: 1fr; }
  .hero-note { display: none; }
}
</style>
