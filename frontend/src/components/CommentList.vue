<template>
  <div class="comment-list">
    <div v-if="comments.length" class="comments">
      <div 
        v-for="comment in comments" 
        :key="comment.id"
        class="comment-item"
      >
        <div class="comment-header">
          <div class="user-info" @click="router.push(`/profile/${comment.userId}`)">
            <el-avatar 
              :size="40"
              src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
            />
            <div class="user-meta">
              <span class="username">{{ comment.username }}</span>
              <span class="time">{{ formatDate(comment.createdAt) }}</span>
            </div>
          </div>
        </div>
        <div class="comment-content">
          {{ comment.content }}
        </div>
      </div>
    </div>
    <el-empty 
      v-else 
      description="暂无评论"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import type { Comment } from '@/types/inspiration';
import { useInspirationStore } from '@/store/useInspirationStore';

// eslint-disable-next-line no-undef
const props = withDefaults(defineProps<{
  inspirationId: number;
}>(), {
  inspirationId: 0
});

const router = useRouter();
const inspirationStore = useInspirationStore();

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleString();
};

// 获取评论列表
const comments = computed<Comment[]>(() => {
  const commentList = inspirationStore.getInspirationComments(props.inspirationId)
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  console.log('Comments for inspiration', props.inspirationId, commentList);
  return commentList;
});
</script>

<style scoped>
.comment-list {
  margin-top: 20px;
}

.comments {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.comment-header {
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.user-info:hover {
  opacity: 0.8;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: bold;
  color: #409eff;
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin-left: 52px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
}
</style> 