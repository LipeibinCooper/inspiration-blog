// 用户相关类型
export interface User {
  id: number;
  username: string;
  email: string;
  avatar?: string;
  token?: string;
  bio?: string;
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

// 修改原有的 Inspiration 接口
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
}

// 修改用户互动记录
export interface UserInteraction {
  likes: number[];           // 点赞的灵感 ID
  eventLikes: number[];      // 点赞的事件 ID
  collections: number[];
  comments: Comment[];
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