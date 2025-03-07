import request from '@/utils/request';
import type { Comment } from '@/types/inspiration';

export const createComment = (data: { inspirationId: number; content: string }) => {
  return request.post('/comments', data);
};

export const getInspirationComments = (inspirationId: number) => {
  return request.get(`/comments/inspiration/${inspirationId}`);
};

export const getUserComments = (userId: number) => {
  return request.get(`/comments/user/${userId}`);
};

export const deleteComment = (id: number) => {
  return request.delete(`/comments/${id}`);
};