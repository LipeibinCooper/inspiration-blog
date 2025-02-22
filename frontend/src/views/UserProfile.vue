<template>
  <div class="user-profile">
    <div class="profile-header">
      <el-avatar :size="80" :src="userInfo?.avatar" />
      <div class="user-info">
        <h1>{{ userInfo?.username }}</h1>
        <p class="bio">{{ userInfo?.bio || '这个人很懒，什么都没写' }}</p>
      </div>
    </div>

    <div class="inspirations-section">
      <h2>公开的灵感</h2>
      <inspiration-list 
        :inspirations="publicInspirations"
        :is-loading="loading"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/store/useAuthStore';
import { useInspirationStore } from '@/store/useInspirationStore';
import InspirationList from '@/components/InspirationList.vue';

const route = useRoute();
const authStore = useAuthStore();
const inspirationStore = useInspirationStore();

const loading = ref(true);
const userInfo = ref(null);

// 获取用户公开的灵感
const publicInspirations = computed(() => {
  const userId = Number(route.params.id);
  return inspirationStore.getInspirations().filter(inspiration => 
    inspiration.userId === userId && inspiration.isPublic
  );
});

onMounted(async () => {
  try {
    const userId = Number(route.params.id);
    userInfo.value = await authStore.getUserById(userId);
  } catch (error) {
    console.error('获取用户信息失败:', error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.user-profile {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;

  .profile-header {
    display: flex;
    align-items: center;
    gap: 24px;
    margin-bottom: 48px;
    
    .user-info {
      h1 {
        font-size: 32px;
        font-weight: 600;
        color: #1d1d1f;
        margin: 0 0 8px;
      }
      
      .bio {
        font-size: 16px;
        color: #6e6e73;
        margin: 0;
        line-height: 1.5;
      }
    }
  }

  .inspirations-section {
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #1d1d1f;
      margin: 0 0 24px;
    }
  }
}
</style> 