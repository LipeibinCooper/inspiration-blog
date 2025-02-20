<template>
  <div class="inspiration-list">
    <el-card 
      v-for="note in processedInspirations" 
      :key="note.id" 
      class="inspiration-card"
      @click="router.push(`/inspiration/${note.id}`)"
    >
      <div class="card-content">
        <h3 class="title">{{ note.title }}</h3>
        <p class="content">{{ note.content.substring(0, 80) }}...</p>
        <div class="meta-info">
          <span class="author">{{ note.author.name }}</span>
          <span class="dot">·</span>
          <span class="time">大约 2 小时前</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { Star, ChatRound, Pointer } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/useAuthStore";
import type { Inspiration } from "@/types/inspiration";

const props = defineProps<{
  inspirations: Inspiration[]
}>();

const router = useRouter();
const authStore = useAuthStore();

// 处理灵感数据，确保作者信息是最新的
const processedInspirations = computed(() => {
  return props.inspirations.map(inspiration => {
    const author = authStore.getUserById(inspiration.userId);
    if (author) {
      return {
        ...inspiration,
        author: {
          id: author.id,
          name: author.username
        }
      };
    }
    return inspiration;
  });
});
</script>

<style scoped lang="scss">
.inspiration-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.inspiration-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
  line-height: 1.4;
}

.content {
  font-size: 14px;
  color: #6e6e73;
  line-height: 1.6;
  margin: 0;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #86868b;

  .author {
    color: #1d1d1f;
    font-weight: 500;
  }

  .dot {
    color: #86868b;
  }
}
</style> 