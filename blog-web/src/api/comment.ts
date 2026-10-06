import request from '@/utils/request'

// 获取文章评论
export function getComments(articleId: number) {
  return request.get(`/web/comments/article/${articleId}`)
}

// 提交评论
export function submitComment(data: {
  articleId: number
  parentId?: number
  replyToId?: number
  nickname: string
  email?: string
  website?: string
  content: string
}) {
  return request.post('/web/comments', data)
}