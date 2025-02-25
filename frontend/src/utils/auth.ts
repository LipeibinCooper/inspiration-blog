import { User } from "../types/inspiration";

const TOKEN_KEY = 'token';
const USER_KEY = 'user';

// 保存 token
export const setToken = (token: string): void => {
  localStorage.setItem(TOKEN_KEY, token);
};

// 获取 token
export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY);
};

// 移除 token
export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY);
};

// 保存用户信息
export const setUser = (user: User): void => {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

// 获取用户信息
export const getUser = (): User | null => {
  const userStr = localStorage.getItem(USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
};

// 移除用户信息
export const removeUser = (): void => {
  localStorage.removeItem(USER_KEY);
};
