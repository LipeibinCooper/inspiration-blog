<template>
  <div class="my-collections">
    <div class="header">
      <h3>我的收藏</h3>
    </div>

    <div class="collections-list" v-if="collectedInspirations.length">
      <div 
        v-for="inspiration in collectedInspirations" 
        :key="inspiration.id"
        class="collection-item"
        @click="goToDetail(inspiration.id)">
        <div class="item-content">
          <h4 class="title">{{ inspiration.title }}</h4>
          <p class="content">{{ truncateContent(inspiration.content) }}</p>
        </div>
        <div class="item-meta">
          <div class="left">
            <span class="author">{{ inspiration.author.name }}</span>
            <span class="dot">·</span>
            <span class="time">{{ formatTime(inspiration.createdAt) }}</span>
          </div>
          <el-button 
            type="primary" 
            size="small"
            plain
            class="collection-btn"
            :class="{ active: true }"
            @click.stop="handleUncollect(inspiration.id)">
            <el-icon><StarFilled /></el-icon>
            已收藏
          </el-button>
        </div>
      </div>
    </div>

    <el-empty 
      v-else 
      description="暂无收藏内容" />
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRouter } from "vue-router";
import { useInteractionStore } from "@/store/useInteractionStore";
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { ElMessage } from 'element-plus';
import { StarFilled } from '@element-plus/icons-vue';

const router = useRouter();
const interactionStore = useInteractionStore();

// 获取收藏的灵感列表
const collectedInspirations = computed(() => interactionStore.collectedInspirations);

// 跳转到详情页
const goToDetail = (id: number) => {
  router.push(`/inspiration/${id}`);
};

// 取消收藏
const handleUncollect = (id: number) => {
  interactionStore.toggleCollection(id);
  ElMessage.success('已取消收藏');
};

// 格式化时间
const formatTime = (timestamp: string) => {
  return formatDistanceToNow(new Date(timestamp), { 
    addSuffix: true,
    locale: zhCN 
  });
};

// 截断内容
const truncateContent = (content: string) => {
  return content.length > 50 ? content.substring(0, 50) + '...' : content;
};
</script>

<style scoped lang="scss">
.my-collections {
  .header {
    margin-bottom: 24px;
    
    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }
  
  .collections-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .collection-item {
    padding: 16px;
    border-radius: 8px;
    background: #f8f9fa;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: #f0f1f2;
    }
    
    .item-content {
      margin-bottom: 12px;
      
      .title {
        margin: 0 0 8px;
        font-size: 16px;
        font-weight: 500;
      }
      
      .content {
        margin: 0;
        font-size: 14px;
        color: #666;
      }
    }
    
    .item-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      
      .left {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #999;
        
        .author {
          color: #666;
        }
        
        .dot {
          color: #999;
        }
      }
      
      .collection-btn {
        display: flex;
        align-items: center;
        gap: 4px;
        
        &.active {
          background: rgba(230, 162, 60, 0.1);
          border-color: #e6a23c;
          color: #e6a23c;
        }
        
        .el-icon {
          font-size: 14px;
        }
      }
    }
  }
}
</style> 