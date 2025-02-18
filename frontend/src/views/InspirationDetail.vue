<template>
  <div class="inspiration-detail">
    <div class="detail-header">
      <el-button @click="router.back()" :icon="ArrowLeft">返回</el-button>
      <h2>{{ inspiration?.title }}</h2>
    </div>

    <div class="content-wrapper">
      <!-- 左侧：灵感内容和树形图 -->
      <div class="main-content">
        <div class="detail-content">
          <p>{{ inspiration?.content }}</p>
          <!-- 添加灵感树组件 -->
          <div class="inspiration-tree-container" v-if="inspiration">
            <inspiration-tree
              :node="{
                id: inspiration.id,
                parentId: null,
                content: inspiration.title,
                createdAt: inspiration.createdAt,
                updatedAt: inspiration.updatedAt,
                children: treeNodes
              }"
              @update="handleUpdateNode"
              @add="handleAddNode"
              @delete="handleDeleteNode"
            />
          </div>
        </div>

        <div class="detail-footer">
          <div class="time-info">
            创建时间: {{ formatTime(inspiration?.createdAt) }}
            <template v-if="inspiration?.updatedAt">
              | 更新时间: {{ formatTime(inspiration?.updatedAt) }}
            </template>
          </div>

          <!-- 互动按钮区 -->
          <div class="interaction-buttons">
            <el-button :type="isLiked ? 'primary' : 'default'" @click="handleLike">
              <el-icon><Pointer /></el-icon>
              点赞 {{ inspiration?.likes || 0 }}
            </el-button>
            <el-button
              :type="isCollected ? 'primary' : 'default'"
              @click="handleCollection"
            >
              <el-icon><Star /></el-icon>
              收藏 {{ inspiration?.collections || 0 }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 右侧：评论区 -->
      <div class="comments-section">
        <h3>评论区</h3>
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="2"
            placeholder="写下你的评论..."
          />
          <el-button type="primary" :loading="submitting" @click="handleComment">
            发表评论
          </el-button>
        </div>
        <comment-list v-if="inspiration" :inspiration-id="inspiration.id" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ArrowLeft, Star, Pointer } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useInspirationStore } from "@/store/useInspirationStore";
import { useInteractionStore } from "@/store/useInteractionStore";
import CommentList from "@/components/CommentList.vue";
import InspirationTree from "@/components/InspirationTree.vue";
import type { InspirationNode } from "@/types/inspiration-tree";
import type { CreateNodeDto, UpdateNodeDto } from "@/types/inspiration-tree";

const route = useRoute();
const router = useRouter();
const inspirationStore = useInspirationStore();
const interactionStore = useInteractionStore();

const newComment = ref("");
const submitting = ref(false);

// 获取当前灵感
const inspiration = computed(() => {
  const id = Number(route.params.id);
  return inspirationStore.allInspirations.find((note) => note.id === id);
});

// 检查当前用户是否已点赞
const isLiked = computed(() => {
  return (
    interactionStore.currentUserInteraction?.likes.includes(
      Number(route.params.id)
    ) || false
  );
});

// 检查当前用户是否已收藏
const isCollected = computed(() => {
  return (
    interactionStore.currentUserInteraction?.collections.includes(
      Number(route.params.id)
    ) || false
  );
});

// 处理点赞
const handleLike = () => {
  if (!inspiration.value) return;
  interactionStore.toggleLike(inspiration.value.id);
};

// 处理收藏
const handleCollection = () => {
  if (!inspiration.value) return;
  interactionStore.toggleCollection(inspiration.value.id);
};

// 处理评论
const handleComment = async () => {
  if (!newComment.value.trim() || !inspiration.value) {
    ElMessage.warning("请输入评论内容");
    return;
  }

  submitting.value = true;
  try {
    await interactionStore.addComment(
      inspiration.value.id,
      newComment.value.trim()
    );
    newComment.value = "";
    ElMessage.success("评论成功");
  } catch (error) {
    console.error("评论失败:", error);
    ElMessage.error(error instanceof Error ? error.message : "评论失败");
  } finally {
    submitting.value = false;
  }
};

const formatTime = (time?: string) => {
  if (!time) return "";
  return new Date(time).toLocaleString();
};

// 树节点数据
const treeNodes = ref<InspirationNode[]>([
  {
    id: 101,
    parentId: null,
    content: "产品创新思路",
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    children: [
      {
        id: 102,
        parentId: 101,
        content: "用户需求分析",
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        children: [
          {
            id: 105,
            parentId: 102,
            content: "痛点调研",
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          }
        ]
      },
      {
        id: 103,
        parentId: 101,
        content: "市场竞争分析",
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    ]
  },
  {
    id: 104,
    parentId: null,
    content: "技术实现方案",
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    children: [
      {
        id: 106,
        parentId: 104,
        content: "架构设计",
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    ]
  }
]);

// 处理添加节点
const handleAddNode = async (data: CreateNodeDto) => {
  const newNode: InspirationNode = {
    id: Date.now(),
    parentId: data.parentId,
    content: data.content,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  // 添加到对应的父节点下
  const addToParent = (nodes: InspirationNode[], parentId: number) => {
    for (const node of nodes) {
      if (node.id === parentId) {
        if (!node.children) node.children = [];
        node.children.push(newNode);
        return true;
      }
      if (node.children && addToParent(node.children, parentId)) {
        return true;
      }
    }
    return false;
  };

  if (data.parentId === inspiration.value?.id) {
    treeNodes.value.push(newNode);
  } else if (data.parentId) {
    addToParent(treeNodes.value, data.parentId);
  }
};

// 处理更新节点
const handleUpdateNode = async (data: { id: number; content: string }) => {
  try {
    // 这里应该调用后端API更新节点
    console.log("更新节点:", data);
    ElMessage.success("节点更新成功");
  } catch (error) {
    console.error("更新节点失败:", error);
    ElMessage.error("更新节点失败");
  }
};

// 处理添加节点
// const handleAddNode = async (data: { parentId: number; content: string }) => {
//   try {
//     // 这里应该调用后端API添加节点
//     console.log('添加节点:', data);
//     ElMessage.success('节点添加成功');
//   } catch (error) {
//     console.error('添加节点失败:', error);
//     ElMessage.error('添加节点失败');
//   }
// };

// 处理删除节点
const handleDeleteNode = async (id: number) => {
  try {
    // 这里应该调用后端API删除节点
    console.log("删除节点:", id);
    ElMessage.success("节点删除成功");
  } catch (error) {
    console.error("删除节点失败:", error);
    ElMessage.error("删除节点失败");
  }
};
const deleteNode = (nodes: InspirationNode[], id: number): boolean => {
  for (let i = 0; i < nodes.length; i++) {
    if (nodes[i].id === id) {
      nodes.splice(i, 1);
      return true;
    }
    if (nodes[i].children && deleteNode(nodes[i].children, id)) {
      return true;
    }
  }
  return false;
};
</script>

<style scoped>
.inspiration-detail {
  padding: 20px;
  max-width: 100%;
  margin: 0 auto;
}

.content-wrapper {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.main-content {
  flex: 3;
}

.comments-section {
  flex: 1;
  min-width: 300px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}
.detail-content {
  margin-bottom: 20px;
  line-height: 1.6;
  white-space: pre-wrap;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.detail-footer {
  margin-top: 20px;
  padding: 20px;
  border-radius: 8px;
  background: #fff;
}
.comment-input {
  margin-bottom: 20px;
}
.comment-input .el-button {
  margin-top: 10px;
  width: 100%;
}
</style>
