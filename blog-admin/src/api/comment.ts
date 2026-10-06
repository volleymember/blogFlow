import request from '@/utils/request'

// 获取评论列表
export function getCommentList(params: {
  status?: number
  pageNum: number
  pageSize: number
}) {
  return request.get('/admin/comments', { params })
}

// 审核评论
export function auditComment(id: number, status: number) {
  return request.put(`/admin/comments/${id}/audit`, null, { params: { status } })
}

// 删除评论
export function deleteComment(id: number) {
  return request.delete(`/admin/comments/${id}`)
}