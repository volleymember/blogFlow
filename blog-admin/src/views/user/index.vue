<template>
  <div class="user-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="keyword" placeholder="用户名/昵称/邮箱" clearable @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建用户
      </el-button>
    </div>

    <!-- 用户列表 -->
    <el-card shadow="never" class="list-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="权限" width="110">
          <template #default="{ row }">
            <el-tag :type="row.roleKey === 'ADMIN' ? 'danger' : 'info'">
              {{ formatRole(row.roleKey) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar v-if="row.avatar" :src="row.avatar" :size="40" />
            <el-avatar v-else icon="User" :size="40" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑用户' : '新建用户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名" :disabled="!!editId" />
        </el-form-item>
        <el-form-item label="密码" :prop="editId ? '' : 'password'">
          <el-input v-model="form.password" type="password" :placeholder="editId ? '留空则不修改' : '密码'" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="邮箱" />
        </el-form-item>
        <el-form-item label="权限" prop="roleKey">
          <el-select v-model="form.roleKey" placeholder="请选择权限" style="width: 100%">
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="form.avatar" placeholder="头像URL" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { getUserList, createUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const keyword = ref('')
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
  avatar: '',
  roleKey: 'USER',
  status: 1
})

const roleOptions = [
  { label: '普通用户', value: 'USER' },
  { label: '管理员', value: 'ADMIN' }
]

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList(keyword.value)
    tableData.value = res.data
  } catch (error) {
    console.error('获取用户失败', error)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  editId.value = null
  Object.assign(form, { username: '', password: '', nickname: '', email: '', avatar: '', roleKey: 'USER', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  editId.value = row.id
  Object.assign(form, { ...row, roleKey: row.roleKey || 'USER', password: '' })
  dialogVisible.value = true
}

function formatRole(roleKey: string) {
  const role = roleOptions.find(item => item.value === roleKey)
  return role ? role.label : '未设置'
}

async function handleSubmit() {
  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    const data = { ...form }
    if (editId.value && !data.password) {
      delete (data as any).password
    }
    if (editId.value) {
      await updateUser(editId.value, data)
    } else {
      await createUser(data)
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
    await deleteUser(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('删除失败', error)
  }
}
</script>

<style scoped lang="scss">
.user-list {
  .search-bar {
    margin: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
  }

  .action-bar {
    margin: 0 20px 20px;
  }

  .list-card {
    margin: 0 20px 20px;
  }
}
</style>
