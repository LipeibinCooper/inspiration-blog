import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { Inspiration, Comment } from "@/types/inspiration";
import type { InspirationNode } from "@/types/tree";
import { useAuthStore } from "./useAuthStore";
import { useInspirationTreeStore } from "./useInspirationTreeStore";
// import { api } from "@/api";

export const useInspirationStore = defineStore("inspiration", () => {
  const authStore = useAuthStore();
  const treeStore = useInspirationTreeStore();
  const inspirations = ref<Inspiration[]>([
    {
      id: 1,
      title: "Vue 3 + TypeScript 开发体验",
      content: "使用 Vue 3 和 TypeScript 进行开发，类型提示和代码补全非常方便...",
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      userId: 1,
      isPublic: true,
      likes: 5,
      comments: 2,
      collections: 3,
      author: {
        id: 1,
        name: "admin",
        avatar: undefined
      }
    },
    // 可以添加更多初始数据...
  ]);

  // 添加评论存储的初始数据
  const allComments = ref<Comment[]>([
    {
      id: 1,
      content: "这是一条测试评论",
      createdAt: new Date().toISOString(),
      userId: 1,
      username: "admin",
      inspirationId: 1
    },
    {
      id: 2,
      content: "Vue 3 确实很棒！",
      createdAt: new Date().toISOString(),
      userId: 2,
      username: "test",
      inspirationId: 1
    }
  ]);

  // 当前用户的灵感笔记（只在左侧列表显示）
  const myInspirations = computed(() =>
    inspirations.value.filter(note => note.userId === authStore.userInfo?.id)
  );

  // 所有公开的灵感笔记（包括自己的）
  const recommendedInspirations = computed(() =>
    inspirations.value.filter(note => note.isPublic)
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  );

  const activeNoteId = ref<number | null>(null);

  // 创建新灵感
  const addInspiration = async (data: {
    title: string;
    content: string;
    isPublic: boolean;
  }) => {
    const currentUser = authStore.userInfo;
    if (!currentUser) throw new Error('请先登录');

    const newInspiration: Inspiration = {
      id: Date.now(),
      title: data.title,
      content: data.content,
      isPublic: data.isPublic,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      userId: currentUser.id,
      likes: 0,
      comments: 0,
      collections: 0,
      author: {
        id: currentUser.id,
        name: currentUser.username,
        avatar: currentUser.avatar || ''
      }
    };

    // 添加到灵感列表
    inspirations.value.unshift(newInspiration);

    // 创建树节点
    const treeNode: InspirationNode = {
      id: newInspiration.id,
      title: newInspiration.title,
      content: newInspiration.content,
      children: [],
      parentId: null,
      createdAt: newInspiration.createdAt,
      updatedAt: newInspiration.updatedAt
    };

    // 初始化树形结构
    treeStore.initTree(treeNode);

    return newInspiration.id;
  };

  // 获取灵感列表
  const getInspirations = () => {
    // 更新所有灵感中的作者信息
    return inspirations.value.map(inspiration => {
      const author = authStore.getUserById(inspiration.userId);
      if (author) {
        return {
          ...inspiration,
          author: {
            id: author.id,
            name: author.username,
            avatar: author.avatar || ''
          }
        };
      }
      return inspiration;
    });
  };

  // 设置当前选中的灵感
  const setActiveNoteId = (id: number) => {
    activeNoteId.value = id;
  };

  // 更新灵感的互动计数
  const updateInspirationStats = (inspirationId: number, type: 'likes' | 'collections' | 'comments', isAdd: boolean) => {
    const inspiration = inspirations.value.find(note => note.id === inspirationId);
    if (inspiration) {
      inspiration[type] = inspiration[type] + (isAdd ? 1 : -1);
    }
  };

  // 添加评论
  const addComment = (comment: Comment) => {
    console.log('Adding comment:', comment);
    allComments.value.unshift(comment);
    updateInspirationStats(comment.inspirationId, 'comments', true);
  };

  // 获取灵感的评论
  const getInspirationComments = (inspirationId: number) => {
    console.log('Getting comments for inspiration:', inspirationId);
    const comments = allComments.value.filter(comment => comment.inspirationId === inspirationId);
    console.log('Found comments:', comments);
    return comments;
  };

  // 获取用户的所有评论
  const getUserComments = (userId: number) => {
    return allComments.value.filter(comment => comment.userId === userId);
  };

  // 添加更新灵感的方法
  const updateInspiration = (id: number, updates: Partial<Inspiration>) => {
    const inspiration = inspirations.value.find(note => note.id === id);
    if (inspiration) {
      Object.assign(inspiration, updates);
    }
  };

  // 删除评论
  const deleteComment = (commentId: number) => {
    const index = allComments.value.findIndex(comment => comment.id === commentId);
    if (index !== -1) {
      const comment = allComments.value[index];
      allComments.value.splice(index, 1);
      // 更新评论计数
      updateInspirationStats(comment.inspirationId, 'comments', false);
    }
  };

  // 删除灵感树
  const deleteInspiration = (id: number) => {
    // 删除灵感
    const index = inspirations.value.findIndex(note => note.id === id);
    if (index !== -1) {
      inspirations.value.splice(index, 1);
    }
    
    // 删除相关评论
    allComments.value = allComments.value.filter(
      comment => comment.inspirationId !== id
    );
    
    // 删除相关的树节点数据
    treeStore.deleteTree(id);
  };

  return {
    inspirations,
    myInspirations,
    recommendedInspirations,
    activeNoteId,
    createInspiration: addInspiration,
    getInspirations,
    setActiveNoteId,
    allComments,
    updateInspirationStats,
    addComment,
    getInspirationComments,
    getUserComments,
    updateInspiration,
    deleteComment,
    deleteInspiration,
  };
}, {
  persist: true // 简化持久化配置
});
