<template>
  <div class="my-comments">
    <div class="header">
      <h3>我的评论</h3>
    </div>

    <div class="comments-list" v-if="userComments.length">
      <div 
        v-for="comment in userComments" 
        :key="comment.id"
        class="comment-item">
        <div class="inspiration-info" @click="goToDetail(comment.inspirationId)">
          <h4 class="title">评论了：{{ comment.inspirationTitle }}</h4>
        </div>
        <div class="comment-content">
          {{ comment.content }}
        </div>
        <div class="comment-meta">
          <span class="time">{{ formatTime(comment.createdAt) }}</span>
          <el-button 
            type="danger" 
            size="small"
            link
            class="delete-btn"
            @click="handleDelete(comment.id)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </div>
    </div>

    <el-empty 
      v-else 
      description="暂无评论" />
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRouter } from "vue-router";
import { useInteractionStore } from "@/store/useInteractionStore";
import { useInspirationStore } from "@/store/useInspirationStore";
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';

const router = useRouter();
const interactionStore = useInteractionStore();
const inspirationStore = useInspirationStore();

// 获取用户的评论列表
const userComments = computed(() => interactionStore.userComments);

// 跳转到灵感详情页
const goToDetail = (id: number) => {
  router.push(`/inspiration/${id}`);
};

// 删除评论
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条评论吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    // 同时调用两个 store 的删除方法
    await inspirationStore.deleteComment(id);
    interactionStore.deleteComment(id);
    ElMessage.success('评论已删除');
  } catch {
    // 用户取消操作
  }
};

// 格式化时间
const formatTime = (timestamp: string) => {
  return formatDistanceToNow(new Date(timestamp), { 
    addSuffix: true,
    locale: zhCN 
  });
};
</script>

<style scoped lang="scss">
.my-comments {
  .header {
    margin-bottom: 24px;
    
    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }
  
  .comments-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .comment-item {
    padding: 16px;
    border-radius: 8px;
    background: #f8f9fa;
    transition: all 0.3s ease;
    
    .inspiration-info {
      margin-bottom: 12px;
      cursor: pointer;
      
      .title {
        margin: 0;
        font-size: 14px;
        color: #666;
        
        &:hover {
          color: #409eff;
        }
      }
    }
    
    .comment-content {
      font-size: 15px;
      line-height: 1.6;
      color: #1d1d1f;
      margin-bottom: 12px;
      white-space: pre-wrap;
      word-break: break-word;
    }
    
    .comment-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      
      .time {
        color: #999;
      }
      
      .delete-btn {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 0;
        
        .el-icon {
          font-size: 14px;
        }
      }
    }
  }
}
</style> 