<template>
  <div class="profile-container">
    <!-- 用户信息区域 -->
    <el-card class="user-info">
      <div class="user-header">
        <el-avatar :size="64">{{ userInfo?.username.charAt(0) }}</el-avatar>
        <h2>{{ userInfo?.username }}</h2>
      </div>
      <div class="user-stats">
        <div class="stat-item">
          <span class="number">{{ myInspirations.length }}</span>
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
      <el-tab-pane label="我的灵感" name="inspirations">
        <inspiration-list :inspirations="myInspirations" />
      </el-tab-pane>
      <el-tab-pane label="点赞过" name="likes">
        <inspiration-list :inspirations="likedInspirations" />
      </el-tab-pane>
      <el-tab-pane label="我的评论" name="comments">
        <comment-list :comments="userComments" />
      </el-tab-pane>
      <el-tab-pane label="收藏" name="collections">
        <inspiration-list :inspirations="collectedInspirations" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { useAuthStore } from "@/store/useAuthStore";
import { useInspirationStore } from "@/store/useInspirationStore";
import { useInteractionStore } from "@/store/useInteractionStore";
import InspirationList from "@/components/InspirationList.vue";
import CommentList from "@/components/CommentList.vue";

export default defineComponent({
  name: "ProfileView",
  components: {
    InspirationList,
    CommentList
  },
  setup() {
    const authStore = useAuthStore();
    const inspirationStore = useInspirationStore();
    const interactionStore = useInteractionStore();
    const activeTab = ref("inspirations");

    const userInfo = computed(() => authStore.userInfo);
    const myInspirations = computed(() => inspirationStore.myInspirations);
    const likedInspirations = computed(() => interactionStore.likedInspirations);
    const collectedInspirations = computed(() => interactionStore.collectedInspirations);
    const userComments = computed(() => interactionStore.userComments);

    const totalLikes = computed(() => 
      myInspirations.value.reduce((sum, note) => sum + note.likes, 0)
    );
    const totalCollections = computed(() => 
      myInspirations.value.reduce((sum, note) => sum + note.collections, 0)
    );

    return {
      activeTab,
      userInfo,
      myInspirations,
      likedInspirations,
      collectedInspirations,
      userComments,
      totalLikes,
      totalCollections
    };
  }
});
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
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
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-item .label {
  font-size: 14px;
  color: #666;
}

.profile-tabs {
  background: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
</style> 