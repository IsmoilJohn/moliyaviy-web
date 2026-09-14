import axios from 'axios'
import { TOKEN_KEY, USER_KEY } from '@/lib/storage'

// In local dev, '/api' is proxied to the backend by Vite (see vite.config.ts).
// In a static production build, VITE_API_URL is inlined at build time and
// points at the deployed backend's own origin (e.g. Railway service URL) -
// see frontend/Dockerfile.
const baseURL = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api` : '/api'

export const apiClient = axios.create({
  baseURL,
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`)
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)
