import request from './request';
import type { InspirationNode } from '@/types/inspiration-tree';

export const createNode = (data: Partial<InspirationNode>) => {
  return request.post('/nodes', data);
};

export const getNode = (id: number) => {
  return request.get(`/nodes/${id}`);
};

export const getInspirationNodes = (inspirationId: number) => {
  return request.get(`/nodes/inspiration/${inspirationId}`);
};

export const updateNode = (id: number, data: Partial<InspirationNode>) => {
  return request.put(`/nodes/${id}`, data);
};

export const deleteNode = (id: number) => {
  return request.delete(`/nodes/${id}`);
};

export const uploadNodeImage = (nodeId: number, file: File, isThumbnail = false) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('isThumbnail', String(isThumbnail));
  return request.post(`/nodes/${nodeId}/images`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const deleteNodeImage = (nodeId: number, imageId: number) => {
  return request.delete(`/nodes/${nodeId}/images/${imageId}`);
};

export const setThumbnail = (nodeId: number, imageId: number) => {
  return request.put(`/nodes/${nodeId}/images/${imageId}/thumbnail`);
}; 