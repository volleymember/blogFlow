import request from '@/utils/request'

// 获取仪表盘统计
export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}

// 获取网站配置
export function getSiteConfig() {
  return request.get('/admin/config')
}

// 更新网站配置
export function updateSiteConfig(data: Record<string, string>) {
  return request.put('/admin/config', data)
}