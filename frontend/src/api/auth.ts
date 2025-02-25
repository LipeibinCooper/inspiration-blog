import request from './request';
import type { LoginForm, RegisterForm } from '@/types/inspiration';

export const login = (data: LoginForm) => {
  return request.post('/auth/login', data);
};

export const register = (data: RegisterForm) => {
  return request.post('/auth/register', data);
};

export const getCurrentUser = () => {
  return request.get('/auth/me');
}; 