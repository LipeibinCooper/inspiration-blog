<template>
  <div class="profile-container">
    <!-- 用户信息区域 -->
    <el-card class="user-info">
      <div class="user-header">
        <el-avatar :size="64">{{ profileUser?.username.charAt(0) }}</el-avatar>
        <h2>{{ profileUser?.username }}</h2>
      </div>
      <div class="user-stats">
        <div class="stat-item">
          <span class="number">{{ userInspirations.length }}</span>
          <span class="label">灵感</span>
        </div>
        <div class="stat-item">
          <span class="number">{{ totalLikes }}</span>
          <span class="label">获赞</span>
        </div>
        <div class="stat-item">
          <span class="number">{{ totalCollections }}</span>
          <span class="label">被收藏</span>
        </div>
      </div>
    </el-card>

    <!-- 内容区域 -->
    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="灵感" name="inspirations">
        <inspiration-list :inspirations="userInspirations" />
      </el-tab-pane>
      <el-tab-pane 
        v-if="isCurrentUser"
        label="点赞过" 
        name="likes"
      >
        <inspiration-list :inspirations="likedInspirations" />
      </el-tab-pane>
      <el-tab-pane label="评论" name="comments">
        <div class="comments-container">
          <div v-if="userComments.length" class="comments-list">
            <div 
              v-for="comment in userComments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-content">
                <p>{{ comment.content }}</p>
                <div class="comment-meta">
                  <span class="time">{{ formatTime(comment.createdAt) }}</span>
                  <el-button 
                    type="text" 
                    @click="router.push(`/inspiration/${comment.inspirationId}`)"
                  >
                    查看灵感
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无评论" />
        </div>
      </el-tab-pane>
      <el-tab-pane 
        v-if="isCurrentUser"
        label="收藏" 
        name="collections"
      >
        <inspiration-list :inspirations="collectedInspirations" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/store/useAuthStore';
import { useInspirationStore } from '@/store/useInspirationStore';
import { useInteractionStore } from '@/store/useInteractionStore';
import InspirationList from '@/components/InspirationList.vue';
import CommentList from '@/components/CommentList.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const inspirationStore = useInspirationStore();
const interactionStore = useInteractionStore();

const activeTab = ref('inspirations');

// 获取要显示的用户信息
const profileUser = computed(() => {
  const userId = Number(route.params.id) || authStore.userInfo?.id;
  return authStore.getUserById(userId);
});

// 判断是否为当前登录用户
const isCurrentUser = computed(() => {
  return profileUser.value?.id === authStore.userInfo?.id;
});

// 获取用户的灵感列表
const userInspirations = computed(() => {
  return inspirationStore.allInspirations.filter(
    note => note.userId === profileUser.value?.id
  );
});

// 获取用户获得的总点赞数
const totalLikes = computed(() => {
  return userInspirations.value.reduce((sum, note) => sum + note.likes, 0);
});

// 获取用户获得的总收藏数
const totalCollections = computed(() => {
  return userInspirations.value.reduce((sum, note) => sum + note.collections, 0);
});

// 仅当前用户可见的数据
const likedInspirations = computed(() => {
  return isCurrentUser.value ? interactionStore.likedInspirations : [];
});

const collectedInspirations = computed(() => {
  return isCurrentUser.value ? interactionStore.collectedInspirations : [];
});

// 获取用户的所有评论
const userComments = computed(() => {
  return inspirationStore.getUserComments(profileUser.value?.id || 0);
});

const formatTime = (time: string) => {
  return new Date(time).toLocaleString();
};
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.user-info {
  margin-bottom: 20px;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.label {
  font-size: 14px;
  color: #666;
}

.profile-tabs {
  margin-top: 20px;
}

.comments-container {
  padding: 20px 0;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
}

.comment-content {
  line-height: 1.6;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style> 