<template>
  <div class="comment-list">
    <el-timeline>
      <el-timeline-item
        v-for="comment in comments"
        :key="comment.id"
        :timestamp="formatDate(comment.createdAt)"
        placement="top"
      >
        <el-card>
          <template #header>
            <div class="comment-header">
              <span class="inspiration-title">{{ comment.inspirationTitle }}</span>
              <el-button 
                type="text" 
                @click="router.push(`/inspiration/${comment.inspirationId}`)"
              >
                查看灵感
              </el-button>
            </div>
          </template>
          <p>{{ comment.content }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import type { Comment } from "@/types/inspiration";

export default defineComponent({
  name: "CommentList",
  props: {
    comments: {
      type: Array as () => Comment[],
      required: true
    }
  },
  setup() {
    const router = useRouter();

    const formatDate = (dateStr: string) => {
      const date = new Date(dateStr);
      return date.toLocaleDateString();
    };

    return {
      router,
      formatDate
    };
  }
});
</script>

<style scoped>
.comment-list {
  padding: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.inspiration-title {
  font-weight: bold;
}
</style> 