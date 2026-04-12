import http from './http'

export const loginApi = (payload) => http.post('/auth/login', payload)

export const registerApi = (payload) => http.post('/auth/register', payload)

export const ssoLoginApi = (payload) => http.post('/auth/sso/login', payload)

export const getCurrentUserApi = () => http.get('/auth/me')

export const refreshTokenApi = (payload) => http.post('/auth/refresh-token', payload)

export const logoutApi = () => http.post('/auth/logout')
