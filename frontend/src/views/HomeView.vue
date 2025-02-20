<template>
  <div class="home-container">
    <!-- 左侧：我的灵感树 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>我的灵感树</h2>
        <el-button 
          type="primary" 
          plain
          circle
          @click="showCreateDialog = true"
        >
          <el-icon><Plus /></el-icon>
        </el-button>
      </div>
      <div class="inspiration-list">
        <div
          v-for="note in inspirationStore.myInspirations"
          :key="note.id"
          class="inspiration-item"
          :class="{ active: note.id === currentId }"
          @click="goToDetail(note.id)"
        >
          <span class="title">{{ note.title }}</span>
          <el-tag v-if="!note.isPublic" size="small" type="info">私密</el-tag>
        </div>
      </div>
    </aside>

    <!-- 右侧：发现灵感 -->
    <main class="main-content">
      <div class="content-wrapper">
        <header class="section-header">
          <h1>发现灵感</h1>
          <p class="subtitle">探索他人的思维火花，激发你的创意灵感</p>
        </header>

        <!-- 只保留这一个 InspirationList -->
        <inspiration-list :inspirations="inspirationStore.recommendedInspirations" />
      </div>
    </main>

    <!-- 创建灵感树对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建灵感树"
      width="600px"
      :close-on-click-modal="false"
      class="create-dialog">
      <div class="dialog-content">
        <el-form 
          :model="createForm" 
          :rules="rules"
          ref="createFormRef"
          label-position="top"
          class="create-form">
          <el-form-item 
            label="种子灵感" 
            prop="title"
            class="form-item">
            <div class="form-item-hint">这是你灵感树的核心节点，后续可以基于它延伸更多想法</div>
            <el-input
              v-model="createForm.title"
              type="text"
              placeholder="输入灵感树的核心想法"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item 
            label="描述（选填）" 
            class="form-item">
            <div class="form-item-hint">补充说明你的想法，让它更容易被理解</div>
            <el-input
              v-model="createForm.content"
              type="textarea"
              :rows="4"
              placeholder="补充说明你的想法..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="可见性" class="form-item">
            <el-radio-group v-model="createForm.isPublic" class="visibility-group">
              <div class="visibility-options">
                <el-radio :label="true" class="visibility-option">
                  <template #default>
                    <div class="visibility-label">
                      <span class="visibility-title">公开</span>
                      <span class="visibility-desc">所有人都可以看到这棵灵感树</span>
                    </div>
                  </template>
                </el-radio>
                <el-radio :label="false" class="visibility-option">
                  <template #default>
                    <div class="visibility-label">
                      <span class="visibility-title">私密</span>
                      <span class="visibility-desc">只有你自己可以看到这棵灵感树</span>
                    </div>
                  </template>
                </el-radio>
              </div>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            :loading="creating"
            @click="handleCreate">
            创建灵感树
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useInspirationStore } from "@/store/useInspirationStore";
import type { FormInstance } from "element-plus";
import { ElMessage } from "element-plus";
import { Plus } from '@element-plus/icons-vue';
import InspirationList from "@/components/InspirationList.vue";

const router = useRouter();
const inspirationStore = useInspirationStore();
const createFormRef = ref<FormInstance>();
const showCreateDialog = ref(false);
const creating = ref(false);

// 初始化表单数据
const createForm = ref({
  title: "",
  content: "",
  isPublic: true
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入种子灵感', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
};

// 创建新灵感
const handleCreate = async () => {
  if (!createFormRef.value) return;
  
  try {
    creating.value = true;
    await createFormRef.value.validate();
    
    const newId = await inspirationStore.addInspiration({
      title: createForm.value.title,
      content: createForm.value.content,
      isPublic: createForm.value.isPublic
    });

    showCreateDialog.value = false;
    createForm.value = { title: "", content: "", isPublic: true };
    ElMessage.success("灵感创建成功");
    router.push(`/inspiration/${newId}`);
  } catch (error) {
    ElMessage.error("创建失败");
  } finally {
    creating.value = false;
  }
};

// 跳转到详情页
const goToDetail = (id: number) => {
  router.push(`/inspiration/${id}`);
};
</script>

<style scoped lang="scss">
.home-container {
  display: flex;
  min-height: 100vh;
  background-color: #fff;
}

/* 左侧边栏 */
.sidebar {
  width: 320px;
  background: #fbfbfd;
  border-right: 1px solid rgba(0, 0, 0, 0.03);
  
  .sidebar-header {
    padding: 32px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(20px);
    border-bottom: 1px solid rgba(0, 0, 0, 0.03);
    position: sticky;
    top: 0;
    z-index: 10;
    
    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #1d1d1f;
      letter-spacing: -0.02em;
    }
  }
}

.inspiration-list {
  padding: 16px;
}

.inspiration-item {
  padding: 16px 20px;
  margin-bottom: 8px;
  border-radius: 12px;
  background: transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    background: rgba(0, 0, 0, 0.02);
  }
  
  &.active {
    background: rgba(0, 0, 0, 0.03);
  }
  
  .title {
    font-size: 15px;
    color: #1d1d1f;
    letter-spacing: -0.01em;
  }
}

/* 主内容区 */
.main-content {
  flex: 1;
  padding: 64px 40px;
  background: #fff;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  max-width: 600px;
  margin: 0 auto 64px;
  text-align: center;
  
  h1 {
    font-size: 48px;
    font-weight: 600;
    color: #1d1d1f;
    letter-spacing: -0.03em;
    margin: 0 0 16px;
    line-height: 1.1;
  }
  
  .subtitle {
    font-size: 24px;
    color: #6e6e73;
    letter-spacing: -0.02em;
    line-height: 1.4;
  }
}

.inspiration-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.inspiration-card {
  background: #fbfbfd;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  
  &:hover {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
  }
  
  .card-content {
    padding: 32px;
    flex: 1;
    display: flex;
    align-items: center;
    
    h3 {
      font-size: 24px;
      font-weight: 600;
      color: #1d1d1f;
      letter-spacing: -0.02em;
      margin: 0;
      line-height: 1.3;
    }
  }
  
  .card-footer {
    padding: 24px 32px;
    background: rgba(0, 0, 0, 0.02);
    border-top: 1px solid rgba(0, 0, 0, 0.03);
    
    .footer-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .author-info {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .author-avatar {
        border: 2px solid #fff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      }
      
      .author-name {
        font-size: 15px;
        font-weight: 500;
        color: #1d1d1f;
        letter-spacing: -0.01em;
      }
    }

    .meta-info {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .time {
        font-size: 14px;
        color: #86868b;
        letter-spacing: -0.01em;
      }
    }
  }
}

/* 创建灵感树对话框样式 */
.create-dialog {
  :deep(.el-dialog) {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 24px 48px rgba(0, 0, 0, 0.12);
    
    .el-dialog__header {
      padding: 32px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.03);
      
      .el-dialog__title {
        font-size: 24px;
        font-weight: 600;
        color: #1d1d1f;
        letter-spacing: -0.02em;
      }
    }
    
    .el-dialog__body {
      padding: 40px;
    }
  }

  .form-item {
    margin-bottom: 32px;
    
    :deep(.el-form-item__label) {
      font-size: 16px;
      color: #1d1d1f;
      font-weight: 500;
      letter-spacing: -0.01em;
    }
    
    .form-item-hint {
      font-size: 14px;
      color: #86868b;
      margin-bottom: 12px;
      letter-spacing: -0.01em;
    }
  }

  .visibility-options {
    display: flex;
    gap: 16px;
  }

  .visibility-option {
    flex: 1;
    padding: 24px;
    border-radius: 16px;
    border: 1px solid rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #06c;
      background: rgba(0, 102, 204, 0.02);
    }
    
    &.is-checked {
      border-color: #06c;
      background: rgba(0, 102, 204, 0.05);
    }
  }
}
</style>
