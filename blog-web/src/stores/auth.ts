import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, type AuthPayload, type AuthUser } from '@/api/auth'

const TOKEN_KEY = 'blog_web_token'
const USER_KEY = 'blog_web_user'

export const useAuthStore = defineStore('webAuth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref<AuthUser | null>(loadUser())

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

  async function login(username: string, password: string) {
    const res = await loginApi({ username, password })
    setSession(res.data)
    return res
  }

  async function register(payload: AuthPayload) {
    const res = await registerApi(payload)
    setSession(res.data)
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  function setSession(user: AuthUser) {
    token.value = user.token
    userInfo.value = user
    localStorage.setItem(TOKEN_KEY, user.token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }

  function loadUser() {
    try {
      const raw = localStorage.getItem(USER_KEY)
      return raw ? JSON.parse(raw) as AuthUser : null
    } catch (error) {
      localStorage.removeItem(USER_KEY)
      return null
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout
  }
})
