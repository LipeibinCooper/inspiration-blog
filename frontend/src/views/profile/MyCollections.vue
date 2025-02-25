<template>
  <div class="my-collections">
    <div class="header">
      <h3>我的收藏</h3>
    </div>

    <div class="collections-list" v-if="collectedInspirations && collectedInspirations.length">
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
            <el-icon><Collection /></el-icon>
            已收藏
          </el-button>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <el-empty description="暂无收藏内容" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted } from 'vue';
import { useRouter } from "vue-router";
import { useInteractionStore } from "@/store/useInteractionStore";
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { ElMessage } from 'element-plus';
import { Collection } from '@element-plus/icons-vue';

const router = useRouter();
const interactionStore = useInteractionStore();

onMounted(async () => {
  await interactionStore.getUserInteractions();
});

const collectedInspirations = computed(() => interactionStore.collectedInspirationList);

const goToDetail = (id: number) => {
  router.push(`/inspiration/${id}`);
};

const handleUncollect = (id: number) => {
  interactionStore.toggleCollection(id);
  ElMessage.success('已取消收藏');
};

const formatTime = (timestamp: string) => {
  return formatDistanceToNow(new Date(timestamp), { 
    addSuffix: true,
    locale: zhCN 
  });
};

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
          background: rgba(9, 105, 218, 0.1);
          border-color: rgb(9, 105, 218);
          color: rgb(9, 105, 218);
        }
        
        .el-icon {
          font-size: 14px;
        }
      }
    }
  }
}
</style> 