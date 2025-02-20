<template>
  <div class="inspiration-tree">
    <!-- 树状图主体 -->
    <div class="tree-scroll-container">
      <div class="tree-container">
        <div class="array-container">
          <div v-for="(array, arrayIndex) in arrays" 
               :key="arrayIndex" 
               class="array-row"
               :style="{ marginLeft: `${arrayIndex * 48}px` }">
            <div v-for="(item, itemIndex) in array"
                 :key="item.id"
                 class="array-item"
                 :class="{ 'root-node': arrayIndex === 0 && itemIndex === 0 }">
              <!-- 修改连接线逻辑 -->
              <div v-if="itemIndex > 0" class="connector horizontal"></div>
              <div v-else-if="arrayIndex > 0" class="connector vertical"></div>
              
              <!-- 节点内容 -->
              <div class="node-content" :class="{ 'has-image': item.thumbnailUrl }">
                <div class="node-content-wrapper" @click="handlePreview(item)">
                  <div class="node-header">
                    <span v-if="arrayIndex === 0 && itemIndex === 0" class="seed-tag">
                      <el-icon><Star /></el-icon>
                      种子灵感
                    </span>
                    <span class="node-time">{{ formatTime(item.createdAt) }}</span>
                  </div>
                  <div class="node-body">
                    <div class="content-area">
                      <div class="node-text truncate">
                        {{ arrayIndex === 0 && itemIndex === 0 ? node.title : item.content }}
                      </div>
                    </div>
                    <div v-if="item.thumbnailUrl" class="thumbnail-container">
                      <img
                        :src="item.thumbnailUrl"
                        class="node-thumbnail"
                        alt="缩略图" />
                    </div>
                  </div>
                </div>
                
                <!-- 节点操作按钮 -->
                <div class="node-actions">
                  <el-dropdown trigger="click" @click.stop>
                    <el-button class="action-button" circle>
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <template v-if="isExpandable(arrayIndex, itemIndex).canExpandRight">
                          <el-dropdown-item @click="handleAddRight(arrayIndex, itemIndex)">
                            <el-icon><ArrowRight /></el-icon> 向右扩展
                          </el-dropdown-item>
                        </template>
                        <template v-if="isExpandable(arrayIndex, itemIndex).canExpandDown">
                          <el-dropdown-item @click="handleAddDown(arrayIndex, itemIndex)">
                            <el-icon><ArrowDown /></el-icon> 向下扩展
                          </el-dropdown-item>
                        </template>
                        <el-dropdown-item @click="handleEditClick(item)">
                          <el-icon><EditPen /></el-icon> 编辑
                        </el-dropdown-item>
                        <el-dropdown-item 
                          v-if="!(arrayIndex === 0 && itemIndex === 0)"
                          @click="handleDelete(arrayIndex, itemIndex)"
                          class="danger">
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
      </div>
    </div>

    <!-- 添加节点对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="addDirection === 'right' ? '向右扩展思维' : '向下延伸想法'"
      width="500px"
      class="custom-dialog">
      <el-form :model="newNodeForm" label-position="top">
        <el-form-item label="内容">
          <el-input
            v-model="newNodeForm.content"
            type="textarea"
            :rows="4"
            placeholder="输入节点内容"
          />
        </el-form-item>
        <el-form-item label="图片">
          <!-- 显示已选择的图片预览 -->
          <div v-if="newNodeForm.images.length" class="selected-images">
            <div v-for="(file, index) in newNodeForm.images" 
                 :key="index" 
                 class="image-item">
              <el-image 
                :src="getPreviewUrl(file)" 
                fit="cover"
                class="preview-thumbnail" />
              <el-button
                type="danger"
                circle
                class="delete-btn"
                @click="handleRemoveNewImage(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
          <!-- 上传新图片 -->
          <el-upload
            class="inspiration-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            multiple
            :on-change="handleImageChange"
            :limit="5">
            <template #trigger>
              <el-button type="primary">选择图片</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                支持jpg/png文件，最多选择5张
                <span v-if="newNodeForm.images.length">
                  (已选择 {{ newNodeForm.images.length }}/5)
                </span>
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="showPreviewDialog"
      title="节点详情"
      width="60%"
      class="preview-dialog">
      <template v-if="previewNode">
        <div class="preview-content">
          <div class="preview-header">
            <span class="preview-time">{{ formatTime(previewNode.createdAt) }}</span>
          </div>
          <div class="preview-body">
            <p class="preview-text">{{ previewNode.content }}</p>
            <div v-if="previewNode.imageUrls?.length" class="preview-images">
              <el-image
                v-for="(url, index) in previewNode.imageUrls"
                :key="index"
                :src="url"
                :preview-src-list="previewNode.imageUrls"
                :initial-index="index"
                fit="cover"
                class="preview-image"
                preview-teleported
                hide-on-click-modal>
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>加载失败</span>
                  </div>
                </template>
              </el-image>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑节点"
      width="500px"
      class="custom-dialog">
      <el-form label-position="top">
        <el-form-item label="内容">
          <el-input
            v-model="editNodeContent"
            type="textarea"
            :rows="4"
            placeholder="输入节点内容"
          />
        </el-form-item>
        <el-form-item label="图片">
          <!-- 显示现有图片 -->
          <div v-if="editNodeImageUrls.length" class="existing-images">
            <div v-for="(url, index) in editNodeImageUrls" 
                 :key="index" 
                 class="image-item">
              <el-image 
                :src="url" 
                fit="cover"
                class="preview-thumbnail" />
              <el-button
                type="danger"
                circle
                class="delete-btn"
                @click="confirmRemoveImage(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
          <!-- 上传新图片 -->
          <el-upload
            class="inspiration-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="true"
            multiple
            :on-change="handleEditImageChange"
            :limit="5">
            <template #trigger>
              <el-button type="primary">选择图片</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">支持jpg/png文件，最多选择5张</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from "vue";
import { 
  MoreFilled, 
  ArrowRight, 
  ArrowDown, 
  EditPen, 
  Delete,
  Star,
  Picture
} from "@element-plus/icons-vue";
import type { InspirationNode } from "@/types/inspiration-tree";
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { useInspirationTreeStore } from "@/store/useInspirationTreeStore";
import { ElMessage, ElMessageBox } from "element-plus";

const props = defineProps<{
  node: {
    id: number;
    title: string;
    content: string;
    // ... 其他属性
  };
}>();

const emit = defineEmits<{
  (e: 'add', data: { 
    parentId: number | null;
    content: string;
    imageUrls?: string[];
    thumbnailUrl?: string;
  }): void;
  (e: 'update', data: { 
    id: number;
    content: string;
    imageUrls?: string[];
    thumbnailUrl?: string;
    isRootNode?: boolean;
  }): void;
  (e: 'delete', id: number): void;
}>();

const treeStore = useInspirationTreeStore();
const arrays = computed(() => treeStore.getTreeNodes(props.node.id));
const showAddDialog = ref(false);
const showEditDialog = ref(false);
const showPreviewDialog = ref(false);
const newNodeForm = ref({
  content: '',
  images: [] as File[]
});
const editNodeContent = ref("");
const currentEditNode = ref<InspirationNode | null>(null);
const previewNode = ref<InspirationNode | null>(null);
const editNodeImages = ref<File[]>([]);
const editNodeImageUrls = ref<string[]>([]);
const addDirection = ref<"right" | "down">("right");
const currentAddPosition = ref<{
  arrayIndex: number;
  itemIndex: number;
} | null>(null);

// 添加类型定义
interface FileItem {
  raw: File;
  url: string;
}

// 格式化时间
const formatTime = (dateStr: string) => {
  return formatDistanceToNow(new Date(dateStr), { 
    addSuffix: true,
    locale: zhCN 
  });
};

// 获取预览URL
const getPreviewUrl = (file: File) => {
  return URL.createObjectURL(file);
};

// 处理图片变更
const handleImageChange = (file: FileItem, fileList: FileItem[]) => {
  // 限制最多5张图片
  const totalImages = newNodeForm.value.images.length + fileList.length;
  if (totalImages > 5) {
    ElMessage.warning('最多只能上传5张图片');
    return;
  }
  
  // 添加新选择的图片
  newNodeForm.value.images.push(file.raw);
};

// 删除已选择的图片
const handleRemoveNewImage = (index: number) => {
  newNodeForm.value.images.splice(index, 1);
};

// 处理编辑点击
const handleEditClick = (item: InspirationNode) => {
  currentEditNode.value = item;
  editNodeContent.value = item.content;
  editNodeImageUrls.value = item.imageUrls || [];
  editNodeImages.value = [];
  showEditDialog.value = true;
};

// 处理编辑图片变更
const handleEditImageChange = (file: FileItem, fileList: FileItem[]) => {
  editNodeImages.value = fileList.map(f => f.raw);
};

// 处理删除现有图片
const confirmRemoveImage = (index: number) => {
  ElMessageBox.confirm(
    '确定要删除这张图片吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    editNodeImageUrls.value = editNodeImageUrls.value.filter((_, i) => i !== index);
    ElMessage.success('图片已删除');
  }).catch(() => {
    // 用户取消删除，不做任何操作
  });
};

// 处理编辑保存
const handleEdit = async () => {
  if (!editNodeContent.value.trim() || !currentEditNode.value) return;

  // 合并现有图片和新上传的图片
  const imageUrls = [
    ...editNodeImageUrls.value,
    ...editNodeImages.value.map(file => URL.createObjectURL(file))
  ];

  const thumbnailUrl = imageUrls.length > 0 ? imageUrls[0] : undefined;

  treeStore.updateNode(props.node.id, currentEditNode.value.id, {
    content: editNodeContent.value.trim(),
    imageUrls,
    thumbnailUrl,
    updatedAt: new Date().toISOString()
  });

  showEditDialog.value = false;
  ElMessage.success('节点已更新');
};

// 处理节点预览
const handlePreview = (node: InspirationNode) => {
  previewNode.value = node;
  showPreviewDialog.value = true;
};

// 初始化数组结构
onMounted(() => {
  if (props.node) {
    const rootNode: InspirationNode = {
      id: props.node.id,
      parentId: null,
      title: props.node.title,
      content: props.node.content,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    treeStore.initTree(rootNode);
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
  if (!newNodeForm.value.content.trim() || !currentAddPosition.value) return;

  const { arrayIndex, itemIndex } = currentAddPosition.value;
  const parentId = arrays.value[arrayIndex][itemIndex].id;

  const imageUrls = newNodeForm.value.images.map(file => URL.createObjectURL(file));
  const thumbnailUrl = imageUrls.length > 0 ? imageUrls[0] : undefined;

  const newNode: InspirationNode = {
    id: Date.now(),
    parentId,
    content: newNodeForm.value.content.trim(),
    imageUrls,
    thumbnailUrl,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  treeStore.addNode(props.node.id, newNode, arrayIndex, addDirection.value);

  emit("add", {
    parentId: newNode.parentId,
    content: newNode.content,
    imageUrls,
    thumbnailUrl
  });

  // 重置表单
  newNodeForm.value = {
    content: '',
    images: []
  };
  showAddDialog.value = false;
};

// 处理删除节点
const handleDelete = (arrayIndex: number, itemIndex: number) => {
  const nodeToDelete = arrays.value[arrayIndex][itemIndex];
  emit("delete", nodeToDelete.id);
  treeStore.deleteNode(props.node.id, arrayIndex, itemIndex);
};
</script>

<style scoped lang="scss">
.inspiration-tree {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tree-scroll-container {
  flex: 1;
  overflow: auto;
  padding: 24px;
  
  // 自定义滚动条样式
  &::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f5f7fa;
    border-radius: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #dcdfe6;
    border-radius: 4px;
    
    &:hover {
      background: #c0c4cc;
    }
  }
  
  // 滚动条交叉处的样式
  &::-webkit-scrollbar-corner {
    background: #f5f7fa;
  }
}

.tree-container {
  min-width: min-content;
  padding-bottom: 16px;
  padding-right: 16px;
}

.array-container {
  display: inline-flex;
  flex-direction: column;
  gap: 48px;
}

.array-row {
  display: flex;
  gap: 48px;
  position: relative;
  white-space: nowrap;
  min-width: min-content;
}

.array-item {
  display: inline-block;
  position: relative;
  white-space: normal;
}

.connector {
  position: absolute;
  background: rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.connector.vertical {
  border-left: 2px dashed rgba(0, 0, 0, 0.08);
  height: 48px;
  left: 50%;
  top: -48px;
  transform: translateX(-50%);
}

.connector.horizontal {
  border-top: 2px dashed rgba(0, 0, 0, 0.08);
  width: 48px;
  top: 50%;
  left: -48px;
  transform: translateY(-50%);
}

.node-content {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  width: 320px;
  height: 160px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }
}

.root-node .node-content {
  background: linear-gradient(135deg, rgb(9, 105, 218) 0%, rgb(0, 122, 255) 100%);
  color: white;
  border: none;
  box-shadow: 0 8px 24px rgba(9, 105, 218, 0.15);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 32px rgba(9, 105, 218, 0.25);
  }
  
  .node-header, .node-text {
    color: rgba(255, 255, 255, 0.95);
  }

  .seed-tag {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(8px);
    
    .el-icon {
      color: rgba(255, 255, 255, 0.95);
    }
  }

  .action-button {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(8px);
    border: none;
    color: white;
    
    &:hover {
      background: rgba(255, 255, 255, 0.25);
      color: white;
    }
  }

  .node-text {
    font-size: 18px;
    font-weight: 600;
  }
}

.node-content-wrapper {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.node-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}

.node-body {
  flex: 1;
  display: flex;
  gap: 12px;
  overflow: hidden;
}

.content-area {
  flex: 1;
  min-width: 0;
}

.node-text {
  font-size: 15px;
  line-height: 1.6;
  color: #1d1d1f;
  
  &.truncate {
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.thumbnail-container {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 6px;
  align-self: center;
}

.node-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.node-actions {
  position: absolute;
  top: 16px;
  right: 16px;
  opacity: 0;
  transition: all 0.3s ease;
  
  .node-content:hover & {
    opacity: 1;
  }
  
  .action-button {
    background: rgba(255, 255, 255, 0.9);
    border: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    
    &:hover {
      background: #fff;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
}

.preview-dialog {
  .preview-content {
    padding: 24px;
    
    .preview-text {
      font-size: 16px;
      line-height: 1.8;
      white-space: pre-wrap;
      word-break: break-word;
    }
    
    .preview-images {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 16px;
      margin-top: 20px;
    }
    
    .preview-image {
      width: 100%;
      aspect-ratio: 1;
      border-radius: 12px;
      cursor: zoom-in;
      transition: transform 0.3s ease;
      
      &:hover {
        transform: scale(1.02);
      }
      
      :deep(.el-image__inner) {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 12px;
      }
    }
    
    .image-error {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #909399;
      font-size: 14px;
      background: #f5f7fa;
      border-radius: 12px;
      
      .el-icon {
        font-size: 24px;
        margin-bottom: 8px;
      }
    }
  }
}

.custom-dialog {
  .inspiration-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    padding: 20px;
    text-align: center;
    
    &:hover {
      border-color: #409eff;
    }
  }
}

.danger {
  color: #f56c6c;
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

.array-item {
  animation: fadeIn 0.4s ease-out;
}

.seed-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.15);
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 13px;
  color: #fff;
  font-weight: 500;
  
  .el-icon {
    font-size: 14px;
  }
}

:deep(.el-image-viewer__wrapper) {
  .el-image-viewer__close {
    color: #fff;
    
    &:hover {
      color: #409eff;
    }
  }
  
  .el-image-viewer__actions {
    opacity: 1;
    background: rgba(0, 0, 0, 0.7);
  }
  
  .el-image-viewer__prev, 
  .el-image-viewer__next {
    width: 44px;
    height: 44px;
    font-size: 24px;
    background-color: rgba(0, 0, 0, 0.3);
    border-radius: 50%;
    
    &:hover {
      background-color: rgba(0, 0, 0, 0.5);
    }
  }
}

.existing-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
  
  .image-item {
    position: relative;
    border-radius: 6px;
    overflow: hidden;
    aspect-ratio: 1;
    
    &:hover .delete-btn {
      opacity: 1;
    }
  }
  
  .preview-thumbnail {
    width: 100%;
    height: 100%;
    
    :deep(.el-image__inner) {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .delete-btn {
    position: absolute;
    top: 4px;
    right: 4px;
    padding: 4px;
    opacity: 0;
    transition: opacity 0.3s ease;
    background-color: rgba(0, 0, 0, 0.5);
    border: none;
    
    &:hover {
      background-color: rgba(0, 0, 0, 0.7);
    }
    
    .el-icon {
      color: #fff;
    }
  }
}

.selected-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
  
  .image-item {
    position: relative;
    border-radius: 6px;
    overflow: hidden;
    aspect-ratio: 1;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    
    &:hover .delete-btn {
      opacity: 1;
    }
  }
  
  .preview-thumbnail {
    width: 100%;
    height: 100%;
    
    :deep(.el-image__inner) {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .delete-btn {
    position: absolute;
    top: 4px;
    right: 4px;
    padding: 4px;
    opacity: 0;
    transition: opacity 0.3s ease;
    background-color: rgba(0, 0, 0, 0.5);
    border: none;
    
    &:hover {
      background-color: rgba(0, 0, 0, 0.7);
    }
    
    .el-icon {
      font-size: 12px;
      color: #fff;
    }
  }
}

.inspiration-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 20px;
  text-align: center;
  background-color: #fafafa;
  
  &:hover {
    border-color: #409eff;
  }
  
  .el-upload__tip {
    margin-top: 8px;
    color: #909399;
  }
}
</style>
