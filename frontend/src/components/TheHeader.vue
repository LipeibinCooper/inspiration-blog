<template>
  <header class="header">
    <div class="left">
      <router-link to="/" class="logo">
        灵感树
      </router-link>
    </div>
    <div class="right">
      <el-dropdown v-if="authStore.isLoggedIn" trigger="click">
        <div class="user-info">
          <el-avatar 
            :size="32" 
            :src="authStore.userInfo?.avatar"
            @error="() => true"
            :key="forceUpdate">
            {{ authStore.userInfo?.username?.charAt(0) }}
          </el-avatar>
          <span class="username">{{ authStore.userInfo?.username }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="router.push('/profile')">
              个人主页
            </el-dropdown-item>
            <el-dropdown-item @click="handleLogout">
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router';
import { useAuthStore } from "@/store/useAuthStore";
import { useUserAvatar } from "@/hooks/useUserAvatar";
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();
const { forceUpdate } = useUserAvatar();

const handleLogout = () => {
  authStore.logout();
  ElMessage.success('已退出登录');
  router.push('/login');
};
</script> 