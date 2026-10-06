import request from '@/utils/request'

// 获取标签列表
export function getTagList() {
  return request.get('/web/tags')
}