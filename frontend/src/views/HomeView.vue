<template>
  <div class="home-container">
    <!-- 左侧：灵感列表 -->
    <aside class="sidebar">
      <h2>我的笔记</h2>
      <el-menu :default-active="inspirationStore.activeNoteId" class="inspiration-menu">
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
        <el-button type="primary" round @click="showCreateDialog = true">
          创建灵感
        </el-button>
      </header>

      <div class="recommendations">
        <el-card
          v-for="note in inspirationStore.recommendedInspirations"
          :key="note.id"
          class="inspiration-card"
        >
          <template #header>
            <div class="card-header">
              <h3>{{ note.title }}</h3>
              <el-tag v-if="!note.isPublic" size="small" type="info">私密</el-tag>
            </div>
          </template>
          <p>{{ note.content.substring(0, 80) }}...</p>
          <el-button type="text" @click="goToDetail(note.id)">查看详情</el-button>
        </el-card>
      </div>
    </main>

    <!-- 创建灵感弹窗 -->
    <el-dialog v-model="showCreateDialog" title="创建新灵感">
      <el-form 
        :model="newInspiration"
        :rules="rules"
        ref="formRef"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="newInspiration.title"
            placeholder="请输入标题"
          ></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="newInspiration.content" 
            type="textarea" 
            rows="4"
            placeholder="请输入内容"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-switch
            v-model="newInspiration.isPublic"
            active-text="公开"
            inactive-text="私密"
          />
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
import { defineComponent, ref } from "vue";
import { useRouter } from "vue-router";
import { useInspirationStore } from "@/store/useInspirationStore";
import type { FormInstance } from "element-plus";
import { ElMessage } from "element-plus";

export default defineComponent({
  name: "HomeView",
  setup() {
    const router = useRouter();
    const inspirationStore = useInspirationStore();
    const showCreateDialog = ref(false);
    const formRef = ref<FormInstance>();

    const newInspiration = ref({
      title: "",
      content: "",
      isPublic: true
    });

    // 表单验证规则
    const rules = {
      title: [
        { required: true, message: "标题不能为空", trigger: "blur" }
      ],
      content: [
        { required: true, message: "内容不能为空", trigger: "blur" }
      ]
    };

    // 跳转到详情页
    const goToDetail = (id: number) => {
      router.push(`/inspiration/${id}`);
    };

    // 创建新灵感
    const createNewInspiration = async () => {
      if (!formRef.value) return;
      
      await formRef.value.validate((valid) => {
        if (valid) {
          inspirationStore.addInspiration(newInspiration.value);
          showCreateDialog.value = false;
          newInspiration.value = { title: "", content: "", isPublic: true };
          ElMessage.success("灵感创建成功");
        }
      });
    };

    return {
      inspirationStore,
      showCreateDialog,
      newInspiration,
      createNewInspiration,
      goToDetail,
      formRef,
      rules
    };
  }
});
</script>

<style scoped>
/* 主体布局 */
.home-container {
  display: flex;
  height: 100vh;
  background-color: #f8f9fa;
  font-family: "Arial", sans-serif;
}

/* 左侧笔记列表 */
.sidebar {
  width: 280px;
  background: #fff;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  border-right: 1px solid #eaecef;
}

.inspiration-menu {
  border-right: none;
}

/* 右侧内容区域 */
.main-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 800px;
  margin-bottom: 20px;
}

.recommendations {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  width: 100%;
  max-width: 800px;
}

/* 推荐笔记卡片样式 */
.inspiration-card {
  padding: 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.inspiration-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

/* 添加开关样式 */
.el-switch {
  margin-top: 10px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-tag {
  margin-left: auto;
}
</style>
