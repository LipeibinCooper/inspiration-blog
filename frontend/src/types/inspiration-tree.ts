// 灵感树节点类型定义
export interface InspirationNode {
  id: number;
  parentId: number | null;  // 父节点ID，根节点为null
  content: string;         // 节点内容
  createdAt: string;
  updatedAt: string;
  children?: InspirationNode[];  // 子节点
}

// 创建新节点的DTO
export interface CreateNodeDto {
  parentId: number | null;
  content: string;
}

// 更新节点的DTO
export interface UpdateNodeDto {
  id: number;
  content: string;
}