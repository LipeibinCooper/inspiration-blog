<template>
  <div class="inspiration-tree">
    <div class="node-content">
      <div class="node-text">
        {{ node.content }}
      </div>
      <div class="node-actions">
        <el-button-group>
          <el-button 
            type="primary" 
            size="small"
            @click="showAddDialog = true"
          >
            <el-icon><Plus /></el-icon>
          </el-button>
          <el-button 
            type="warning" 
            size="small"
            @click="showEditDialog = true"
          >
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-button 
            type="danger" 
            size="small"
            @click="handleDelete"
            v-if="node.parentId !== null"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 子节点 -->
    <div class="children" v-if="node.children && node.children.length > 0">
      <div v-for="child in node.children" :key="child.id" class="child-node">
        <div class="branch-line"></div>
        <inspiration-tree
          :node="child"
          @update="$emit('update', $event)"
          @add="$emit('add', $event)"
          @delete="$emit('delete', $event)"
        />
      </div>
    </div>

    <!-- 添加节点对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="添加子节点"
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
import { ref } from 'vue';
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

const showAddDialog = ref(false);
const showEditDialog = ref(false);
const newNodeContent = ref('');
const editNodeContent = ref('');

const handleAdd = () => {
  if (!newNodeContent.value.trim()) return;
  emit('add', {
    parentId: props.node.id,
    content: newNodeContent.value.trim()
  });
  newNodeContent.value = '';
  showAddDialog.value = false;
};

const handleEdit = () => {
  if (!editNodeContent.value.trim()) return;
  emit('update', {
    id: props.node.id,
    content: editNodeContent.value.trim()
  });
  editNodeContent.value = '';
  showEditDialog.value = false;
};

const handleDelete = () => {
  emit('delete', props.node.id);
};
</script>

<style scoped>
.inspiration-tree {
  padding: 10px;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  padding: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.node-text {
  flex: 1;
  white-space: pre-wrap;
}

.children {
  margin-left: 20px;
  padding-left: 20px;
  border-left: 2px solid #ddd;
}

.child-node {
  position: relative;
  margin-top: 10px;
}

.branch-line {
  position: absolute;
  left: -22px;
  top: 20px;
  width: 20px;
  height: 2px;
  background: #ddd;
}
</style>