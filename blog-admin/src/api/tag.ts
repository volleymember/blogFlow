import request from '@/utils/request'

// 获取标签列表
export function getTagList() {
  return request.get('/admin/tags')
}

// 获取标签详情
export function getTag(id: number) {
  return request.get(`/admin/tags/${id}`)
}

// 创建标签
export function createTag(data: any) {
  return request.post('/admin/tags', data)
}

// 更新标签
export function updateTag(id: number, data: any) {
  return request.put(`/admin/tags/${id}`, data)
}

// 删除标签
export function deleteTag(id: number) {
  return request.delete(`/admin/tags/${id}`)
}