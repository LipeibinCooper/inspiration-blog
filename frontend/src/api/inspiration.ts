import request from "@/utils/request";
import type { Inspiration, CreateInspirationDto } from "@/types/inspiration";

// API 返回的灵感类型
interface ApiInspiration {
  id: number;
  title: string;
  content: string;
  createdAt: string;
}

// 获取灵感列表
export const getInspirations = () => {
  return request.get<ApiInspiration[]>("/inspirations");
};

// 获取单个灵感详情
export const getInspirationById = (id: number): Promise<ApiInspiration> => {
  return request.get(`/inspirations/${id}`);
};

// 创建灵感
export const createInspiration = (data: CreateInspirationDto) => {
  return request.post<ApiInspiration>("/inspirations", data);
};

// 删除灵感
export const deleteInspiration = (id: number): Promise<void> => {
  return request.delete(`/inspirations/${id}`);
};
