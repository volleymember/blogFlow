import request from '@/utils/request'

// 获取文章列表
export function getArticleList(params: {
  categoryId?: number
  status?: number
  visibility?: number
  keyword?: string
  pageNum: number
  pageSize: number
}) {
  return request.get('/admin/articles', { params })
}

// 获取文章详情
export function getArticle(id: number) {
  return request.get(`/admin/articles/${id}`)
}

// 创建文章
export function createArticle(data: any) {
  return request.post('/admin/articles', data)
}

// 更新文章
export function updateArticle(id: number, data: any) {
  return request.put(`/admin/articles/${id}`, data)
}

// 删除文章
export function deleteArticle(id: number) {
  return request.delete(`/admin/articles/${id}`)
}

// 获取热门文章
export function getHotArticles(limit: number = 10) {
  return request.get('/web/articles/hot', { params: { limit } })
}
