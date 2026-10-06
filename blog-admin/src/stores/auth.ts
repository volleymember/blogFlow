import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<{
    username: string
    nickname: string
    role: string
  } | null>(null)

  // 登录
  async function login(username: string, password: string) {
    const res = await loginApi({ username, password })
    token.value = res.data.token
    userInfo.value = {
      username: res.data.username,
      nickname: res.data.nickname,
      role: res.data.role
    }
    localStorage.setItem('token', res.data.token)
    return res
  }

  // 登出
  async function logout() {
    await logoutApi()
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  // 获取用户信息
  function getUserInfo() {
    return userInfo.value
  }

  return {
    token,
    userInfo,
    login,
    logout,
    getUserInfo
  }
})