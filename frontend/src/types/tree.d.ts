// 灵感树节点类型定义
export interface InspirationNode {
  id: number;
  title: string;
  content: string;
  children: InspirationNode[];
  parentId: number | null;  // 改为必需属性，移除可选标记 ?
  createdAt: string;        // 添加创建时间
  updatedAt: string;        // 添加更新时间
} 