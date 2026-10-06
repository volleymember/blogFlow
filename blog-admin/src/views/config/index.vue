<template>
  <div class="config-page">
    <el-card shadow="never">
      <template #header>
        <span>系统配置</span>
      </template>

      <el-form ref="formRef" :model="form" label-width="100px" v-loading="loading">
        <el-divider content-position="left">基本信息</el-divider>

        <el-form-item label="网站标题">
          <el-input v-model="form.site_title" placeholder="网站标题" />
        </el-form-item>
        <el-form-item label="网站副标题">
          <el-input v-model="form.site_subtitle" placeholder="网站副标题" />
        </el-form-item>
        <el-form-item label="网站描述">
          <el-input v-model="form.site_description" type="textarea" :rows="2" placeholder="网站描述" />
        </el-form-item>
        <el-form-item label="SEO关键词">
          <el-input v-model="form.site_keywords" placeholder="SEO关键词" />
        </el-form-item>

        <el-divider content-position="left">图片设置</el-divider>

        <el-form-item label="网站Logo">
          <el-input v-model="form.site_logo" placeholder="Logo URL" />
        </el-form-item>
        <el-form-item label="Favicon">
          <el-input v-model="form.site_favicon" placeholder="Favicon URL" />
        </el-form-item>

        <el-divider content-position="left">社交链接</el-divider>

        <el-form-item label="GitHub">
          <el-input v-model="form.social_github" placeholder="GitHub链接" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.social_email" placeholder="联系邮箱" />
        </el-form-item>
        <el-form-item label="微信二维码">
          <el-input v-model="form.social_wechat" placeholder="微信二维码图片URL" />
        </el-form-item>

        <el-divider content-position="left">其他设置</el-divider>

        <el-form-item label="备案号">
          <el-input v-model="form.site_icp" placeholder="备案号" />
        </el-form-item>
        <el-form-item label="评论审核">
          <el-switch v-model="form.comment_audit" active-value="true" inactive-value="false" />
          <span style="margin-left: 10px; color: #999;">开启后评论需要审核才能显示</span>
        </el-form-item>
        <el-form-item label="默认主题">
          <el-radio-group v-model="form.theme_mode">
            <el-radio label="light">亮色</el-radio>
            <el-radio label="dark">暗色</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSiteConfig, updateSiteConfig } from '@/api/config'

const loading = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive<Record<string, string>>({
  site_title: '',
  site_subtitle: '',
  site_description: '',
  site_keywords: '',
  site_logo: '',
  site_favicon: '',
  site_icp: '',
  social_github: '',
  social_email: '',
  social_wechat: '',
  comment_audit: 'true',
  theme_mode: 'light'
})

onMounted(() => {
  fetchConfig()
})

async function fetchConfig() {
  loading.value = true
  try {
    const res = await getSiteConfig()
    Object.assign(form, res.data)
  } catch (error) {
    console.error('获取配置失败', error)
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  submitting.value = true
  try {
    await updateSiteConfig(form)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.config-page {
  margin: 20px;
}
</style>