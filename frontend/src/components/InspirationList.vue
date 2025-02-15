<template>
  <div class="inspiration-list">
    <el-card v-for="note in inspirations" :key="note.id" class="inspiration-card">
      <template #header>
        <div class="card-header">
          <h3>{{ note.title }}</h3>
          <el-tag v-if="!note.isPublic" size="small" type="info">私密</el-tag>
        </div>
      </template>
      <p>{{ note.content.substring(0, 80) }}...</p>
      <div class="card-footer">
        <div class="stats">
          <span><el-icon><Star /></el-icon> {{ note.collections }}</span>
          <span><el-icon><ChatRound /></el-icon> {{ note.comments }}</span>
          <span><el-icon><Pointer /></el-icon> {{ note.likes }}</span>
        </div>
        <el-button type="text" @click="router.push(`/inspiration/${note.id}`)">
          查看详情
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { Star, ChatRound, Pointer } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import type { Inspiration } from "@/types/inspiration";

export default defineComponent({
  name: "InspirationList",
  components: {
    Star,
    ChatRound,
    Pointer
  },
  props: {
    inspirations: {
      type: Array as () => Inspiration[],
      required: true
    }
  },
  setup() {
    const router = useRouter();
    return { router };
  }
});
</script>

<style scoped>
.inspiration-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.inspiration-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.stats {
  display: flex;
  gap: 16px;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
}
</style> 