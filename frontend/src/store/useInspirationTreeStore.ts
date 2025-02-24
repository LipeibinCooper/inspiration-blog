import { defineStore } from "pinia";
import { ref } from "vue";
import type { InspirationNode } from "@/types/inspiration-tree";

export const useInspirationTreeStore = defineStore("inspirationTree", () => {
  // 存储所有灵感树的数据
  const trees = ref<Record<number, InspirationNode[][]>>({});

  // 检查是否已存在树
  const hasTree = (inspirationId: number): boolean => {
    return !!trees.value[inspirationId];
  };

  // 初始化树
  const initTree = (rootNode: InspirationNode) => {
    if (!trees.value[rootNode.id]) {
      trees.value[rootNode.id] = [[rootNode]];
    }
  };

  // 获取树节点
  const getTreeNodes = (inspirationId: number) => {
    return trees.value[inspirationId] || [[/* 空数组作为默认值 */]];
  };

  // 添加节点
  const addNode = (inspirationId: number, node: InspirationNode, arrayIndex: number, position: 'right' | 'down') => {
    const treeNodes = trees.value[inspirationId];
    if (!treeNodes) return;

    if (position === 'right') {
      treeNodes[arrayIndex].push(node);
    } else {
      if (!treeNodes[arrayIndex + 1]) {
        treeNodes[arrayIndex + 1] = [];
      }
      treeNodes[arrayIndex + 1].push(node);
    }
  };

  // 更新节点
  const updateNode = (inspirationId: number, nodeId: number, updates: Partial<InspirationNode>) => {
    const treeNodes = trees.value[inspirationId];
    if (!treeNodes) return;

    treeNodes.forEach(array => {
      array.forEach(node => {
        if (node.id === nodeId) {
          Object.assign(node, updates);
        }
      });
    });
  };

  // 删除节点
  const deleteNode = (inspirationId: number, arrayIndex: number, itemIndex: number) => {
    const treeNodes = trees.value[inspirationId];
    if (!treeNodes) return;

    treeNodes[arrayIndex].splice(itemIndex, 1);
    if (treeNodes[arrayIndex].length === 0 && arrayIndex > 0) {
      treeNodes.splice(arrayIndex, 1);
    }
  };

  // 删除整棵树
  const deleteTree = (inspirationId: number) => {
    delete trees.value[inspirationId];
  };

  return {
    trees,
    hasTree,
    initTree,
    getTreeNodes,
    addNode,
    updateNode,
    deleteNode,
    deleteTree,
  };
}, {
  persist: true // 启用持久化
}); 