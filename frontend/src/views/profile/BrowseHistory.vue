<template>
  <div class="browse-history">
    <div class="header">
      <h3>浏览记录</h3>
      <el-button 
        type="danger" 
        plain 
        size="small"
        @click="handleClear"
        v-if="browseHistory.length">
        清空记录
      </el-button>
    </div>

    <div class="history-list" v-if="browseHistory.length">
      <div 
        v-for="record in browseHistory" 
        :key="record.inspiration.id"
        class="history-item"
        @click="goToDetail(record.inspiration.id)">
        <div class="item-content">
          <h4 class="title">{{ record.inspiration.title }}</h4>
          <p class="content">{{ truncateContent(record.inspiration.content) }}</p>
        </div>
        <div class="item-meta">
          <span class="author">{{ record.inspiration.author.name }}</span>
          <span class="time">{{ formatTime(record.timestamp) }}</span>
        </div>
      </div>
    </div>

    <el-empty 
      v-else 
      description="暂无浏览记录" />
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useBrowseHistoryStore } from "@/store/useBrowseHistoryStore";
import { useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';

const router = useRouter();
const browseHistoryStore = useBrowseHistoryStore();
const browseHistory = computed(() => browseHistoryStore.browseHistory);

// 跳转到详情页
const goToDetail = (id: number) => {
  router.push(`/inspiration/${id}`);
};

// 清空记录
const handleClear = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有浏览记录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    browseHistoryStore.clearHistory();
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

// 截断内容
const truncateContent = (content: string) => {
  return content.length > 50 ? content.substring(0, 50) + '...' : content;
};
</script>

<style scoped lang="scss">
.browse-history {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }
  
  .history-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .history-item {
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
      font-size: 13px;
      color: #999;
      
      .author {
        color: #666;
      }
    }
  }
}
</style> 