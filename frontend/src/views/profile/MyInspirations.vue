<template>
  <div class="my-inspirations">
    <div class="header-actions">
      <el-button v-if="!isManaging" type="primary" @click="handleManage">
        <el-icon><Setting /></el-icon>
        管理灵感
      </el-button>
      <template v-else>
        <el-button 
          type="danger" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          删除选中 ({{ selectedIds.length }})
        </el-button>
        <el-button @click="cancelManage">
          取消
        </el-button>
      </template>
    </div>

    <div class="list-content">
      <div v-for="inspiration in myInspirations" 
           :key="inspiration.id" 
           class="list-item"
           :class="{ 
             'is-managing': isManaging,
             'is-selected': isManaging && selectedIds.includes(inspiration.id)
           }"
           @click="handleItemClick(inspiration)">
        <div class="item-content">
          <div class="item-header">
            <h3 class="item-title">{{ inspiration.title }}</h3>
            <span class="item-time">{{ formatTime(inspiration.createdAt) }}</span>
          </div>
          <div class="item-footer">
            <div class="interaction-info">
              <span class="likes">
                <el-icon><Star /></el-icon>
                {{ inspiration.likes }}
              </span>
              <span class="collections">
                <el-icon><Collection /></el-icon>
                {{ inspiration.collections }}
              </span>
              <span class="comments">
                <el-icon><ChatDotRound /></el-icon>
                {{ inspiration.comments }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { 
  Setting,
  Star,
  Collection,
  ChatDotRound,
  Delete
} from "@element-plus/icons-vue";
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { useInspirationStore } from "@/store/useInspirationStore";
import { ElMessageBox, ElMessage } from "element-plus";
import type { Inspiration } from '@/types/inspiration';

const router = useRouter();
const inspirationStore = useInspirationStore();
const isManaging = ref(false);
const selectedIds = ref<number[]>([]);

const myInspirations = computed(() => inspirationStore.myInspirations);

// 格式化时间
const formatTime = (dateStr: string) => {
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) {
      return '无效时间';
    }
    return formatDistanceToNow(date, { 
      addSuffix: true,
      locale: zhCN 
    });
  } catch (error) {
    console.error('时间格式化错误:', error);
    return '无效时间';
  }
};

// 处理点击灵感卡片
const handleItemClick = (inspiration: Inspiration) => {
  if (isManaging.value) {
    // 管理模式下点击卡片选中/取消选中
    const index = selectedIds.value.indexOf(inspiration.id);
    if (index === -1) {
      selectedIds.value.push(inspiration.id);
    } else {
      selectedIds.value.splice(index, 1);
    }
  } else {
    // 普通模式下跳转到详情
    router.push(`/inspiration/${inspiration.id}`);
  }
};

// 进入管理模式
const handleManage = () => {
  isManaging.value = true;
  selectedIds.value = [];
};

// 取消管理
const cancelManage = () => {
  isManaging.value = false;
  selectedIds.value = [];
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) return;

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个灵感吗？删除后无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    // 批量删除
    for (const id of selectedIds.value) {
      await inspirationStore.deleteInspiration(id);
    }
    
    ElMessage.success('删除成功');
    cancelManage();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};
</script>

<style scoped lang="scss">
.my-inspirations {
  .header-actions {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
  }

  .list-content {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
  }

  .list-item {
    position: relative;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    overflow: hidden;
    height: 120px;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    &.is-managing {
      cursor: pointer;
      
      &:hover {
        background: #f5f7fa;
      }
    }
    
    &.is-selected {
      background: #ecf5ff;
      border: 1px solid #409eff;
      
      &:hover {
        background: #ecf5ff;
      }
      
      .item-title {
        color: #409eff;
      }
    }
  }

  .item-content {
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    cursor: pointer;
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
    
    .item-title {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      flex: 1;
      padding-right: 40px;
    }
    
    .item-time {
      font-size: 13px;
      color: #666;
    }
  }

  .item-footer {
    .interaction-info {
      display: flex;
      gap: 24px;
      font-size: 14px;
      color: #666;
      
      span {
        display: flex;
        align-items: center;
        gap: 6px;
        
        .el-icon {
          font-size: 16px;
        }
        
        &.likes {
          color: #f56c6c;
        }
        
        &.collections {
          color: #e6a23c;
        }
        
        &.comments {
          color: #409eff;
        }
      }
    }
  }
}
</style>