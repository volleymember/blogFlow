<template>
  <div class="category-list">
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建分类
      </el-button>
    </div>

    <!-- 分类列表 -->
    <el-card shadow="never" class="list-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="slug" label="标识" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="articleCount" label="文章数" width="100" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑分类' : '新建分类'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="标识" prop="slug">
          <el-input v-model="form.slug" placeholder="URL标识" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="分类描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '@/api/category'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const formRef = ref()

const form = reactive({
  name: '',
  slug: '',
  description: '',
  sortOrder: 0
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  slug: [{ required: true, message: '请输入标识', trigger: 'blur' }]
}

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getCategoryList()
    tableData.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  editId.value = null
  Object.assign(form, { name: '', slug: '', description: '', sortOrder: 0 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  editId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (editId.value) {
      await updateCategory(editId.value, form)
    } else {
      await createCategory(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await deleteCategory(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('删除失败', error)
  }
}
</script>

<style scoped lang="scss">
.category-list {
  .action-bar {
    margin: 20px;
  }

  .list-card {
    margin: 0 20px 20px;
  }
}
</style>