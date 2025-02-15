<template>
  <div class="inspiration-detail">
    <el-card class="detail-card">
      <!-- 标题区域 -->
      <template #header>
        <div class="card-header">
          <h2>{{ inspiration?.title }}</h2>
          <div class="header-actions">
            <el-tag v-if="!inspiration?.isPublic" type="info">私密</el-tag>
            <el-button 
              type="primary" 
              circle
              @click="handleCollection"
            >
              <el-icon>
                <component :is="isCollected ? 'StarFilled' : 'Star'" />
              </el-icon>
            </el-button>
          </div>
        </div>
      </template>

      <!-- 内容区域 -->
      <div class="content">
        <p>{{ inspiration?.content }}</p>
      </div>

      <!-- 互动区域 -->
      <div class="interaction-area">
        <div class="stats">
          <el-button 
            :type="isLiked ? 'primary' : 'default'"
            @click="handleLike"
          >
            <el-icon><Pointer /></el-icon>
            {{ inspiration?.likes || 0 }}
          </el-button>
          <el-button type="default">
            <el-icon><ChatRound /></el-icon>
            {{ inspiration?.comments || 0 }}
          </el-button>
          <el-button 
            :type="isCollected ? 'primary' : 'default'"
            @click="handleCollection"
          >
            <el-icon><Star /></el-icon>
            {{ inspiration?.collections || 0 }}
          </el-button>
        </div>
      </div>

      <!-- 评论区域 -->
      <div class="comment-section">
        <h3>评论</h3>
        <!-- 评论输入框 -->
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="2"
            placeholder="写下你的评论..."
          />
          <el-button 
            type="primary" 
            :loading="submitting"
            @click="handleComment"
          >
            发表评论
          </el-button>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <el-timeline>
            <el-timeline-item
              v-for="comment in comments"
              :key="comment.id"
              :timestamp="formatDate(comment.createdAt)"
            >
              <div class="comment-item">
                <div class="comment-header">
                  <el-avatar :size="24">{{ comment.username.charAt(0) }}</el-avatar>
                  <span class="username">{{ comment.username }}</span>
                </div>
                <p class="comment-content">{{ comment.content }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { useRoute } from "vue-router";
import { useInspirationStore } from "@/store/useInspirationStore";
import { useInteractionStore } from "@/store/useInteractionStore";
import { Star, StarFilled, Pointer, ChatRound } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import type { Comment } from "@/types/inspiration";

export default defineComponent({
  name: "InspirationDetail",
  components: {
    Star,
    StarFilled,
    Pointer,
    ChatRound
  },
  setup() {
    const route = useRoute();
    const inspirationStore = useInspirationStore();
    const interactionStore = useInteractionStore();
    const inspirationId = Number(route.params.id);

    const newComment = ref("");
    const submitting = ref(false);

    // 获取当前灵感
    const inspiration = computed(() => 
      inspirationStore.allInspirations.find(note => note.id === inspirationId)
    );

    // 获取评论列表
    const comments = computed(() => 
      inspirationStore.getInspirationComments(inspirationId)
    );

    // 检查当前用户的互动状态
    const isLiked = computed(() => {
      const userInteraction = interactionStore.currentUserInteraction;
      if (!userInteraction) return false;
      return userInteraction.likes.includes(inspirationId);
    });

    const isCollected = computed(() => {
      const userInteraction = interactionStore.currentUserInteraction;
      if (!userInteraction) return false;
      return userInteraction.collections.includes(inspirationId);
    });

    // 处理点赞
    const handleLike = () => {
      interactionStore.toggleLike(inspirationId);
    };

    // 处理收藏
    const handleCollection = () => {
      interactionStore.toggleCollection(inspirationId);
    };

    // 处理评论
    const handleComment = async () => {
      if (!newComment.value.trim()) {
        ElMessage.warning("请输入评论内容");
        return;
      }

      submitting.value = true;
      try {
        await interactionStore.addComment(inspirationId, newComment.value);
        newComment.value = "";
        ElMessage.success("评论成功");
      } catch (error) {
        ElMessage.error("评论失败");
      } finally {
        submitting.value = false;
      }
    };

    const formatDate = (dateStr: string) => {
      const date = new Date(dateStr);
      return date.toLocaleString();
    };

    return {
      inspiration,
      comments,
      newComment,
      submitting,
      isLiked,
      isCollected,
      handleLike,
      handleCollection,
      handleComment,
      formatDate
    };
  }
});
</script>

<style scoped>
.inspiration-detail {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.content {
  margin: 20px 0;
  line-height: 1.6;
}

.interaction-area {
  margin: 20px 0;
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.stats {
  display: flex;
  gap: 20px;
}

.comment-section {
  margin-top: 20px;
}

.comment-input {
  margin: 20px 0;
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.username {
  font-weight: bold;
}

.comment-content {
  margin: 0;
  color: #666;
}
</style>
