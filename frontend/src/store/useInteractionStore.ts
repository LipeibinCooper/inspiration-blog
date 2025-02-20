import { defineStore } from "pinia";
import { ref, computed, watch } from "vue";
import type { Comment, UserInteraction, Inspiration } from "@/types/inspiration";
import { useAuthStore } from "./useAuthStore";
import { useInspirationStore } from "./useInspirationStore";

export const useInteractionStore = defineStore("interaction", () => {
  const authStore = useAuthStore();
  const inspirationStore = useInspirationStore();

  // 用户互动记录
  const userInteractions = ref<Record<number, UserInteraction>>({});

  // 获取当前用户的互动记录
  const currentUserInteraction = ref<UserInteraction | null>(null);

  // 添加 watcher 来监听用户变化
  watch(() => authStore.userInfo?.id, (newUserId) => {
    if (newUserId) {
      // 如果用户互动记录不存在，则初始化
      if (!userInteractions.value[newUserId]) {
        userInteractions.value[newUserId] = {
          likes: [],
          eventLikes: [],
          collections: [],
          comments: []
        };
      }
      currentUserInteraction.value = userInteractions.value[newUserId];
    } else {
      currentUserInteraction.value = null;
    }
  }, { immediate: true });

  // 点赞灵感
  const toggleLike = (inspirationId: number) => {
    if (!currentUserInteraction.value) return;
    
    const index = currentUserInteraction.value.likes.indexOf(inspirationId);
    const isAdd = index === -1;
    
    if (isAdd) {
      currentUserInteraction.value.likes.push(inspirationId);
    } else {
      currentUserInteraction.value.likes.splice(index, 1);
    }

    inspirationStore.updateInspirationStats(inspirationId, 'likes', isAdd);
  };

  // 收藏灵感
  const toggleCollection = (inspirationId: number) => {
    if (!currentUserInteraction.value) return;
    
    const index = currentUserInteraction.value.collections.indexOf(inspirationId);
    const isAdd = index === -1;
    
    if (isAdd) {
      currentUserInteraction.value.collections.push(inspirationId);
    } else {
      currentUserInteraction.value.collections.splice(index, 1);
    }

    inspirationStore.updateInspirationStats(inspirationId, 'collections', isAdd);
  };

  // 添加评论
  const addComment = async (inspirationId: number, content: string) => {
    const authUser = authStore.userInfo;
    if (!authUser) throw new Error('请先登录');

    const newComment: Comment = {
      id: Date.now(),
      content,
      createdAt: new Date().toISOString(),
      userId: authUser.id,
      username: authUser.username,
      inspirationId
    };

    // 添加到 InspirationStore 的评论列表中
    inspirationStore.addComment(newComment);

    // 添加到当前用户的评论列表中
    currentUserInteraction.value?.comments.push(newComment);

    return newComment;
  };

  // 获取用户点赞的灵感
  const likedInspirations = computed(() =>
    currentUserInteraction.value
      ? inspirationStore.getInspirations().filter(
          (note: Inspiration) => currentUserInteraction.value?.likes.includes(note.id)
        )
      : []
  );

  // 获取用户收藏的灵感
  const collectedInspirations = computed(() =>
    currentUserInteraction.value
      ? inspirationStore.getInspirations().filter(
          (note: Inspiration) => currentUserInteraction.value?.collections.includes(note.id)
        )
      : []
  );

  // 获取用户的评论
  const userComments = computed(() => 
    currentUserInteraction.value?.comments || []
  );

  return {
    toggleLike,
    toggleCollection,
    addComment,
    likedInspirations,
    collectedInspirations,
    userComments,
    currentUserInteraction,
    userInteractions
  };
}, {
  persist: true
}); 