// 用户相关类型
export interface User {
  id: number;
  username: string;
  email?: string;
  avatar?: string;
  bio?: string;
  token?: string;
}

export interface UserDTO {
  id: number;
  username: string;
  email?: string;
  avatar?: string;
  bio?: string;
}

// API 返回的灵感类型
export interface ApiInspiration {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt?: string;  // 可选，因为可能需要使用 createdAt 作为默认值
  userId?: number;      // 可选，因为可能有默认值 0
  isPublic?: boolean;   // 可选，因为可能有默认值 false
  likes?: number;       // 可选，因为可能有默认值 0
  comments?: number;    // 可选，因为可能有默认值 0
  collections?: number; // 可选，因为可能有默认值 0
  author?: {           // 可选，因为可能有默认值
    id: number;
    name: string;
    avatar: string;
  };
}

// 扩展 Inspiration 接口，确保所有字段都是必需的
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
  author: {
    id: number;
    name: string;
    avatar: string;
  };
  isLiked?: boolean;
  isCollected?: boolean;
}

// 用户互动类型
export interface UserInteraction {
  id: number;
  userId: number;
  inspirationId: number;
  type: 'LIKE' | 'COLLECTION';
  createdAt: string;
}

export interface LoginForm {
  username: string;
  password: string;
}

export interface RegisterForm extends LoginForm {
  confirmPassword: string;
}

// 添加密码更新表单类型
export interface PasswordUpdateForm {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

// 创建灵感的 DTO
export interface CreateInspirationDto {
  title: string;
  content: string;
  isPublic: boolean;
}

// 修改评论接口
export interface Comment {
  id: number;
  content: string;
  createdAt: string;
  userId: number;
  username: string;
  avatar?: string;
  inspirationId: number;
  inspirationTitle: string;
}

interface Author {
  id: number;
  name: string;
  avatar: string;
}

// API 响应类型
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  comments?: Comment[];  // 添加可选的评论字段
} 