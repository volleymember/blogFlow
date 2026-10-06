import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/BlogLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        // meta: { title: '首页' }
      },
      {
        path: 'article/:id',
        name: 'Article',
        component: () => import('@/views/article/index.vue'),
        // meta: { title: '文章详情' }
      },
      {
        path: 'category/:slug',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        // meta: { title: '分类' }
      },
      {
        path: 'archive',
        name: 'Archive',
        component: () => import('@/views/archive/index.vue'),
        // meta: { title: '归档' }
      },
      {
        path: 'moments',
        name: 'Moments',
        component: () => import('@/views/moments/index.vue'),
        // meta: { title: '随心记' }
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/about/index.vue'),
        // meta: { title: '关于' }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/search/index.vue'),
        // meta: { title: '搜索' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach((to, from, next) => {
  const title = to.meta.title as string
  document.title = title ? `${title} - Capybara` : 'Capybara'
  next()
})

export default router
