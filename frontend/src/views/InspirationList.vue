<template>
  <div class="inspiration-list">
    <div class="list-header">
      <h2>{{ isMyList ? '我的灵感' : '发现灵感' }}</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建灵感
      </el-button>
    </div>

    <!-- 灵感列表 -->
    <div class="list-content">
      <div v-for="inspiration in inspirations" 
           :key="inspiration.id" 
           class="list-item">
        <div class="item-content" @click="handleClick(inspiration)">
          <div class="item-header">
            <h3 class="item-title">{{ inspiration.title }}</h3>
            <span class="item-time">{{ formatTime(inspiration.createdAt) }}</span>
          </div>
          <p class="item-desc">{{ inspiration.content }}</p>
          <div class="item-footer">
            <div class="author-info">
              <el-avatar :size="24" :src="inspiration.author.avatar" />
              <span>{{ inspiration.author.name }}</span>
            </div>
            <div class="interaction-info">
              <span><el-icon><View /></el-icon>{{ inspiration.likes }}</span>
              <span><el-icon><ChatDotRound /></el-icon>{{ inspiration.comments }}</span>
              <span><el-icon><Star /></el-icon>{{ inspiration.collections }}</span>
            </div>
          </div>
        </div>
        <!-- 删除按钮 -->
        <el-button
          v-if="inspiration.userId === authStore.userInfo?.id"
          type="danger"
          circle
          class="delete-btn"
          @click.stop="handleDelete(inspiration.id)">
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 创建灵感对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建新灵感"
      width="500px">
      <!-- ... 创建对话框的内容 ... -->
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { 
  Plus, 
  View, 
  ChatDotRound, 
  Star, 
  Delete 
} from "@element-plus/icons-vue";
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { useAuthStore } from "@/store/useAuthStore";
import { useInspirationStore } from "@/store/useInspirationStore";
import { ElMessageBox, ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const inspirationStore = useInspirationStore();

const showCreateDialog = ref(false);
const isMyList = computed(() => route.name === 'MyInspirations');
const inspirations = computed(() => 
  isMyList.value ? inspirationStore.myInspirations : inspirationStore.recommendedInspirations
);

// 格式化时间
const formatTime = (dateStr: string) => {
  return formatDistanceToNow(new Date(dateStr), { 
    addSuffix: true,
    locale: zhCN 
  });
};

// 处理点击灵感
const handleClick = (inspiration: any) => {
  router.push(`/inspiration/${inspiration.id}`);
};

// 处理删除灵感树
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个灵感树吗？删除后无法恢复。',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await inspirationStore.deleteInspiration(id);
    ElMessage.success('灵感树已删除');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};
</script>

<style scoped lang="scss">
.inspiration-list {
  padding: 24px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
  }
}

.list-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.list-item {
  position: relative;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    
    .delete-btn {
      opacity: 1;
    }
  }
}

.item-content {
  padding: 20px;
  cursor: pointer;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  
  .item-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
  }
  
  .item-time {
    font-size: 13px;
    color: #666;
  }
}

.item-desc {
  margin: 0 0 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .author-info {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #666;
  }
  
  .interaction-info {
    display: flex;
    gap: 16px;
    font-size: 13px;
    color: #666;
    
    span {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.delete-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1;
  
  &:hover {
    background-color: #f56c6c;
    color: #fff;
  }
}
</style> 