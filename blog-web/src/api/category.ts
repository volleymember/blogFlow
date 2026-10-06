import request from '@/utils/request'

// 获取分类列表
export function getCategoryList() {
  return request.get('/web/categories')
}

// 获取分类下的文章
export function getCategoryArticles(slug: string, params: {
  pageNum: number
  pageSize: number
}) {
  return request.get(`/web/categories/${slug}/articles`, { params })
}