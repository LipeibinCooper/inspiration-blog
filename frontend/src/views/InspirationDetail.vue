<template>
  <div v-if="inspiration" class="inspiration-detail">
    <!-- 左侧：灵感树区域 -->
    <div class="tree-section">
      <InspirationTree 
        :node="inspiration"
        @update="handleUpdate"
        @add="handleAdd"
        @delete="handleDelete"
      />
    </div>

    <!-- 右侧：互动区域 -->
    <div class="interaction-section">
      <!-- 互动按钮 -->
      <div class="interaction-buttons">
        <el-button 
          type="primary" 
          plain 
          size="small"
          :class="{ active: isLiked }"
          @click="handleLike">
          <el-icon><Pointer /></el-icon>
          {{ inspiration.likes || 0 }}
        </el-button>
        <el-button 
          type="primary" 
          plain
          size="small"
          :class="{ active: isCollected }"
          @click="handleCollect">
          <el-icon><StarFilled /></el-icon>
          {{ inspiration.collections || 0 }}
        </el-button>
      </div>

      <!-- 评论区 -->
      <div class="comments-section">
        <h3>评论 ({{ comments?.length || 0 }})</h3>
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="写下你的想法..."
          />
          <el-button 
            type="primary"
            @click="handleAddComment"
            :loading="submitting"
            :disabled="!newComment.trim()">
            发表评论
          </el-button>
        </div>
        <div class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <div class="user-info">
                <el-avatar 
                  :size="32" 
                  :src="comment.avatar || ''" 
                  class="comment-avatar">
                  {{ comment.username.charAt(0) }}
                </el-avatar>
                <span class="username">{{ comment.username }}</span>
              </div>
              <div class="comment-actions">
                <span class="time">{{ formatTime(comment.createdAt) }}</span>
                <!-- 只有评论作者可以删除评论 -->
                <el-button 
                  v-if="comment.userId === authStore.userInfo?.id"
                  type="text" 
                  class="delete-btn"
                  @click="handleDeleteComment(comment.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="not-found">
    <el-empty description="找不到该灵感" />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { 
  StarFilled, 
  Pointer,
  Delete
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useInspirationStore } from "@/store/useInspirationStore";
import { useInteractionStore } from "@/store/useInteractionStore";
import { useInspirationTreeStore } from "@/store/useInspirationTreeStore";
import InspirationTree from "@/components/InspirationTree.vue";
import type { InspirationNode } from "@/types/inspiration-tree";
import type { CreateNodeDto } from "@/types/inspiration-tree";
import { useAuthStore } from "@/store/useAuthStore";
import CommentList from '@/components/CommentList.vue';

const route = useRoute();
const inspirationStore = useInspirationStore();
const interactionStore = useInteractionStore();
const treeStore = useInspirationTreeStore();
const authStore = useAuthStore();

const newComment = ref("");
const submitting = ref(false);

// 获取灵感 ID
const inspirationId = computed(() => Number(route.params.id));

// 获取灵感详情
const inspiration = computed(() => {
  const allInspirations = inspirationStore.getInspirations();
  return allInspirations.find(note => note.id === inspirationId.value);
});

// 检查当前用户是否已点赞
const isLiked = computed(() => {
  return (
    interactionStore.currentUserInteraction?.likes.includes(
      Number(route.params.id)
    ) || false
  );
});

// 检查当前用户是否已收藏
const isCollected = computed(() => {
  return (
    interactionStore.currentUserInteraction?.collections.includes(
      Number(route.params.id)
    ) || false
  );
});

// 添加评论数据
const comments = computed(() => {
  const id = Number(route.params.id);
  return inspirationStore.getInspirationComments(id) || [];
});

// 处理点赞
const handleLike = () => {
  if (!inspiration.value) return;
  interactionStore.toggleLike(inspiration.value.id);
};

// 处理收藏
const handleCollect = () => {
  if (!inspiration.value) return;
  interactionStore.toggleCollection(inspiration.value.id);
};

// 添加删除评论的方法
const handleDeleteComment = async (commentId: number) => {
  try {
    await inspirationStore.deleteComment(commentId);
    ElMessage.success('评论已删除');
  } catch (error) {
    console.error('删除评论失败:', error);
    ElMessage.error('删除评论失败');
  }
};

// 修改添加评论的方法，添加头像
const handleAddComment = async () => {
  if (!newComment.value.trim()) return;
  
  try {
    submitting.value = true;
    const comment = {
      id: Date.now(),
      content: newComment.value,
      createdAt: new Date().toISOString(),
      userId: authStore.userInfo?.id || 0,
      username: authStore.userInfo?.username || '未知用户',
      avatar: authStore.userInfo?.avatar, // 添加头像
      inspirationId: Number(route.params.id)
    };
    
    await inspirationStore.addComment(comment);
    newComment.value = '';
    ElMessage.success('评论发表成功');
  } catch (error) {
    console.error('发表评论失败:', error);
    ElMessage.error('发表评论失败');
  } finally {
    submitting.value = false;
  }
};

const formatTime = (time?: string) => {
  if (!time) return "";
  return new Date(time).toLocaleString();
};

// 初始化树节点数据
const initTreeData = () => {
  if (!inspiration.value) return;
  
  // 如果没有现有的树节点数据，创建一个根节点
  if (!treeStore.hasTree(inspiration.value.id)) {
    const rootNode: InspirationNode = {
      id: inspiration.value.id,
      title: inspiration.value.title,
      parentId: null,
      content: inspiration.value.content,
      createdAt: inspiration.value.createdAt,
      updatedAt: inspiration.value.updatedAt,
      children: []
    };
    treeStore.initTree(rootNode);
  }
};

// 在组件挂载时初始化树数据
onMounted(() => {
  initTreeData();
  if (!inspiration.value) {
    // 如果找不到灵感数据，可以显示错误信息或重定向
    console.error('找不到灵感数据');
  }
});

// 监听路由变化，重新初始化树数据
watch(() => route.params.id, () => {
  initTreeData();
});

// 处理添加节点
const handleAdd = async (data: CreateNodeDto) => {
  try {
    const newNode: InspirationNode = {
      id: Date.now(),
      title: data.title || '',
      parentId: data.parentId,
      content: data.content,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      children: []
    };

    await treeStore.addNode(data.parentId, newNode);
    ElMessage.success('添加成功');
  } catch (error) {
    console.error('添加节点失败:', error);
    ElMessage.error('添加失败');
  }
};

// 处理更新节点
const handleUpdate = async (data: {
  id: number;
  title?: string;
  content: string;
}) => {
  try {
    await treeStore.updateNode(data.id, {
      title: data.title,
      content: data.content,
      updatedAt: new Date().toISOString()
    });
    ElMessage.success('更新成功');
  } catch (error) {
    console.error('更新节点失败:', error);
    ElMessage.error('更新失败');
  }
};

// 处理删除节点
const handleDelete = async (id: number) => {
  try {
    await treeStore.deleteNode(id);
    ElMessage.success('删除成功');
  } catch (error) {
    console.error('删除节点失败:', error);
    ElMessage.error('删除失败');
  }
};
</script>

<style scoped lang="scss">
.inspiration-detail {
  display: flex;
  height: calc(100vh - 64px); // 减去顶部导航栏高度
  overflow: hidden; // 防止整个页面滚动
}

.tree-section {
  flex: 1;
  overflow: hidden; // 防止溢出
  min-width: 0; // 允许flex item收缩
  padding: 24px;
}

.interaction-section {
  width: 380px;
  border-left: 1px solid rgba(0, 0, 0, 0.06);
  background: #fff;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.interaction-buttons {
  padding: 16px 24px;
  display: flex;
  gap: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  
  .el-button {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    min-width: 88px;
    padding: 8px 16px;
    font-size: 13px;
    
    .el-icon {
      font-size: 14px;
    }
    
    &.active {
      background: rgba(9, 105, 218, 0.1);
      border-color: rgb(9, 105, 218);
      color: rgb(9, 105, 218);
    }
  }
}

.comments-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 24px;
  overflow: hidden;
  
  h3 {
    margin: 0 0 20px;
    font-size: 18px;
    font-weight: 600;
    color: #1d1d1f;
  }
  
  .comment-input {
    margin-bottom: 24px;
    
    .el-button {
      margin-top: 12px;
      width: 100%;
    }
  }
  
  .comments-list {
    flex: 1;
    overflow-y: auto;
    padding-right: 12px;
    
    // 美化滚动条
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: rgba(0, 0, 0, 0.02);
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.1);
      border-radius: 3px;
      
      &:hover {
        background: rgba(0, 0, 0, 0.2);
      }
    }
  }
}

.comments-list {
  .comment-item {
    padding: 16px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    
    &:last-child {
      border-bottom: none;
    }
    
    .comment-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      
      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .comment-avatar {
          border: 1px solid rgba(0, 0, 0, 0.06);
        }
        
        .username {
          font-weight: 500;
          color: #1d1d1f;
        }
      }
      
      .comment-actions {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .time {
          font-size: 13px;
          color: #86868b;
        }
        
        .delete-btn {
          padding: 4px;
          color: #999;
          
          &:hover {
            color: #f56c6c;
          }
          
          .el-icon {
            font-size: 14px;
          }
        }
      }
    }
    
    .comment-content {
      font-size: 14px;
      line-height: 1.6;
      color: #1d1d1f;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }
}

.not-found {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
