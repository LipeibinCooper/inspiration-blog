<template>
  <div class="inspiration-tree">
    <div class="array-container">
      <div v-for="(array, arrayIndex) in arrays" :key="arrayIndex" class="array-row">
        <div v-for="(item, itemIndex) in array" :key="item.id" class="array-item">
          <div class="node-content">
            <div class="node-text">{{ item.content }}</div>
            <div class="node-actions">
              <el-button-group>
                <el-button 
                  type="primary" 
                  size="small"
                  @click="handleAddRight(arrayIndex, itemIndex)"
                  title="向右扩展"
                >
                  <el-icon><Plus /></el-icon>
                  →
                </el-button>
                <el-button 
                  type="success" 
                  size="small"
                  @click="handleAddDown(arrayIndex, itemIndex)"
                  title="向下扩展"
                >
                  <el-icon><Plus /></el-icon>
                  ↓
                </el-button>
                <el-button 
                  type="warning" 
                  size="small"
                  @click="showEditDialog = true; currentEditNode = item"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button 
                  type="danger" 
                  size="small"
                  @click="handleDelete(arrayIndex, itemIndex)"
                  v-if="!(arrayIndex === 0 && itemIndex === 0)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加节点对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="addDirection === 'right' ? '向右扩展' : '向下扩展'"
      width="30%"
    >
      <el-input
        v-model="newNodeContent"
        type="textarea"
        :rows="3"
        placeholder="输入节点内容"
      />
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑节点对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑节点"
      width="30%"
    >
      <el-input
        v-model="editNodeContent"
        type="textarea"
        :rows="3"
        placeholder="输入节点内容"
      />
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import type { InspirationNode } from '@/types/inspiration-tree';

const props = defineProps<{
  node: InspirationNode
}>();

const emit = defineEmits<{
  (e: 'update', data: { id: number; content: string }): void
  (e: 'add', data: { parentId: number; content: string }): void
  (e: 'delete', id: number): void
}>();

const arrays = ref<InspirationNode[][]>([[]]);
const showAddDialog = ref(false);
const showEditDialog = ref(false);
const newNodeContent = ref('');
const editNodeContent = ref('');
const addDirection = ref<'right' | 'down'>('right');
const currentEditNode = ref<InspirationNode | null>(null);
const currentAddPosition = ref<{arrayIndex: number; itemIndex: number} | null>(null);

// 初始化数组结构
onMounted(() => {
  if (props.node) {
    arrays.value[0][0] = props.node;
  }
});

// 向右添加节点
const handleAddRight = (arrayIndex: number, itemIndex: number) => {
  addDirection.value = 'right';
  currentAddPosition.value = { arrayIndex, itemIndex };
  showAddDialog.value = true;
};

// 向下添加节点
const handleAddDown = (arrayIndex: number, itemIndex: number) => {
  addDirection.value = 'down';
  currentAddPosition.value = { arrayIndex, itemIndex };
  showAddDialog.value = true;
};

// 处理添加节点
const handleAdd = () => {
  if (!newNodeContent.value.trim() || !currentAddPosition.value) return;
  
  const { arrayIndex, itemIndex } = currentAddPosition.value;
  const newNode: InspirationNode = {
    id: Date.now(),
    parentId: arrays.value[arrayIndex][itemIndex].id,
    content: newNodeContent.value.trim(),
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  if (addDirection.value === 'right') {
    arrays.value[arrayIndex].push(newNode);
  } else {
    if (!arrays.value[arrayIndex + 1]) {
      arrays.value[arrayIndex + 1] = [];
    }
    arrays.value[arrayIndex + 1].push(newNode);
  }

  emit('add', {
    parentId: newNode.parentId,
    content: newNode.content
  });

  newNodeContent.value = '';
  showAddDialog.value = false;
};

// 处理编辑节点
const handleEdit = () => {
  if (!editNodeContent.value.trim() || !currentEditNode.value) return;
  
  emit('update', {
    id: currentEditNode.value.id,
    content: editNodeContent.value.trim()
  });

  // 更新本地节点内容
  arrays.value.forEach(array => {
    array.forEach(node => {
      if (node.id === currentEditNode.value?.id) {
        node.content = editNodeContent.value.trim();
      }
    });
  });

  editNodeContent.value = '';
  showEditDialog.value = false;
};

// 处理删除节点
const handleDelete = (arrayIndex: number, itemIndex: number) => {
  const nodeToDelete = arrays.value[arrayIndex][itemIndex];
  emit('delete', nodeToDelete.id);
  arrays.value[arrayIndex].splice(itemIndex, 1);

  // 如果数组为空且不是第一个数组，则删除该数组
  if (arrays.value[arrayIndex].length === 0 && arrayIndex > 0) {
    arrays.value.splice(arrayIndex, 1);
  }
};

// 监听编辑对话框，设置初始内容
watch(showEditDialog, (newVal) => {
  if (newVal && currentEditNode.value) {
    editNodeContent.value = currentEditNode.value.content;
  }
});
</script>

<style scoped>
.inspiration-tree {
  padding: 20px;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  max-width: 400px;
}

.node-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.node-text {
  flex: 1;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  white-space: pre-wrap;
  margin-bottom: 10px;
}

.node-actions {
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  justify-content: flex-end;
}

.node-content:hover .node-actions {
  opacity: 1;
}

.children {
  padding-left: 40px;
  margin-top: 20px;
  position: relative;
}

.child-node {
  position: relative;
  margin-bottom: 20px;
}

.branch-line {
  position: absolute;
  left: -20px;
  top: -10px;
  bottom: 50%;
  width: 2px;
  background: #ddd;
}

.child-node::before {
  content: '';
  position: absolute;
  left: -20px;
  top: 50%;
  width: 20px;
  height: 2px;
  background: #ddd;
}

.child-node:last-child {
  margin-bottom: 0;
}

.el-dialog__body {
  padding-top: 20px;
}

/* 添加节点展开/收起的过渡动画 */
.children-enter-active,
.children-leave-active {
  transition: all 0.3s ease;
}

.children-enter-from,
.children-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
.array-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding: 20px;
}

.array-row {
  display: flex;
  gap: 32px;
  align-items: flex-start;
  position: relative;
}

.array-row::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  height: 2px;
  background: linear-gradient(90deg, rgba(221,221,221,0) 0%, rgba(221,221,221,1) 50%, rgba(221,221,221,0) 100%);
  z-index: -1;
}

.array-item {
  position: relative;
  animation: fadeIn 0.3s ease;
}

.array-item::before {
  content: '';
  position: absolute;
  left: 50%;
  top: -32px;
  height: 32px;
  width: 2px;
  background: #ddd;
  display: none;
}

.array-row:not(:first-child) .array-item::before {
  display: block;
}

.node-content {
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 240px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.el-button-group .el-button {
  margin: 0 4px;
  border-radius: 6px !important;
}

.el-button-group .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-button-group {
  display: flex;
  gap: 5px;
}
.node-text {
  white-space: pre-wrap;
  margin-bottom: 10px;
}

.node-actions {
  display: flex;
  justify-content: flex-end;
}

.el-button-group {
  display: flex;
  gap: 5px;
}
</style>