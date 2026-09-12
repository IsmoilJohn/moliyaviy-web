import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { apiClient } from '@/api/client'
import { TOKEN_KEY, USER_KEY } from '@/lib/storage'
import type { LoginResponse, User } from '@/types/models'

function readStoredUser(): User | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as User
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const user = ref<User | null>(readStoredUser())
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  async function login(email: string, password: string) {
    loading.value = true
    error.value = null
    try {
      const { data } = await apiClient.post<LoginResponse>('/auth/login', { email, password })
      token.value = data.token
      user.value = data.user
      localStorage.setItem(TOKEN_KEY, data.token)
      localStorage.setItem(USER_KEY, JSON.stringify(data.user))
    } catch (e) {
      if (axios.isAxiosError(e) && e.response?.data?.message) {
        error.value = e.response.data.message
      } else {
        error.value = 'Не удалось войти. Проверьте подключение к серверу.'
      }
      throw e
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, user, loading, error, isAuthenticated, login, logout }
})
