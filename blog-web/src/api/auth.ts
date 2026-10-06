import request from '@/utils/request'

export interface AuthPayload {
  username: string
  password: string
  nickname?: string
  email?: string
}

export interface AuthUser {
  token: string
  username: string
  nickname: string
  role: string
}

export function login(data: Pick<AuthPayload, 'username' | 'password'>) {
  return request.post<AuthUser>('/auth/login', data)
}

export function register(data: AuthPayload) {
  return request.post<AuthUser>('/auth/register', data)
}
