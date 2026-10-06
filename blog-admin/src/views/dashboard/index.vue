<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon><View /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayPv || 0 }}</div>
              <div class="stat-label">今日访问</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayUv || 0 }}</div>
              <div class="stat-label">今日访客</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.articleCount || 0 }}</div>
              <div class="stat-label">文章总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon><ChatDotSquare /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.commentCount || 0 }}</div>
              <div class="stat-label">评论总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门文章 -->
    <el-card class="page-card" shadow="never">
      <template #header>
        <span>热门文章 TOP 10</span>
      </template>
      <el-table :data="stats.hotArticles || []" stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/config'

const stats = ref<any>({
  todayPv: 0,
  todayUv: 0,
  articleCount: 0,
  categoryCount: 0,
  commentCount: 0,
  hotArticles: [],
  pvTrend: []
})

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    stats.value = res.data
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-cards {
    margin: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          font-size: 28px;
        }

        .stat-info {
          margin-left: 16px;

          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #333;
          }

          .stat-label {
            font-size: 14px;
            color: #999;
            margin-top: 4px;
          }
        }
      }
    }
  }
}
</style>