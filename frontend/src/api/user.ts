import request from '@/utils/request';
import type { UserDTO } from '@/types/inspiration';

export const getUserProfile = (id: number) => {
  return request.get(`/users/${id}`);
};

export const updateProfile = (data: Partial<UserDTO>) => {
  return request.put('/users/profile', data);
};

export const updateAvatar = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/users/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const updateBio = (bio: string) => {
  return request.put('/users/bio', { bio });
};