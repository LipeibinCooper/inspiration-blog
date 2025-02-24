<template>
  <div class="profile-view">
    <div class="profile-header">
      <el-avatar 
        :size="64" 
        :src="userInfo?.avatar"
        :key="forceUpdate">
        {{ userInfo?.username?.charAt(0) }}
      </el-avatar>
      <h2>{{ userInfo?.username }}</h2>
    </div>

    <div class="profile-content">
      <div class="sidebar">
        <div class="menu-list">
          <router-link 
            v-for="menu in menuItems" 
            :key="menu.path"
            :to="menu.path"
            class="menu-item"
            active-class="active">
            <el-icon><component :is="menu.icon" /></el-icon>
            {{ menu.title }}
          </router-link>
        </div>
      </div>
      
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useAuthStore } from "@/store/useAuthStore";
import { useUserAvatar } from "@/hooks/useUserAvatar";
import { 
  User,
  Memo,
  Clock,
  Star,
  Collection,
  ChatDotRound
} from "@element-plus/icons-vue";

const authStore = useAuthStore();
const userInfo = computed(() => authStore.userInfo);
const { forceUpdate } = useUserAvatar();

const menuItems = [
  {
    title: '个人信息',
    icon: User,
    name: 'profile-info',
    path: '/profile/info'
  },
  {
    title: '我的灵感',
    icon: Memo,
    name: 'my-inspirations',
    path: '/profile/inspirations'
  },
  {
    title: '浏览记录',
    icon: Clock,
    name: 'browse-history',
    path: '/profile/history'
  },
  {
    title: '我的点赞',
    icon: Star,
    name: 'my-likes',
    path: '/profile/likes'
  },
  {
    title: '我的收藏',
    icon: Collection,
    name: 'my-collections',
    path: '/profile/collections'
  },
  {
    title: '我的评论',
    icon: ChatDotRound,
    name: 'my-comments',
    path: '/profile/comments'
  }
];
</script>

<style scoped lang="scss">
.profile-view {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  
  h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
  }
}

.profile-content {
  display: flex;
  gap: 24px;
  min-height: calc(100vh - 200px);
}

.sidebar {
  width: 200px;
  flex-shrink: 0;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #606266;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  
  &:hover {
    background: #f5f7fa;
    color: #409eff;
  }
  
  &.active {
    background: #ecf5ff;
    color: #409eff;
    font-weight: 500;
  }
  
  .el-icon {
    font-size: 18px;
  }
}

.main-content {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
</style> 