import request from '@/utils/request'

// 获取文章列表
export function getArticleList(params: {
  categoryId?: number
  keyword?: string
  pageNum: number
  pageSize: number
}) {
  return request.get('/web/articles', { params })
}

// 获取随心记文章列表
export function getMomentsArticles(params: {
  pageNum: number
  pageSize: number
}) {
  return request.get('/web/articles/moments', { params })
}

// 获取文章详情
export function getArticle(id: number) {
  return request.get(`/web/articles/${id}`)
}

// 获取热门文章
export function getHotArticles(limit: number = 10) {
  return request.get('/web/articles/hot', { params: { limit } })
}

// 获取归档列表
export function getArchiveList() {
  return request.get('/web/articles/archive')
}
