<template>
  <div class="inspiration-tree">
    <div class="array-container">
      <div v-for="(array, arrayIndex) in arrays" :key="arrayIndex" class="array-row">
        <div v-for="(item, itemIndex) in array" :key="item.id" class="array-item">
          <div class="node-content">
            <div class="node-text">{{ item.content }}</div>
            <div class="node-actions">
              <el-dropdown trigger="click">
                <span class="el-dropdown-link">
                  <el-icon><More /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <template v-if="isExpandable(arrayIndex, itemIndex).canExpandRight">
                      <el-dropdown-item @click="handleAddRight(arrayIndex, itemIndex)">
                        <el-icon><Plus /></el-icon> 向右扩展
                      </el-dropdown-item>
                    </template>
                    <template v-if="isExpandable(arrayIndex, itemIndex).canExpandDown">
                      <el-dropdown-item @click="handleAddDown(arrayIndex, itemIndex)">
                        <el-icon><Plus /></el-icon> 向下扩展
                      </el-dropdown-item>
                    </template>
                    <el-dropdown-item @click="() => {showEditDialog = true; currentEditNode = item; editNodeContent = item.content}">
                      <el-icon><Edit /></el-icon> 编辑
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="!(arrayIndex === 0 && itemIndex === 0)"
                      @click="handleDelete(arrayIndex, itemIndex)"
                    >
                      <el-icon><Delete /></el-icon> 删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
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
import { ref, onMounted } from 'vue';
import { Plus, Edit, Delete, More } from '@element-plus/icons-vue';
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
const currentEditNode = ref<InspirationNode | null>(null);

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
const addDirection = ref<'right' | 'down'>('right');
const currentAddPosition = ref<{arrayIndex: number; itemIndex: number} | null>(null);

// 初始化数组结构
onMounted(() => {
  if (props.node) {
    arrays.value[0][0] = props.node;
  }
});

// 判断节点是否可扩展（最右或最下的节点）
const isExpandable = (arrayIndex: number, itemIndex: number) => {
  // 根节点（第一行第一个）特殊处理
  if (arrayIndex === 0 && itemIndex === 0) {
    // 只有当没有其他节点时才能向下扩展
    const hasOnlyRoot = arrays.value.length === 1 && arrays.value[0].length === 1;
    return {
      canExpandRight: false,
      canExpandDown: hasOnlyRoot
    };
  }
  
  // 最右侧节点可以向右扩展
  const canExpandRight = itemIndex === arrays.value[arrayIndex].length - 1;
  
  // 最下方第一个节点可以向下扩展
  const canExpandDown = arrayIndex === arrays.value.length - 1 && itemIndex === 0;
  
  return { canExpandRight, canExpandDown };
};

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
// const handleEdit = () => {
//   if (!editNodeContent.value.trim() || !currentEditNode.value) return;
  
//   emit('update', {
//     id: currentEditNode.value?.id,
//     content: editNodeContent.value.trim()
//   });

//   // 更新本地节点内容
//   arrays.value.forEach(array => {
//     array.forEach(node => {
//       if (node.id === currentEditNode.value?.id) {
//         node.content = editNodeContent.value.trim();
//       }
//     });
//   });

//   editNodeContent.value = '';
//   showEditDialog.value = false;
// };

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
</script>

<style scoped>
.inspiration-tree {
  padding: 20px;
  overflow: hidden;
}

.array-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 20px;
}

.array-row {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  position: relative;
}

.array-row::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  height: 1px;
  background: linear-gradient(90deg, rgba(221,221,221,0) 0%, rgba(221,221,221,1) 50%, rgba(221,221,221,0) 100%);
  z-index: -1;
}

.array-item {
  position: relative;
  animation: fadeIn 0.3s ease;
}

.node-content {
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 180px;
  max-width: 300px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.node-content:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.node-text {
  flex: 1;
  font-size: 13px;
  line-height: 1.4;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
}

.node-actions {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.node-content:hover .node-actions,
.node-content:focus-within .node-actions {
  opacity: 1;
}

.el-dropdown-link {
  cursor: pointer;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.el-dropdown-link:hover {
  background-color: rgba(0, 0, 0, 0.05);
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
</style>