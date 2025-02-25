import request from './request';
import type { UserInteraction } from '@/types/inspiration';

export const toggleLike = (inspirationId: number) => {
  return request.post(`/inspirations/${inspirationId}/like`);
};

export const toggleCollection = (inspirationId: number) => {
  return request.post(`/inspirations/${inspirationId}/collect`);
};

export const getUserInteractions = () => {
  return request.get('/user/interactions');
}; 