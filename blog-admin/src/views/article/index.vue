<template>
  <div class="article-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="标题/摘要" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" class="filter-select" placeholder="全部分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" class="filter-select" placeholder="全部状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="可见性">
          <el-select v-model="searchForm.visibility" class="filter-select" placeholder="全部" clearable>
            <el-option label="公开" :value="0" />
            <el-option label="私密" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="router.push('/article/create')">
        <el-icon><Plus /></el-icon>
        新建文章
      </el-button>
    </div>

    <!-- 文章列表 -->
    <el-card class="list-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="标签" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="tag in row.tags"
              :key="tag.id"
              size="small"
              style="margin-right: 4px;"
            >
              {{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="可见性" width="100">
          <template #default="{ row }">
            <el-tag :type="row.visibility === 1 ? 'warning' : 'success'">
              {{ row.visibility === 1 ? '私密' : '公开' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isTop ? 'warning' : 'info'">
              {{ row.isTop ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row.id)">编辑</el-button>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getArticleList, deleteArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'

const router = useRouter()

const loading = ref(false)
const tableData = ref<any[]>([])
const categories = ref<any[]>([])

const searchForm = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  status: undefined as number | undefined,
  visibility: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => {
  fetchCategories()
  fetchData()
})

async function fetchCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getArticleList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取文章列表失败', error)
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  searchForm.status = undefined
  searchForm.visibility = undefined
  pagination.pageNum = 1
  fetchData()
}

function handleEdit(id: number) {
  router.push(`/article/edit/${id}`)
}

async function handleDelete(id: number) {
  try {
    await deleteArticle(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('删除失败', error)
  }
}
</script>

<style scoped lang="scss">
.article-list {
  .search-bar {
    margin: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;

    .filter-select {
      width: 160px;
    }
  }

  .action-bar {
    margin: 0 20px 20px;
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
