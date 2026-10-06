<template>
  <div>
    <!-- Hero Profile Section -->
    <section class="relative overflow-hidden py-20 md:py-28">
      <div class="absolute inset-0 grid-lines opacity-30"></div>
      <div class="absolute inset-0 bg-gradient-to-b from-transparent via-transparent to-[var(--color-bg)]"></div>

      <div class="container-blog relative">
        <div class="max-w-4xl mx-auto text-center">
          <!-- Avatar -->
          <div class="w-28 h-28 mx-auto mb-6">
            <img :src="avatarImg" alt="avatar" class="w-full h-full rounded-full object-cover" />
          </div>

          <!-- Name -->
          <h1 class="font-display text-display-sm md:text-display text-[var(--color-text-primary)] mb-3 animate-fade-in-up">
            {{ config.title || '博客作者' }}
          </h1>

          <!-- Title -->
          <p class="text-lg text-[var(--color-text-secondary)] mb-6 animate-fade-in-up" style="animation-delay: 0.1s; opacity: 0;">
            {{ config.subtitle || '全栈开发者' }}
          </p>

          <!-- Social Links -->
          <div class="flex items-center justify-center gap-3 animate-fade-in-up" style="animation-delay: 0.2s; opacity: 0;">
            <a v-if="config.github" :href="config.github" target="_blank" rel="noopener noreferrer"
              class="w-10 h-10 rounded-xl flex items-center justify-center bg-[var(--color-accent-soft)] text-[var(--color-accent)] hover:bg-[var(--color-accent)] hover:text-white transition-all duration-300 hover:-translate-y-0.5 hover:shadow-glow">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path fill-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" clip-rule="evenodd" /></svg>
            </a>
            <a v-if="config.email" :href="`mailto:${config.email}`"
              class="w-10 h-10 rounded-xl flex items-center justify-center bg-[var(--color-accent-soft)] text-[var(--color-accent)] hover:bg-[var(--color-accent)] hover:text-white transition-all duration-300 hover:-translate-y-0.5 hover:shadow-glow">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
            </a>
          </div>
        </div>
      </div>
    </section>

    <!-- About Content -->
    <section class="pb-20">
      <div class="container-blog">
        <div class="max-w-4xl mx-auto space-y-12">

          <!-- About Me -->
          <div class="card animate-fade-in-up">
            <h2 class="section-title mb-6">关于我</h2>
            <div class="space-y-4 text-[var(--color-text-secondary)] leading-relaxed">
              <p>
                你好！欢迎来到我的博客。我是一名热爱技术的全栈开发者，专注于构建高质量的 Web 应用。
              </p>
              <p>
                在这里，我会分享技术文章、学习笔记和项目经验。我相信持续学习和知识分享是成长为优秀工程师的关键。
              </p>
              <p>
                如果你对技术有同样的热情，或者想要交流合作，欢迎通过上方的联系方式与我联系。
              </p>
            </div>
          </div>

          <!-- Tech Stack -->
          <div class="card animate-fade-in-up">
            <h2 class="section-title mb-6">技术栈</h2>

            <!-- 后端 -->
            <div class="mb-5">
              <h4 class="text-xs font-semibold text-[var(--color-text-tertiary)] uppercase tracking-widest mb-3 flex items-center gap-2">
                <span class="w-5 h-px bg-[var(--color-accent)]"></span>
                后端开发
              </h4>
              <div class="flex flex-wrap gap-2">
                <span class="skill-tag">Java</span>
                <span class="skill-tag">Spring Boot</span>
                <span class="skill-tag">MyBatis</span>
                <span class="skill-tag">MySQL</span>
                <span class="skill-tag">Redis</span>
                <span class="skill-tag">Maven</span>
              </div>
            </div>

            <!-- 前端 -->
            <div class="mb-5">
              <h4 class="text-xs font-semibold text-[var(--color-text-tertiary)] uppercase tracking-widest mb-3 flex items-center gap-2">
                <span class="w-5 h-px bg-[var(--color-accent)]"></span>
                前端开发
              </h4>
              <div class="flex flex-wrap gap-2">
                <span class="skill-tag">Vue.js</span>
                <span class="skill-tag">Element Plus</span>
              </div>
            </div>

            <!-- 工具 & 运维 -->
            <div class="mb-5">
              <h4 class="text-xs font-semibold text-[var(--color-text-tertiary)] uppercase tracking-widest mb-3 flex items-center gap-2">
                <span class="w-5 h-px bg-[var(--color-accent)]"></span>
                工具 & 运维
              </h4>
              <div class="flex flex-wrap gap-2">
                <span class="skill-tag">Docker</span>
                <span class="skill-tag">Git</span>
                <span class="skill-tag">Linux</span>
                <span class="skill-tag">Nginx</span>
              </div>
            </div>

            <!-- 其他 -->
            <div>
              <h4 class="text-xs font-semibold text-[var(--color-text-tertiary)] uppercase tracking-widest mb-3 flex items-center gap-2">
                <span class="w-5 h-px bg-[var(--color-accent)]"></span>
                其他技能
              </h4>
              <div class="flex flex-wrap gap-2">
                <span class="skill-tag">RESTful API</span>
                <span class="skill-tag">JWT</span>
                <span class="skill-tag">DDD</span>
                <span class="skill-tag">设计模式</span>
              </div>
            </div>
          </div>


          <!-- Contact CTA -->
          <div class="relative rounded-2xl overflow-hidden animate-fade-in-up">
            <div class="absolute inset-0 bg-animated-gradient opacity-90"></div>
            <div class="absolute inset-0 bg-noise"></div>
            <div class="relative text-center py-12 px-6">
              <h2 class="font-display text-2xl font-bold text-white mb-3">期待与你交流</h2>
              <p class="text-white/80 text-sm mb-6 max-w-md mx-auto">
                如果你对技术有热情，或是有合作想法，欢迎通过以下方式联系我
              </p>
              <div class="flex items-center justify-center gap-3">
                <a v-if="config.email" :href="`mailto:${config.email}`"
                  class="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-white/20 backdrop-blur-sm text-white font-medium text-sm hover:bg-white/30 transition-all duration-300 border border-white/20">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
                  发送邮件
                </a>
                <a v-if="config.github" :href="config.github" target="_blank" rel="noopener noreferrer"
                  class="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-white/20 backdrop-blur-sm text-white font-medium text-sm hover:bg-white/30 transition-all duration-300 border border-white/20">
                  <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path fill-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" clip-rule="evenodd" /></svg>
                  GitHub
                </a>
              </div>
            </div>
          </div>

        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { getSiteConfig } from '@/api/config'
import avatarImg from '@/static/WechatIMG1062.jpg'

const appStore = useAppStore()
const config = computed(() => appStore.siteConfig || {})

onMounted(async () => {
  if (!appStore.siteConfig) {
    try {
      const res = await getSiteConfig()
      appStore.setSiteConfig(res.data)
    } catch (error) {
      console.error('获取配置失败', error)
    }
  }
})
</script>

<style scoped>
.skill-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.8125rem;
  font-family: var(--font-mono);
  background: var(--color-surface);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
  transition: all 0.25s var(--transition-smooth);
}

.skill-tag:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
  transform: translateY(-1px);
}

.tech-badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.6875rem;
  font-family: var(--font-mono);
  font-weight: 500;
  background: var(--color-accent-soft);
  color: var(--color-accent);
  letter-spacing: 0.02em;
}
</style>
