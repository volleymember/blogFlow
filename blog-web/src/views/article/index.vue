<template>
  <div>
    <article v-if="article" class="article-page">
      <header class="article-hero">
        <div class="article-header-inner">
          <nav class="article-breadcrumb" aria-label="面包屑">
            <router-link to="/">首页</router-link><span>/</span>
            <router-link v-if="article.categorySlug" :to="`/category/${article.categorySlug}`">{{ article.categoryName }}</router-link>
          </nav>
          <div v-if="!article.categorySlug || article.visibility === 1" class="article-kicker">
            <span v-if="!article.categorySlug">{{ article.categoryName || '随笔' }}</span>
            <span v-if="article.visibility === 1">仅登录可见</span>
          </div>
          <h1>{{ article.title }}</h1>
          <p v-if="article.summary" class="article-lead">{{ article.summary }}</p>
          <div class="article-byline">
            <img :src="avatarImg" alt="作者头像" />
            <div><strong>{{ article.authorName || '作者' }}</strong><span>{{ formatDate(article.createTime) }} · {{ readingTime }} 分钟阅读 · {{ article.viewCount || 0 }} 次浏览</span></div>
          </div>
        </div>
      </header>

      <div v-if="article.coverImage" class="article-cover-wrap">
        <img :src="article.coverImage" :alt="article.title" />
      </div>

      <div class="article-layout">
        <main class="article-main">
          <div class="article-content markdown-body" v-html="renderedContent" @click="handleArticleContentClick"></div>

            <!-- Like Button -->
            <div class="mt-8 flex justify-center animate-fade-in-up" style="animation-delay: 0.3s; opacity: 0;">
              <button
                class="group flex items-center gap-2 px-6 py-3 rounded-2xl font-medium text-sm transition-all duration-300"
                :class="hasLiked
                  ? 'bg-[var(--color-accent-soft)] text-[var(--color-accent)] border border-[var(--color-border)] hover:bg-[var(--color-accent-soft)]'
                  : 'bg-[var(--color-accent-soft)] text-[var(--color-accent)] border border-[var(--color-border)] hover:bg-[var(--color-accent)] hover:text-white hover:shadow-glow'"
                @click="toggleLike"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 transition-transform duration-300 group-hover:scale-110" :fill="hasLiked ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                {{ hasLiked ? '已点赞' : '点赞' }}
                <span class="font-mono text-xs opacity-70">({{ article.likeCount || 0 }})</span>
              </button>
            </div>

            <!-- Comments Section -->
            <section class="mt-10">
              <div class="card animate-fade-in-up" style="animation-delay: 0.35s; opacity: 0;">
                <h3 class="section-title mb-6">
                  评论
                  <span class="ml-2 text-sm font-mono font-normal text-[var(--color-text-tertiary)]">({{ comments.length }})</span>
                </h3>

                <!-- Comment Form -->
                <div class="mb-8 p-5 rounded-xl bg-[var(--color-accent-soft)] border border-[var(--color-border)]">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-3 mb-3">
                    <input
                      v-model="commentForm.nickname"
                      type="text"
                      placeholder="昵称 *"
                      class="px-3.5 py-2.5 rounded-xl bg-[var(--color-surface)] border border-[var(--color-border)] text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:border-[var(--color-accent)] focus:ring-1 focus:ring-[var(--color-accent-glow)] transition-all text-sm"
                    />
                    <input
                      v-model="commentForm.email"
                      type="email"
                      placeholder="邮箱（可选）"
                      class="px-3.5 py-2.5 rounded-xl bg-[var(--color-surface)] border border-[var(--color-border)] text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:border-[var(--color-accent)] focus:ring-1 focus:ring-[var(--color-accent-glow)] transition-all text-sm"
                    />
                  </div>
                  <textarea
                    v-model="commentForm.content"
                    placeholder="写下你的想法..."
                    rows="3"
                    class="w-full px-3.5 py-2.5 rounded-xl bg-[var(--color-surface)] border border-[var(--color-border)] text-[var(--color-text-primary)] placeholder:text-[var(--color-text-tertiary)] focus:outline-none focus:border-[var(--color-accent)] focus:ring-1 focus:ring-[var(--color-accent-glow)] transition-all resize-none text-sm"
                  ></textarea>
                  <div class="flex justify-end mt-3">
                    <button @click="submitComment" :disabled="submitting" class="btn btn-primary text-sm disabled:opacity-50">
                      {{ submitting ? '提交中...' : '发表评论' }}
                    </button>
                  </div>
                </div>

                <!-- Comment List -->
                <div v-if="comments.length" class="space-y-5">
                  <div v-for="comment in comments" :key="comment.id" class="group/comment">
                    <div class="flex gap-3">
                      <div class="w-8 h-8 rounded-full bg-[var(--color-text-primary)] flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                        {{ comment.nickname?.[0] || '?' }}
                      </div>
                      <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-2 mb-1">
                          <span class="text-sm font-medium text-[var(--color-text-primary)]">{{ comment.nickname }}</span>
                          <span class="text-[10px] font-mono text-[var(--color-text-tertiary)]">{{ formatDate(comment.createTime) }}</span>
                        </div>
                        <p class="text-sm text-[var(--color-text-secondary)] leading-relaxed">{{ comment.content }}</p>
                        <button
                          @click="replyTo(comment)"
                          class="mt-2 text-xs text-[var(--color-text-tertiary)] hover:text-[var(--color-accent)] transition-colors opacity-0 group-hover/comment:opacity-100"
                        >
                          回复
                        </button>
                      </div>
                    </div>

                    <!-- Replies -->
                    <div v-if="comment.children && comment.children.length" class="ml-11 mt-3 pl-4 border-l-2 border-[var(--color-border)] space-y-4">
                      <div v-for="child in comment.children" :key="child.id">
                        <div class="flex gap-3">
                          <div class="w-6 h-6 rounded-full bg-[var(--color-text-tertiary)] flex items-center justify-center text-white text-[10px] font-bold flex-shrink-0">
                            {{ child.nickname?.[0] || '?' }}
                          </div>
                          <div>
                            <div class="flex items-center gap-2 mb-1">
                              <span class="text-xs font-medium text-[var(--color-text-primary)]">{{ child.nickname }}</span>
                              <span v-if="child.replyToNickname" class="text-[10px] text-[var(--color-text-tertiary)]">
                                回复 <span class="text-[var(--color-accent)]">{{ child.replyToNickname }}</span>
                              </span>
                            </div>
                            <p class="text-xs text-[var(--color-text-secondary)] leading-relaxed">{{ child.content }}</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-else class="text-center py-10">
                  <p class="text-[var(--color-text-tertiary)] text-sm">暂无评论，来说点什么吧</p>
                </div>
              </div>
            </section>
        </main>

        <aside class="article-aside">
          <div class="aside-sticky">
            <section v-if="tocItems.length" class="article-toc">
              <span class="aside-label">本页目录</span>
              <a v-for="item in tocItems" :key="item.id" :href="`#${item.id}`" :class="[`toc-level-${item.level}`, { active: activeTocId === item.id }]">{{ item.text }}</a>
            </section>
            <section v-if="relatedArticles.length" class="article-related">
              <span class="aside-label">继续阅读</span>
              <router-link v-for="item in relatedArticles.slice(0, 3)" :key="item.id" :to="`/article/${item.id}`">{{ item.title }}</router-link>
            </section>
          </div>
        </aside>
      </div>
    </article>
    <section v-else-if="accessError" class="py-20">
      <div class="container-blog">
        <div class="max-w-xl mx-auto card text-center">
          <div class="w-14 h-14 mx-auto mb-5 rounded-2xl bg-[var(--color-accent-soft)] flex items-center justify-center text-[var(--color-accent)]">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h1 class="font-display text-2xl text-[var(--color-text-primary)] mb-3">无法查看私密文章</h1>
          <p class="text-sm text-[var(--color-text-secondary)] leading-relaxed">
            {{ accessError }}
          </p>
          <router-link to="/" class="btn btn-primary mt-6 inline-flex">返回首页</router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import { getArticle, getHotArticles } from '@/api/article'
import { getComments, submitComment as submitCommentApi } from '@/api/comment'
import { useAuthStore } from '@/stores/auth'
import avatarImg from '@/static/WechatIMG1062.jpg'

const route = useRoute()
const authStore = useAuthStore()

const article = ref<any>(null)
const comments = ref<any[]>([])
const relatedArticles = ref<any[]>([])
const activeTocId = ref('')
const submitting = ref(false)
const hasLiked = ref(false)
const accessError = ref('')

const commentForm = ref({
  nickname: '',
  email: '',
  content: ''
})

function escapeHtml(value: string) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

marked.setOptions({
  gfm: true,
  breaks: false
})

const languageAliases: Record<string, string> = {
  js: 'javascript',
  jsx: 'javascript',
  ts: 'typescript',
  tsx: 'typescript',
  py: 'python',
  sh: 'bash',
  shell: 'bash',
  'c++': 'cpp',
  cplusplus: 'cpp',
  'c#': 'csharp'
}

function normalizeLanguageName(language = '') {
  const rawLanguage = language.trim().split(/\s+/)[0].toLowerCase()
  const aliasedLanguage = languageAliases[rawLanguage] || rawLanguage
  return aliasedLanguage.replace(/[^\w-]/g, '')
}

function detectLanguageFromClassName(className = '') {
  const languageMatch = className.match(/(?:language|lang)-([\w+#-]+)/i)
  if (languageMatch) return normalizeLanguageName(languageMatch[1])

  const knownMatch = className.match(/\b(java|javascript|js|typescript|ts|python|py|cpp|c\+\+|csharp|go|rust|sql|bash|shell)\b/i)
  return knownMatch ? normalizeLanguageName(knownMatch[1]) : ''
}

function getCodeElementLanguage(pre: Element, codeElement: Element | null) {
  const dataCandidates = [
    codeElement?.getAttribute('data-language'),
    codeElement?.getAttribute('data-lang'),
    pre.getAttribute('data-language'),
    pre.getAttribute('data-lang')
  ]

  for (const candidate of dataCandidates) {
    const languageName = normalizeLanguageName(candidate || '')
    if (languageName) return languageName
  }

  const classCandidates = [
    codeElement?.getAttribute('class'),
    pre.getAttribute('class')
  ]

  for (const candidate of classCandidates) {
    if (!candidate) continue
    const languageName = detectLanguageFromClassName(candidate)
    if (languageName) return languageName
  }

  return ''
}

function detectLanguageFromCodeText(code: string) {
  if (/#include|std::|vector<|public:\s*$|private:\s*$/m.test(code)) return 'cpp'
  if (/System\.out|public\s+(?:class|interface)|\bint\[\]|\bHashMap\b|\bList<|\bString\[\]/.test(code)) return 'java'
  if (/\bdef\s+\w+\(|\bfrom\s+\w+\s+import\b|\bList\[|\bNone\b/.test(code)) return 'python'
  if (/\binterface\s+\w+|:\s*(string|number|boolean)\b|Array<\w+>/.test(code)) return 'typescript'
  if (/\bfunction\b|\bconst\b|\blet\b|=>|console\.log/.test(code)) return 'javascript'
  if (/\bfunc\s+\w+\(|fmt\.Println|:=/.test(code)) return 'go'
  if (/\bSELECT\b|\bINSERT\b|\bUPDATE\b|\bDELETE\b|\bFROM\b/i.test(code)) return 'sql'
  return ''
}

function isLikelyCodeText(code: string) {
  const lines = code.split('\n').filter(line => line.trim())
  if (lines.length < 2) return false

  const signalPatterns = [
    /[{};]/,
    /\b(class|public|private|protected|static|void|return|if|else|for|while|function|const|let|var|def|import|from|package|interface|extends|new|vector|map|hashmap)\b/i,
    /=>|==|!=|<=|>=|\+\+|--|&&|\|\||::/,
    /#include|System\.out|console\.log|fmt\.Println/
  ]
  const signalCount = signalPatterns.filter(pattern => pattern.test(code)).length
  return signalCount >= 2
}

function hasOnlyCodeText(paragraph: HTMLParagraphElement) {
  if (!paragraph.querySelector('code')) return false

  const walker = document.createTreeWalker(paragraph, NodeFilter.SHOW_TEXT)
  let currentNode = walker.nextNode()
  while (currentNode) {
    const textNode = currentNode as Text
    const parentElement = textNode.parentElement
    if (!parentElement?.closest('code') && textNode.textContent?.trim()) {
      return false
    }
    currentNode = walker.nextNode()
  }

  return Boolean(paragraph.textContent?.trim())
}

function getCodeParagraphLines(paragraph: HTMLParagraphElement) {
  return (paragraph.textContent || '')
    .replace(/\u00a0/g, ' ')
    .replace(/\r\n?/g, '\n')
    .split('\n')
    .map(line => line.replace(/\s+$/g, ''))
    .filter(line => line.trim())
}

function reindentBraceCode(lines: string[]) {
  let depth = 0
  return lines.map(rawLine => {
    const line = rawLine.trim()
    if (/^[}\])]/.test(line)) {
      depth = Math.max(0, depth - 1)
    }

    const nextLine = `${'    '.repeat(depth)}${line}`
    const openCount = (line.match(/{/g) || []).length
    const closeCount = (line.match(/}/g) || []).length
    depth = Math.max(0, depth + openCount - closeCount)
    return nextLine
  })
}

function normalizeInlineCodeParagraphs(root: ParentNode) {
  const paragraphs = Array.from(root.querySelectorAll('p')) as HTMLParagraphElement[]
  let group: HTMLParagraphElement[] = []

  const flushGroup = () => {
    if (group.length < 2) {
      group = []
      return
    }

    const rawLines = group.flatMap(getCodeParagraphLines)
    const code = reindentBraceCode(rawLines).join('\n')
    if (!isLikelyCodeText(code)) {
      group = []
      return
    }

    const wrapper = document.createElement('template')
    wrapper.innerHTML = createCodeBlockHtml(code, detectLanguageFromCodeText(code))
    const codeBlock = wrapper.content.firstElementChild
    if (codeBlock) {
      group[0].replaceWith(codeBlock)
      group.slice(1).forEach(paragraph => paragraph.remove())
    }
    group = []
  }

  paragraphs.forEach(paragraph => {
    if (paragraph.closest('.code-block')) return

    const shouldJoin = hasOnlyCodeText(paragraph)
    const previous = group[group.length - 1]
    if (!shouldJoin || (previous && previous.nextElementSibling !== paragraph)) {
      flushGroup()
    }

    if (shouldJoin) {
      group.push(paragraph)
    }
  })
  flushGroup()
}

function highlightCode(code: string, languageName: string) {
  try {
    if (languageName && hljs.getLanguage(languageName)) {
      return hljs.highlight(code, { language: languageName }).value
    }
    return hljs.highlightAuto(code).value
  } catch (e) {
    return escapeHtml(code)
  }
}

function createCodeBlockHtml(code: string, language = '') {
  const languageName = normalizeLanguageName(language)
  const languageClass = languageName ? ` language-${languageName}` : ''
  const langLabel = languageName || 'text'
  const highlightedCode = highlightCode(code, languageName)

  return `
    <div class="code-block">
      <div class="code-header">
        <span class="code-lang">${escapeHtml(langLabel)}</span>
        <button class="copy-btn" type="button">复制</button>
      </div>
      <pre class="code-content"><code class="hljs${languageClass}">${highlightedCode}</code></pre>
    </div>
  `
}

function normalizeRawCodeBlocks(html: string) {
  if (typeof document === 'undefined') return html

  const template = document.createElement('template')
  template.innerHTML = html

  normalizeInlineCodeParagraphs(template.content)

  template.content.querySelectorAll('pre').forEach(pre => {
    if (pre.closest('.code-block')) return

    const codeElement = pre.querySelector('code')
    const code = codeElement?.textContent ?? pre.textContent ?? ''
    const languageName = getCodeElementLanguage(pre, codeElement)
    const wrapper = document.createElement('template')
    wrapper.innerHTML = createCodeBlockHtml(code, languageName)
    const codeBlock = wrapper.content.firstElementChild
    if (codeBlock) {
      pre.replaceWith(codeBlock)
    }
  })

  return template.innerHTML
}

// Custom marked renderer
const renderer = {
  code(code: string, language: string) {
    return createCodeBlockHtml(code, language)
  }
}

marked.use({ renderer })

const processedArticle = computed(() => {
  if (!article.value?.content) return { html: '', toc: [] as Array<{ id: string; text: string; level: number }> }
  const html = marked(article.value.content) as string
  const normalizedHtml = normalizeRawCodeBlocks(html)
  const sanitized = DOMPurify.sanitize(normalizedHtml, {
    ADD_TAGS: ['iframe', 'button', 'span', 'div', 'pre', 'code', 'input'],
    ADD_ATTR: ['class', 'target', 'rel', 'allow', 'allowfullscreen', 'frameborder', 'type', 'checked', 'disabled', 'align', 'style', 'id']
  })

  if (typeof document === 'undefined') return { html: sanitized, toc: [] }
  const template = document.createElement('template')
  template.innerHTML = sanitized
  const toc: Array<{ id: string; text: string; level: number }> = []
  template.content.querySelectorAll('h2, h3').forEach((heading, index) => {
    const text = heading.textContent?.trim() || `章节 ${index + 1}`
    const id = `section-${index + 1}`
    heading.id = id
    toc.push({ id, text, level: Number(heading.tagName.slice(1)) })
  })
  return { html: template.innerHTML, toc }
})

const renderedContent = computed(() => processedArticle.value.html)
const tocItems = computed(() => processedArticle.value.toc)
const readingTime = computed(() => {
  const text = article.value?.content?.replace(/<[^>]*>|[#_*`>\[\]()!-]/g, ' ') || ''
  return Math.max(1, Math.ceil(text.trim().length / 500))
})

function handleArticleContentClick(event: MouseEvent) {
  const target = event.target as HTMLElement | null
  const button = target?.closest<HTMLButtonElement>('.copy-btn')
  if (!button) return
  void copyCode(button)
}

async function copyCode(btn: HTMLButtonElement) {
  const codeBlock = btn.closest('.code-block')
  const code = codeBlock?.querySelector('code')?.textContent || ''
  if (!code) return

  await writeTextToClipboard(code)
  btn.textContent = '已复制'
  btn.classList.add('copied')
  window.setTimeout(() => {
    btn.textContent = '复制'
    btn.classList.remove('copied')
  }, 2000)
}

async function writeTextToClipboard(text: string) {
  if (navigator.clipboard?.writeText) {
    try {
      await navigator.clipboard.writeText(text)
      return
    } catch (error) {
      console.warn('Clipboard API failed, falling back to textarea copy.', error)
    }
  }

  const textarea = document.createElement('textarea')
  textarea.value = text
  textarea.setAttribute('readonly', '')
  textarea.style.position = 'fixed'
  textarea.style.left = '-9999px'
  document.body.appendChild(textarea)
  textarea.select()
  document.execCommand('copy')
  textarea.remove()
}

function checkLikeStatus() {
  if (!article.value?.id) return
  const likedArticles = JSON.parse(localStorage.getItem('likedArticles') || '[]')
  hasLiked.value = likedArticles.includes(article.value.id)
}

function toggleLike() {
  if (!article.value) return
  const likedArticles: number[] = JSON.parse(localStorage.getItem('likedArticles') || '[]')
  const articleId = article.value.id

  if (hasLiked.value) {
    article.value.likeCount = Math.max(0, (article.value.likeCount || 0) - 1)
    const index = likedArticles.indexOf(articleId)
    if (index > -1) likedArticles.splice(index, 1)
    hasLiked.value = false
  } else {
    article.value.likeCount = (article.value.likeCount || 0) + 1
    likedArticles.push(articleId)
    hasLiked.value = true
  }

  localStorage.setItem('likedArticles', JSON.stringify(likedArticles))
}

// 目录高亮：滚动时标记当前阅读到的章节
function updateActiveToc() {
  const items = tocItems.value
  if (!items.length) return
  let current = items[0].id
  for (const item of items) {
    const el = document.getElementById(item.id)
    if (el && el.getBoundingClientRect().top <= 120) current = item.id
  }
  activeTocId.value = current
}

let tocTicking = false
function handleTocScroll() {
  if (tocTicking) return
  tocTicking = true
  window.requestAnimationFrame(() => {
    updateActiveToc()
    tocTicking = false
  })
}

onBeforeUnmount(() => window.removeEventListener('scroll', handleTocScroll))

onMounted(async () => {
  window.addEventListener('scroll', handleTocScroll, { passive: true })
  await loadArticle()
  fetchRelated()
})

watch(() => route.params.id, async () => {
  if (route.params.id) {
    await loadArticle()
    fetchRelated()
  }
})

watch(() => [authStore.token, authStore.isAdmin], async () => {
  if (route.params.id) {
    await loadArticle()
    fetchRelated()
  }
})

function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

async function loadArticle() {
  comments.value = []
  accessError.value = ''
  article.value = null
  const loaded = await fetchArticle()
  if (loaded) {
    fetchComments()
  }
}

async function fetchArticle() {
  try {
    const res = await getArticle(Number(route.params.id))
    article.value = res.data
    checkLikeStatus()
    return true
  } catch (error) {
    accessError.value = (error as Error).message || '当前账号无权查看这篇文章'
    console.error('获取文章失败', error)
    return false
  }
}

async function fetchComments() {
  try {
    const res = await getComments(Number(route.params.id))
    comments.value = res.data
  } catch (error) {
    console.error('获取评论失败', error)
  }
}

async function fetchRelated() {
  try {
    const res = await getHotArticles(5)
    relatedArticles.value = res.data.filter((a: any) => a.id !== Number(route.params.id)).slice(0, 4)
  } catch (error) {
    console.error('获取相关文章失败', error)
  }
}

async function submitComment() {
  if (!commentForm.value.nickname || !commentForm.value.content) {
    alert('请填写昵称和评论内容')
    return
  }

  submitting.value = true
  try {
    await submitCommentApi({
      articleId: Number(route.params.id),
      nickname: commentForm.value.nickname,
      email: commentForm.value.email,
      content: commentForm.value.content
    })
    commentForm.value.content = ''
    alert('评论提交成功，等待审核')
    fetchComments()
  } catch (error) {
    console.error('提交评论失败', error)
  } finally {
    submitting.value = false
  }
}

function replyTo(comment: any) {
  commentForm.value.content = `@${comment.nickname} `
}
</script>

<style>
.article-page {
  --article-col: 720px;
  --aside-col: 220px;
  --article-gap: clamp(2.5rem, 5vw, 4.5rem);
  --article-text: #3b423d;
  padding-bottom: 5rem;
}
.dark .article-page { --article-text: #c9d0ca; }
/* 标题区、封面、正文共用同一套栅格，左边缘严格对齐 */
.article-hero,
.article-cover-wrap,
.article-layout {
  display: grid;
  grid-template-columns: minmax(0, var(--article-col)) var(--aside-col);
  justify-content: center;
  column-gap: var(--article-gap);
  margin: 0 auto;
  padding-left: 1.5rem;
  padding-right: 1.5rem;
}
.article-hero { padding-top: clamp(2.5rem, 5vw, 3.75rem); }
.article-header-inner { grid-column: 1; min-width: 0; padding-bottom: 2rem; border-bottom: 1px solid var(--color-border); }
.article-breadcrumb { display: flex; gap: .55rem; margin-bottom: 1.5rem; color: var(--color-text-tertiary); font-size: .82rem; }
.article-breadcrumb a { color: inherit; }
.article-breadcrumb a:hover { color: var(--color-accent); }
.article-kicker { display: flex; gap: .7rem; margin-bottom: .9rem; color: var(--color-accent); font: 650 .74rem/1.2 var(--font-mono); letter-spacing: .1em; text-transform: uppercase; }
.article-header-inner h1 { margin: 0; font-size: clamp(1.6rem, 3vw, 2.25rem); font-weight: 650; line-height: 1.4; letter-spacing: 0; text-wrap: balance; }
.article-lead { margin: 1rem 0 0; color: var(--color-text-secondary); font-size: 1rem; line-height: 1.8; }
.article-byline { display: flex; align-items: center; gap: .75rem; margin-top: 1.5rem; }
.article-byline img { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.article-byline div { display: flex; flex-direction: column; gap: .18rem; }
.article-byline strong { color: var(--color-text-primary); font-size: .88rem; }
.article-byline span { color: var(--color-text-tertiary); font-size: .8rem; }
.article-cover-wrap { margin-top: 2rem; }
.article-cover-wrap img { grid-column: 1; display: block; width: 100%; max-height: 460px; border-radius: 12px; object-fit: cover; }
.article-layout { padding-top: 2.25rem; }
.article-main { min-width: 0; }
.article-aside { min-width: 0; }
.aside-sticky { position: sticky; top: 96px; display: flex; flex-direction: column; gap: 2rem; max-height: calc(100vh - 120px); overflow-y: auto; }
.article-toc, .article-related { display: flex; flex-direction: column; gap: .15rem; }
.aside-label { margin-bottom: .6rem; color: var(--color-text-tertiary); font-size: .75rem; font-weight: 600; letter-spacing: .08em; }
.article-toc a { display: block; padding: .3rem 0 .3rem .85rem; border-left: 2px solid var(--color-border); color: var(--color-text-tertiary); font-size: .84rem; line-height: 1.5; transition: color .2s ease, border-color .2s ease; }
.article-toc a.toc-level-3 { padding-left: 1.6rem; font-size: .8rem; }
.article-toc a:hover { color: var(--color-text-primary); }
.article-toc a.active { border-left-color: var(--color-accent); color: var(--color-accent); font-weight: 600; }
.article-related a { display: block; padding: .35rem 0; color: var(--color-text-secondary); font-size: .86rem; line-height: 1.55; }
.article-related a:hover { color: var(--color-accent); }

/* ---- 正文排版 ---- */
.article-content { color: var(--article-text); }
.article-content p { color: var(--article-text); }
.article-content strong { color: var(--color-text-primary); font-weight: 650; }
.article-content ul { list-style: disc; padding-left: 1.5em; }
.article-content ol { list-style: decimal; padding-left: 1.6em; }
.article-content ul ul { list-style: circle; }
.article-content li { padding-left: .2em; }
.article-content li::marker { color: var(--color-text-tertiary); }
.article-content hr { height: 1px; margin: 2.5em 0; background: var(--color-border); border: 0; }
.article-content h2 { margin-top: 2.2em; padding-bottom: .4em; border-bottom: 1px solid var(--color-border-subtle); }
.article-content h3 { margin-top: 1.8em; }
.article-content { font-family: var(--font-body); font-size: 1rem; line-height: 1.85; letter-spacing: .01em; }
.article-content p { margin-bottom: 1em; }
.article-content h2, .article-content h3 { scroll-margin-top: 100px; }
@media (max-width: 900px) {
  .article-hero, .article-cover-wrap, .article-layout { grid-template-columns: minmax(0, var(--article-col)); }
  .article-aside { display: none; }
}
@media (max-width: 640px) {
  .article-hero { padding-top: 2rem; }
  .article-breadcrumb { margin-bottom: 1.2rem; }
  .article-layout { padding-top: 2rem; }
  .article-content { font-size: .97rem; line-height: 1.8; }
}

/* Code block styles (global for v-html) */
.code-block {
  margin: 24px 0;
  border-radius: 8px;
  max-width: 100%;
  min-width: 0;
  overflow: hidden;
  background: #f8fafc;
  border: 1px solid var(--color-border);
  color: #24292f;
}

.dark .code-block {
  background: #0d1117;
  color: #c9d1d9;
  border-color: #30363d;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 7px 12px;
  background: rgba(90, 126, 194, 0.04);
  border-bottom: 1px solid var(--color-border);
}

.dark .code-header {
  background: rgba(255, 255, 255, 0.03);
  border-bottom-color: #30363d;
}

.code-lang {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 11px;
  color: #57606a;
  font-family: var(--font-mono);
  letter-spacing: 0;
}

.dark .code-lang {
  color: #8b949e;
}

.copy-btn {
  flex-shrink: 0;
  padding: 3px 10px;
  font-size: 11px;
  color: #57606a;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-mono);
}

.copy-btn:hover {
  background: rgba(90, 126, 194, 0.08);
  color: var(--color-text-primary);
}

.dark .copy-btn {
  color: #8b949e;
  border-color: #30363d;
}

.dark .copy-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #c9d1d9;
}

.copy-btn.copied {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
  border-color: rgba(16, 185, 129, 0.3);
}

.code-content {
  display: block;
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 18px;
  overflow-x: auto;
  overflow-y: hidden;
  white-space: pre;
  word-break: normal;
  overflow-wrap: normal;
  -webkit-overflow-scrolling: touch;
  background: transparent;
  color: inherit;
  scrollbar-width: thin;
  scrollbar-color: rgba(148, 163, 184, 0.55) transparent;
}

.code-content::-webkit-scrollbar {
  height: 10px;
}

.code-content::-webkit-scrollbar-track {
  background: transparent;
}

.code-content::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.35);
  border-radius: 999px;
  border: 3px solid transparent;
  background-clip: content-box;
}

.code-content::-webkit-scrollbar-thumb:hover {
  background: rgba(148, 163, 184, 0.55);
}

.code-content code {
  display: block;
  width: max-content;
  min-width: 100%;
  font-family: var(--font-mono);
  font-size: 0.875rem;
  line-height: 1.65;
  white-space: inherit;
  word-break: inherit;
  overflow-wrap: inherit;
  tab-size: 4;
  color: inherit;
}

/* Highlight.js syntax colors */
.hljs-comment, .hljs-quote { color: #6e7781; font-style: italic; }
.hljs-keyword, .hljs-selector-tag, .hljs-type { color: #cf222e; }
.hljs-string, .hljs-attr { color: #0a3069; }
.hljs-number, .hljs-meta, .hljs-literal, .hljs-symbol, .hljs-bullet { color: #0550ae; }
.hljs-function, .hljs-class, .hljs-title { color: #8250df; }
.hljs-variable, .hljs-params, .hljs-built_in { color: #953800; }
.hljs-addition { color: #116329; background: rgba(46, 160, 67, 0.12); }
.hljs-deletion { color: #82071e; background: rgba(248, 81, 73, 0.12); }

.dark .hljs-comment, .dark .hljs-quote { color: #8b949e; }
.dark .hljs-keyword, .dark .hljs-selector-tag, .dark .hljs-type { color: #ff7b72; }
.dark .hljs-string, .dark .hljs-attr { color: #a5d6ff; }
.dark .hljs-number, .dark .hljs-meta, .dark .hljs-literal, .dark .hljs-symbol, .dark .hljs-bullet { color: #79c0ff; }
.dark .hljs-function, .dark .hljs-class, .dark .hljs-title { color: #d2a8ff; }
.dark .hljs-variable, .dark .hljs-params, .dark .hljs-built_in { color: #ffa657; }
.dark .hljs-addition { color: #aff5b4; background: rgba(46, 160, 67, 0.15); }
.dark .hljs-deletion { color: #ffdcd7; background: rgba(248, 81, 73, 0.15); }
</style>
