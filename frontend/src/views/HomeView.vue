<template>
  <div class="home-container">
    <!-- 左侧：灵感列表 -->
    <aside class="sidebar">
      <h2>我的笔记</h2>
      <el-menu :default-active="activeNoteId" class="inspiration-menu">
        <el-menu-item
          v-for="note in inspirationStore.myInspirations"
          :key="note.id"
          :index="note.id.toString()"
          @click="goToDetail(note.id)"
        >
          {{ note.title }}
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 右侧：推荐笔记 -->
    <main class="main-content">
      <header class="top-bar">
        <h1>发现灵感</h1>
        <el-button type="primary" @click="showCreateDialog = true">
          创建灵感
        </el-button>
      </header>

      <div class="recommendations">
        <el-card
          v-for="note in inspirationStore.recommendedInspirations"
          :key="note.id"
          class="inspiration-card"
        >
          <h3>{{ note.title }}</h3>
          <p>{{ note.content.substring(0, 100) }}...</p>
          <el-button type="text" @click="goToDetail(note.id)"
            >查看详情</el-button
          >
        </el-card>
      </div>
    </main>

    <!-- 创建灵感弹窗 -->
    <el-dialog v-model="showCreateDialog" title="创建新灵感">
      <el-form :model="newInspiration">
        <el-form-item label="标题">
          <el-input v-model="newInspiration.title"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="newInspiration.content"
            type="textarea"
            rows="4"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createNewInspiration">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useInspirationStore } from "@/store/useInspirationStore";
import type { CreateInspirationDto } from "@/types/inspiration";

export default defineComponent({
  name: "HomeView",
  setup() {
    const router = useRouter();
    const inspirationStore = useInspirationStore();
    const showCreateDialog = ref(false);
    const newInspiration = ref<CreateInspirationDto>({
      title: "",
      content: ""
    });
    const activeNoteId = ref("");

    // 跳转到详情页
    const goToDetail = (id: number) => {
      activeNoteId.value = id.toString();
      router.push(`/inspiration/${id}`);
    };

    // 创建新灵感
    const createNewInspiration = async () => {
      if (!newInspiration.value.title || !newInspiration.value.content) return;
      await inspirationStore.createInspiration(newInspiration.value);
      showCreateDialog.value = false;
      newInspiration.value = { title: "", content: "" };
    };

    onMounted(() => {
      inspirationStore.fetchInspirations();
    });

    return {
      inspirationStore,
      showCreateDialog,
      newInspiration,
      activeNoteId,
      goToDetail,
      createNewInspiration
    };
  }
});
</script>

<style scoped>
.home-container {
  display: flex;
  height: 100vh;
}

/* 左侧笔记列表 */
.sidebar {
  width: 250px;
  background-color: #f5f5f5;
  padding: 20px;
}

.inspiration-menu {
  border-right: none;
}

/* 右侧内容区域 */
.main-content {
  flex: 1;
  padding: 20px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.recommendations {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.inspiration-card {
  padding: 20px;
  background: white;
}
</style>
