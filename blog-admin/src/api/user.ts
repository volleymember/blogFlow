import request from '@/utils/request'

// 获取用户列表
export function getUserList(keyword?: string) {
  return request.get('/admin/users', { params: { keyword } })
}

// 获取用户详情
export function getUser(id: number) {
  return request.get(`/admin/users/${id}`)
}

// 创建用户
export function createUser(data: any) {
  return request.post('/admin/users', data)
}

// 更新用户
export function updateUser(id: number, data: any) {
  return request.put(`/admin/users/${id}`, data)
}

// 删除用户
export function deleteUser(id: number) {
  return request.delete(`/admin/users/${id}`)
}