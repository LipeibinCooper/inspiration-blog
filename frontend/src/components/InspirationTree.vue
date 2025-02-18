<template>
  <div class="inspiration-tree">
    <div class="array-container">
      <div
        v-for="(array, arrayIndex) in arrays"
        :key="arrayIndex"
        class="array-row"
      >
        <div
          v-for="(item, itemIndex) in array"
          :key="item.id"
          class="array-item"
        >
          <div class="node-content">
            <div class="node-content-wrapper" @click="handlePreview(item)">
              <div class="node-text">{{ item.content }}</div>
              <img
                v-if="item.thumbnailUrl"
                :src="item.thumbnailUrl"
                class="node-thumbnail"
                alt="缩略图"
              />
            </div>
            <div class="node-actions">
              <el-dropdown trigger="click" @click.stop>
                <span class="el-dropdown-link">
                  <el-icon><More /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <template
                      v-if="isExpandable(arrayIndex, itemIndex).canExpandRight"
                    >
                      <el-dropdown-item
                        @click="handleAddRight(arrayIndex, itemIndex)"
                      >
                        <el-icon><Plus /></el-icon> 向右扩展
                      </el-dropdown-item>
                    </template>
                    <template
                      v-if="isExpandable(arrayIndex, itemIndex).canExpandDown"
                    >
                      <el-dropdown-item
                        @click="handleAddDown(arrayIndex, itemIndex)"
                      >
                        <el-icon><Plus /></el-icon> 向下扩展
                      </el-dropdown-item>
                    </template>
                    <el-dropdown-item
                      @click="
                        () => {
                          showEditDialog = true;
                          currentEditNode = item;
                          editNodeContent = item.content;
                        }
                      "
                    >
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
      <el-upload
        class="upload-demo"
        action="#"
        :auto-upload="false"
        :show-file-list="true"
        :on-change="
          (file) => {
            newNodeImage = file.raw;
          }
        "
      >
        <template #trigger>
          <el-button type="primary">选择图片</el-button>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑节点对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑节点" width="30%">
      <el-input
        v-model="editNodeContent"
        type="textarea"
        :rows="3"
        placeholder="输入节点内容"
      />
      <el-upload
        class="upload-demo"
        action="#"
        :auto-upload="false"
        :show-file-list="true"
        :on-change="
          (file) => {
            editNodeImage = file.raw;
          }
        "
      >
        <template #trigger>
          <el-button type="primary">选择图片</el-button>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="showPreviewDialog" title="节点详情" width="50%">
      <template v-if="previewNode">
        <div class="preview-content">
          <p>{{ previewNode.content }}</p>
          <img
            v-if="previewNode.imageUrl"
            :src="previewNode.imageUrl"
            class="preview-image"
            alt="完整图片"
          />
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from "vue";
import { Plus, Edit, Delete, More } from "@element-plus/icons-vue";
import type { InspirationNode } from "@/types/inspiration-tree";

const props = defineProps<{
  node: InspirationNode;
}>();

const emit = defineEmits<{
  (
    e: "update",
    data: {
      id: number;
      content: string;
      imageUrl?: string;
      thumbnailUrl?: string;
    }
  ): void;
  (
    e: "add",
    data: {
      parentId: number;
      content: string;
      imageUrl?: string;
      thumbnailUrl?: string;
    }
  ): void;
  (e: "delete", id: number): void;
}>();

const arrays = ref<InspirationNode[][]>([[]]);
const showAddDialog = ref(false);
const showEditDialog = ref(false);
const showPreviewDialog = ref(false);
const newNodeContent = ref("");
const editNodeContent = ref("");
const currentEditNode = ref<InspirationNode | null>(null);
const previewNode = ref<InspirationNode | null>(null);
const newNodeImage = ref<File | null>(null);
const editNodeImage = ref<File | null>(null);

// 处理编辑节点
const handleEdit = async () => {
  if (!editNodeContent.value.trim() || !currentEditNode.value) return;

  let imageUrl = currentEditNode.value.imageUrl;
  let thumbnailUrl = currentEditNode.value.thumbnailUrl;

  if (editNodeImage.value) {
    // 这里应该调用后端API上传图片，获取图片URL和缩略图URL
    // 为了演示，这里使用本地URL
    imageUrl = URL.createObjectURL(editNodeImage.value);
    thumbnailUrl = imageUrl; // 实际应该使用缩略图URL
  }

  emit("update", {
    id: currentEditNode.value.id,
    content: editNodeContent.value.trim(),
    imageUrl,
    thumbnailUrl
  });

  // 更新本地节点内容
  arrays.value.forEach((array) => {
    array.forEach((node) => {
      if (node.id === currentEditNode.value?.id) {
        node.content = editNodeContent.value.trim();
        node.imageUrl = imageUrl;
        node.thumbnailUrl = thumbnailUrl;
      }
    });
  });

  editNodeContent.value = "";
  editNodeImage.value = null;
  showEditDialog.value = false;
};

// 处理节点预览
const handlePreview = (node: InspirationNode) => {
  previewNode.value = node;
  showPreviewDialog.value = true;
};
const addDirection = ref<"right" | "down">("right");
const currentAddPosition = ref<{
  arrayIndex: number;
  itemIndex: number;
} | null>(null);

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
    const hasOnlyRoot =
      arrays.value.length === 1 && arrays.value[0].length === 1;
    return {
      canExpandRight: false,
      canExpandDown: hasOnlyRoot
    };
  }

  // 最右侧节点可以向右扩展
  const canExpandRight = itemIndex === arrays.value[arrayIndex].length - 1;

  // 最下方第一个节点可以向下扩展
  const canExpandDown =
    arrayIndex === arrays.value.length - 1 && itemIndex === 0;

  return { canExpandRight, canExpandDown };
};

// 向右添加节点
const handleAddRight = (arrayIndex: number, itemIndex: number) => {
  addDirection.value = "right";
  currentAddPosition.value = { arrayIndex, itemIndex };
  showAddDialog.value = true;
};

// 向下添加节点
const handleAddDown = (arrayIndex: number, itemIndex: number) => {
  addDirection.value = "down";
  currentAddPosition.value = { arrayIndex, itemIndex };
  showAddDialog.value = true;
};

// 处理添加节点
const handleAdd = () => {
  if (!newNodeContent.value.trim() || !currentAddPosition.value) return;

  let imageUrl = undefined;
  let thumbnailUrl = undefined;

  if (newNodeImage.value) {
    // 这里应该调用后端API上传图片，获取图片URL和缩略图URL
    // 为了演示，这里使用本地URL
    imageUrl = URL.createObjectURL(newNodeImage.value);
    thumbnailUrl = imageUrl; // 实际应该使用缩略图URL
  }

  const { arrayIndex, itemIndex } = currentAddPosition.value;
  const newNode: InspirationNode = {
    id: Date.now(),
    parentId: arrays.value[arrayIndex][itemIndex].id,
    content: newNodeContent.value.trim(),
    imageUrl,
    thumbnailUrl,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  if (addDirection.value === "right") {
    arrays.value[arrayIndex].push(newNode);
  } else {
    if (!arrays.value[arrayIndex + 1]) {
      arrays.value[arrayIndex + 1] = [];
    }
    arrays.value[arrayIndex + 1].push(newNode);
  }

  emit("add", {
    parentId: newNode.parentId,
    content: newNode.content,
    imageUrl,
    thumbnailUrl
  });

  newNodeContent.value = "";
  newNodeImage.value = null;
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
  emit("delete", nodeToDelete.id);
  arrays.value[arrayIndex].splice(itemIndex, 1);

  // 如果数组为空且不是第一个数组，则删除该数组
  if (arrays.value[arrayIndex].length === 0 && arrayIndex > 0) {
    arrays.value.splice(arrayIndex, 1);
  }
};
</script>

<style scoped>
.inspiration-tree {
  padding: 32px 64px;
  overflow: auto;
  min-height: 100vh;
  background: linear-gradient(to bottom, #f8f9fa, #ffffff);
}

.array-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding: 24px 32px;
  position: relative;
}

.array-row {
  display: flex;
  gap: 32px;
  align-items: flex-start;
  position: relative;
  padding: 16px 0;
}

.array-row::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  height: 2px;
  background: linear-gradient(
    90deg,
    rgba(66, 133, 244, 0) 0%,
    rgba(66, 133, 244, 0.2) 50%,
    rgba(66, 133, 244, 0) 100%
  );
  z-index: -1;
  transform: translateY(-50%);
}

.array-item {
  position: relative;
  animation: fadeIn 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.node-content {
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(66, 133, 244, 0.1);
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 200px;
  max-width: 320px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.node-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(66, 133, 244, 0.15);
  border-color: rgba(66, 133, 244, 0.2);
}

.node-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.node-content-wrapper:hover {
  background-color: rgba(66, 133, 244, 0.05);
}

.node-thumbnail {
  max-width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.node-thumbnail:hover {
  transform: scale(1.05);
}

.preview-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.preview-content p {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.8;
  color: #333;
  font-size: 15px;
}

.preview-content img {
  max-width: 100%;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.preview-content img:hover {
  transform: scale(1.02);
}

.node-text {
  flex: 1;
  font-size: 14px;
  line-height: 1.6;
  color: #2c3e50;
  white-space: pre-wrap;
  word-break: break-word;
}

.node-actions {
  opacity: 0;
  transition: all 0.3s ease;
  align-self: flex-start;
  transform: translateX(10px);
}

.node-content:hover .node-actions {
  opacity: 1;
  transform: translateX(0);
}

.el-dropdown-link {
  cursor: pointer;
  color: #606266;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.el-dropdown-link:hover {
  background-color: rgba(66, 133, 244, 0.1);
  color: #4285f4;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
