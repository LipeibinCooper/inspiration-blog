import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { Inspiration, CreateInspirationDto, Comment } from "@/types/inspiration";
import { useAuthStore } from "./useAuthStore";

interface InspirationState {
  allInspirations: Inspiration[];
  allComments: Comment[];
}

export const useInspirationStore = defineStore("inspiration", () => {
  const authStore = useAuthStore();
  const allInspirations = ref<Inspiration[]>([
    {
      id: 1,
      title: "Vue 3 组合式 API",
      content: "Vue 3 的组合式 API 提供了更好的代码组织方式...",
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      userId: 1, // admin 的笔记
      isPublic: true,
      likes: 0,
      comments: 0,
      collections: 0
    },
    {
      id: 2,
      title: "TypeScript 类型体操",
      content: "TypeScript 的类型系统非常强大...",
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      userId: 2, // test 的笔记
      isPublic: true,
      likes: 0,
      comments: 0,
      collections: 0
    }
  ]);

  // 添加评论存储
  const allComments = ref<Comment[]>([]);

  // 当前用户的灵感笔记（只在左侧列表显示）
  const myInspirations = computed(() => 
    allInspirations.value.filter(note => note.userId === authStore.userInfo?.id)
  );

  // 所有公开的灵感笔记（包括自己的）
  const recommendedInspirations = computed(() => 
    allInspirations.value.filter(note => note.isPublic)
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  );

  const activeNoteId = ref<number | null>(null);

  // 添加新灵感
  const addInspiration = (data: CreateInspirationDto) => {
    const newInspiration: Inspiration = {
      id: Date.now(),
      ...data,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      userId: authStore.userInfo?.id || 1,
      likes: 0,
      comments: 0,
      collections: 0
    };
    
    allInspirations.value.unshift(newInspiration);
  };

  // 设置当前选中的灵感
  const setActiveNoteId = (id: number) => {
    activeNoteId.value = id;
  };

  // 更新灵感的互动计数
  const updateInspirationStats = (inspirationId: number, type: 'likes' | 'collections' | 'comments', isAdd: boolean) => {
    const inspiration = allInspirations.value.find(note => note.id === inspirationId);
    if (inspiration) {
      inspiration[type] = inspiration[type] + (isAdd ? 1 : -1);
    }
  };

  // 添加评论
  const addComment = (comment: Comment) => {
    allComments.value.unshift(comment);
    updateInspirationStats(comment.inspirationId, 'comments', true);
  };

  // 获取灵感的评论
  const getInspirationComments = (inspirationId: number) => {
    return allComments.value.filter(comment => comment.inspirationId === inspirationId);
  };

  return {
    myInspirations,
    recommendedInspirations,
    activeNoteId,
    addInspiration,
    setActiveNoteId,
    allInspirations,
    updateInspirationStats,
    addComment,
    getInspirationComments
  };
}, {
  persist: true // 简化持久化配置
});
