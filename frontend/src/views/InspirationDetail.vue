<template>
  <div class="inspiration-detail">
    <div class="detail-header">
      <el-button @click="router.back()" :icon="ArrowLeft">返回</el-button>
      <h2>{{ inspiration?.title }}</h2>
    </div>
    
    <div class="detail-content">
      <p>{{ inspiration?.content }}</p>
    </div>

    <div class="detail-footer">
      <div class="time-info">
        创建时间: {{ formatTime(inspiration?.createdAt) }}
        <template v-if="inspiration?.updatedAt">
          | 更新时间: {{ formatTime(inspiration?.updatedAt) }}
        </template>
      </div>

      <!-- 互动按钮区 -->
      <div class="interaction-buttons">
        <el-button 
          :type="isLiked ? 'primary' : 'default'"
          @click="handleLike"
        >
          <el-icon><Pointer /></el-icon>
          点赞 {{ inspiration?.likes || 0 }}
        </el-button>
        <el-button 
          :type="isCollected ? 'primary' : 'default'"
          @click="handleCollection"
        >
          <el-icon><Star /></el-icon>
          收藏 {{ inspiration?.collections || 0 }}
        </el-button>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comments-section">
      <h3>评论区</h3>
      <div class="comment-input">
        <el-input
          v-model="newComment"
          type="textarea"
          :rows="2"
          placeholder="写下你的评论..."
        />
        <el-button 
          type="primary" 
          :loading="submitting"
          @click="handleComment"
        >
          发表评论
        </el-button>
      </div>
    </div>

    <comment-list 
      v-if="inspiration"
      :inspiration-id="inspiration.id"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft, Star, Pointer } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { useInspirationStore } from '@/store/useInspirationStore';
import { useInteractionStore } from '@/store/useInteractionStore';
import CommentList from '@/components/CommentList.vue';

const route = useRoute();
const router = useRouter();
const inspirationStore = useInspirationStore();
const interactionStore = useInteractionStore();

const newComment = ref('');
const submitting = ref(false);

// 获取当前灵感
const inspiration = computed(() => {
  const id = Number(route.params.id);
  return inspirationStore.allInspirations.find(note => note.id === id);
});

// 检查当前用户是否已点赞
const isLiked = computed(() => {
  return interactionStore.currentUserInteraction?.likes.includes(Number(route.params.id)) || false;
});

// 检查当前用户是否已收藏
const isCollected = computed(() => {
  return interactionStore.currentUserInteraction?.collections.includes(Number(route.params.id)) || false;
});

// 处理点赞
const handleLike = () => {
  if (!inspiration.value) return;
  interactionStore.toggleLike(inspiration.value.id);
};

// 处理收藏
const handleCollection = () => {
  if (!inspiration.value) return;
  interactionStore.toggleCollection(inspiration.value.id);
};

// 处理评论
const handleComment = async () => {
  if (!newComment.value.trim() || !inspiration.value) {
    ElMessage.warning('请输入评论内容');
    return;
  }

  submitting.value = true;
  try {
    await interactionStore.addComment(inspiration.value.id, newComment.value.trim());
    newComment.value = '';
    ElMessage.success('评论成功');
  } catch (error) {
    console.error('评论失败:', error);
    ElMessage.error(error instanceof Error ? error.message : '评论失败');
  } finally {
    submitting.value = false;
  }
};

const formatTime = (time?: string) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};
</script>

<style scoped>
.inspiration-detail {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.detail-content {
  margin: 20px 0;
  line-height: 1.6;
  white-space: pre-wrap;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.detail-footer {
  margin-top: 20px;
  padding: 20px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-info {
  color: #666;
  font-size: 14px;
}

.interaction-buttons {
  display: flex;
  gap: 12px;
}

.comments-section {
  margin-top: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.comments-section h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.comment-input {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-input .el-button {
  align-self: flex-end;
}
</style>
