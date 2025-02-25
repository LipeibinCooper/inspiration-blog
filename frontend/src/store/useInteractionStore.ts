import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { Inspiration, UserInteraction, Comment } from "@/types/inspiration";
import { useAuthStore } from "./useAuthStore";
import { useInspirationStore } from "./useInspirationStore";
import * as interactionApi from "@/api/interaction";
import * as commentApi from "@/api/comment";
import { ElMessage } from "element-plus";

export const useInteractionStore = defineStore("interaction", () => {
  const authStore = useAuthStore();
  const inspirationStore = useInspirationStore();

  const loading = ref(false);
  const likedInspirations = ref<number[]>([]);
  const collectedInspirations = ref<number[]>([]);
  const comments = ref<Comment[]>([]);

  // 检查互动状态
  const hasLiked = (inspirationId: number) => likedInspirations.value.includes(inspirationId);
  const hasCollected = (inspirationId: number) => collectedInspirations.value.includes(inspirationId);

  // 获取用户的所有互动记录
  const getUserInteractions = async () => {
    if (!authStore.userInfo?.id) return;
    
    loading.value = true;
    try {
      const res = await interactionApi.getUserInteractions();
      const interactions = res.data.data as UserInteraction[];
      
      // 更新点赞和收藏状态
      likedInspirations.value = interactions
        .filter((item: UserInteraction) => item.type === 'LIKE')
        .map((item: UserInteraction) => item.inspirationId);
        
      collectedInspirations.value = interactions
        .filter((item: UserInteraction) => item.type === 'COLLECTION')
        .map((item: UserInteraction) => item.inspirationId);

      comments.value = res.data.comments;
    } catch (error) {
      console.error('获取用户互动记录失败:', error);
      ElMessage.error('获取互动记录失败');
    } finally {
      loading.value = false;
    }
  };

  // 点赞/取消点赞
  const toggleLike = async (inspirationId: number) => {
    try {
      await interactionApi.toggleLike(inspirationId);
      const index = likedInspirations.value.indexOf(inspirationId);
      
      if (index === -1) {
        likedInspirations.value.push(inspirationId);
        await inspirationStore.updateInspirationStats(inspirationId, 'likes', true);
      } else {
        likedInspirations.value.splice(index, 1);
        await inspirationStore.updateInspirationStats(inspirationId, 'likes', false);
      }
    } catch (error) {
      ElMessage.error('操作失败');
      throw error;
    }
  };

  // 收藏/取消收藏
  const toggleCollection = async (inspirationId: number) => {
    try {
      await interactionApi.toggleCollection(inspirationId);
      const index = collectedInspirations.value.indexOf(inspirationId);
      
      if (index === -1) {
        collectedInspirations.value.push(inspirationId);
        await inspirationStore.updateInspirationStats(inspirationId, 'collections', true);
      } else {
        collectedInspirations.value.splice(index, 1);
        await inspirationStore.updateInspirationStats(inspirationId, 'collections', false);
      }
    } catch (error) {
      ElMessage.error('操作失败');
      throw error;
    }
  };

  // 添加评论
  const addComment = async (inspirationId: number, content: string) => {
    try {
      const res = await commentApi.createComment({ inspirationId, content });
      comments.value.unshift(res.data);
      await inspirationStore.updateInspirationStats(inspirationId, 'comments', true);
      ElMessage.success('评论成功');
      return res.data;
    } catch (error) {
      ElMessage.error('评论失败');
      throw error;
    }
  };

  // 删除评论
  const deleteComment = async (id: number, inspirationId: number) => {
    try {
      await commentApi.deleteComment(id);
      comments.value = comments.value.filter(comment => comment.id !== id);
      await inspirationStore.updateInspirationStats(inspirationId, 'comments', false);
      ElMessage.success('删除成功');
    } catch (error) {
      ElMessage.error('删除失败');
      throw error;
    }
  };

  // 计算属性
  const userComments = computed(() => comments.value);
  const likedInspirationList = computed(() => 
    inspirationStore.inspirations.filter(item => 
      likedInspirations.value.includes(item.id)
    )
  );
  const collectedInspirationList = computed(() => 
    inspirationStore.inspirations.filter(item => 
      collectedInspirations.value.includes(item.id)
    )
  );

  return {
    loading,
    hasLiked,
    hasCollected,
    userComments,
    likedInspirationList,
    collectedInspirationList,
    getUserInteractions,
    toggleLike,
    toggleCollection,
    addComment,
    deleteComment
  };
}, {
  persist: true
}); 