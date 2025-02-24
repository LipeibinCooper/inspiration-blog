// 作者类型定义
export interface Author {
  id: number;
  name: string;
  avatar?: string;  // 将 avatar 改为可选字段
}

// 灵感笔记的类型定义
export interface Inspiration {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
  userId: number;
  isPublic: boolean;
  likes: number;
  comments: number;
  collections: number;
  author: Author;
  // 当前用户的互动状态
  isLiked?: boolean;
  isCollected?: boolean;
}

// 评论类型定义
export interface Comment {
  id: number;
  content: string;
  createdAt: string;
  userId: number;
  username: string;
  inspirationId: number;
  inspirationTitle: string;
}

// 用户互动记录类型
export interface UserInteraction {
  likes: number[];      // 点赞的灵感 ID 列表
  collections: number[]; // 收藏的灵感 ID 列表
  comments: Comment[];   // 评论列表
}

// 创建灵感的请求参数类型
export interface CreateInspirationDto {
  title: string;
  content: string;
  isPublic: boolean;
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

// 注册表单类型
export interface RegisterForm {
  username: string;
  password: string;
}

// 灵感树节点类型
export interface InspirationNode {
  id: number;
  title: string;
  content: string;
  children: InspirationNode[];
}
