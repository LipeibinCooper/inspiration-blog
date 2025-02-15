// 灵感笔记的类型定义
export interface Inspiration {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
  userId: number;
}

// 创建灵感的请求参数类型
export interface CreateInspirationDto {
  title: string;
  content: string;
}

// 用户相关类型
export interface User {
  id: number;
  username: string;
  email: string;
}

// 登录表单类型
export interface LoginForm {
  username: string;
  password: string;
}
