<template>
  <div class="comment-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable @change="fetchData">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 评论列表 -->
    <el-card shadow="never" class="list-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="articleTitle" label="文章" min-width="150" show-overflow-tooltip />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="160" />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status].type">
              {{ statusMap[row.status].label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              @click="handleAudit(row.id, 1)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="warning"
              link
              @click="handleAudit(row.id, 2)"
            >
              拒绝
            </el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCommentList, auditComment, deleteComment } from '@/api/comment'

const loading = ref(false)
const tableData = ref<any[]>([])

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已拒绝', type: 'danger' }
}

const searchForm = reactive({
  status: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getCommentList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取评论失败', error)
  } finally {
    loading.value = false
  }
}

async function handleAudit(id: number, status: number) {
  try {
    await auditComment(id, status)
    ElMessage.success('操作成功')
    fetchData()
  } catch (error) {
    console.error('审核失败', error)
  }
}

async function handleDelete(id: number) {
  try {
    await deleteComment(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('删除失败', error)
  }
}
</script>

<style scoped lang="scss">
.comment-list {
  .search-bar {
    margin: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
  }

  .list-card {
    margin: 0 20px 20px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>