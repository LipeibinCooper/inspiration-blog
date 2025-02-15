import { User } from "../types/inspiration";

// 保存 token
export const setToken = (token: string): void => {
  localStorage.setItem("token", token);
};

// 获取 token
export const getToken = (): string | null => {
  return localStorage.getItem("token");
};

// 移除 token
export const removeToken = (): void => {
  localStorage.removeItem("token");
};

// 保存用户信息
export const setUser = (user: User): void => {
  localStorage.setItem("user", JSON.stringify(user));
};

// 获取用户信息
export const getUser = (): User | null => {
  const userStr = localStorage.getItem("user");
  return userStr ? JSON.parse(userStr) : null;
};

// 移除用户信息
export const removeUser = (): void => {
  localStorage.removeItem("user");
};
