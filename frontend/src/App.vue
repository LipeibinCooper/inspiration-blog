<template>
  <el-container class="app-container">
    <el-header>
      <nav class="nav-bar">
        <router-link to="/" class="logo">
          <img src="@/assets/logo.png" alt="灵感笔记" class="logo-image">
        </router-link>
        <div class="user-area">
          <template v-if="isLoggedIn">
            <el-dropdown>
              <div class="user-info">
                <el-avatar :size="32">{{ userInfo?.username.charAt(0) }}</el-avatar>
                <span>{{ userInfo?.username }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">个人主页</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login">
              <el-button circle>
                <el-icon><User /></el-icon>
              </el-button>
            </router-link>
          </template>
        </div>
      </nav>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";
import { User } from "@element-plus/icons-vue";
import { useAuthStore } from "@/store/useAuthStore";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "App",
  components: { User },
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    const isLoggedIn = computed(() => authStore.isLoggedIn);
    const userInfo = computed(() => authStore.userInfo);

    const handleLogout = () => {
      authStore.logout();
      router.push("/login");
    };

    return {
      isLoggedIn,
      userInfo,
      handleLogout,
      router
    };
  }
});
</script>

<style scoped>
.app-container {
  min-height: 100vh;
}

.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  border-bottom: 1px solid #eaecef;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-image {
  height: 64px;
  width: auto;
}

.user-area {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
</style>
