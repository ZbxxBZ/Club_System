import axios from 'axios'
import { clearAccessToken, getAccessToken } from '../utils/token'
import router from '../router'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

http.interceptors.request.use(
  (config) => {
    const token = getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

http.interceptors.response.use(
  (response) => {
    const data = response.data
    // 检查业务状态码，如果不是 0 则视为错误
    if (data && typeof data.code !== 'undefined' && data.code !== 0) {
      const error = new Error(data.message || '请求失败')
      error.response = response
      return Promise.reject(error)
    }
    return data
  },
  (error) => {
    if (error?.response?.status === 401) {
      clearAccessToken()
      const currentPath = router.currentRoute.value.fullPath
      if (router.currentRoute.value.path !== '/login') {
        router.replace({
          path: '/login',
          query: {
            redirect: currentPath,
          },
        })
      }
    }
    return Promise.reject(error)
  },
)

export default http
