import request from "@/utils/request";
import type { Inspiration, CreateInspirationDto } from "@/types/inspiration";

// API 响应类型
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// API 返回的灵感类型
interface ApiInspiration {
  id: number;
  title: string;
  content: string;
  createdAt: string;
}

// 获取灵感列表
export const getInspirations = (page?: number, pageSize?: number) => {
  return request.get(page && pageSize ? `/inspirations?page=${page}&pageSize=${pageSize}` : '/inspirations');
};

// 获取单个灵感详情
export const getInspirationById = (id: number) => {
  return request.get(`/inspirations/${id}`);
};

// 创建灵感
export const createInspiration = (data: CreateInspirationDto) => {
  return request.post<ApiResponse<Inspiration>>('/inspirations/create', data);
};

// 删除灵感
export const deleteInspiration = (id: number) => {
  return request.delete<ApiResponse<void>>(`/inspirations/${id}`);
};

// 更新灵感
export const updateInspiration = (id: number, data: Partial<Inspiration>) => {
  return request.put<ApiResponse<void>>(`/inspirations/${id}`, data);
};

// 点赞/取消点赞
export const toggleLike = (id: number) => {
  return request.post<ApiResponse<void>>(`/inspirations/${id}/like`);
};

// 收藏/取消收藏
export const toggleCollection = (id: number) => {
  return request.post<ApiResponse<void>>(`/inspirations/${id}/collect`);
};
