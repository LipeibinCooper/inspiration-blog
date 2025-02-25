// 灵感树节点类型定义
export interface InspirationNode {
  id: number;
  inspirationId: number;
  parentId: number | null;  // 父节点ID，根节点为null
  title?: string;
  content: string;         // 节点内容
  imageUrls?: string[];    // 改为数组存储多张图片
  thumbnailUrl?: string;   // 图片缩略图URL
  createdAt: string;
  updatedAt: string;
  children?: InspirationNode[];  // 子节点
}

// 创建新节点的DTO
export interface CreateNodeDto {
  parentId: number | null;
  content: string;
  imageUrl?: string;
  thumbnailUrl?: string;
}

// 更新节点的DTO
export interface UpdateNodeDto {
  id: number;
  content: string;
  imageUrl?: string;
  thumbnailUrl?: string;
}