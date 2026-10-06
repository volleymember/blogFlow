import request from '@/utils/request'

// 获取分类列表
export function getCategoryList() {
  return request.get('/admin/categories')
}

// 获取分类详情
export function getCategory(id: number) {
  return request.get(`/admin/categories/${id}`)
}

// 创建分类
export function createCategory(data: any) {
  return request.post('/admin/categories', data)
}

// 更新分类
export function updateCategory(id: number, data: any) {
  return request.put(`/admin/categories/${id}`, data)
}

// 删除分类
export function deleteCategory(id: number) {
  return request.delete(`/admin/categories/${id}`)
}