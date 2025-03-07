<template>
  <div class="inspiration-list" :class="{ 'sidebar-collapsed': isSidebarCollapsed }">
    <el-card
      v-for="inspiration in processedInspirations"
      :key="inspiration.id"
      class="inspiration-card"
      @click="router.push(`/inspiration/${inspiration.id}`)"
      :body-style="{ padding: '16px', height: '100%' }"
    >
      <div class="card-content">
        <div class="card-main">
          <h3 class="title" :title="inspiration.title">{{ inspiration.title }}</h3>
          <p class="content" :title="inspiration.content">
            {{ truncateContent(inspiration.content || '暂无内容') }}
          </p>
        </div>
        <div class="meta-info">
          <span class="author">{{ inspiration.author?.name || '未知用户' }}</span>
          <div class="collections">
            <el-icon><StarFilled /></el-icon>
            <span>{{ inspiration.collections || 0 }}</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { StarFilled } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/useAuthStore";
import type { Inspiration } from "@/types/inspiration";
import { getInspirations } from "@/api/inspiration";

const props = defineProps<{
  inspirations?: Inspiration[];
  isSidebarCollapsed?: boolean;
  loadAll?: boolean;
}>();

const currentPage = ref(1);
const pageSize = ref(15);
const loading = ref(false);
const hasMore = ref(true);
const inspirationList = ref<Inspiration[]>([]);

// 如果是加载全部数据（如个人灵感仓库），直接使用props传入的数据
// 否则使用分页加载的数据
const loadInspirations = async () => {

  // TODO: 先暴力加载全部
  inspirationList.value = props.inspirations;

  // ---start---

  // if (props.loadAll && props.inspirations) {
  //
  //   inspirationList.value = props.inspirations;
  //   console.log('props111',inspirationList.value);
  //   return;
  // }
  //
  // if (!hasMore.value || loading.value) return;

  // try {
  //   loading.value = true;
  //   const response = await getInspirations(currentPage.value, pageSize.value);
  //   const { items, total } = response.data.data;
  //
  //   inspirationList.value = currentPage.value === 1
  //     ? items
  //     : [...inspirationList.value, ...items];
  //
  //   hasMore.value = inspirationList.value?.length < total;
  //   currentPage.value++;
  // } catch (error) {
  //   console.error('加载灵感列表失败:', error);
  // } finally {
  //   loading.value = false;
  // }

  // ---end---
};

// 滚动处理函数
const handleScroll = () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
  const clientHeight = document.documentElement.clientHeight;
  const scrollHeight = document.documentElement.scrollHeight;

  // 当滚动到距离底部100px时触发加载
  if (scrollHeight - scrollTop - clientHeight <= 100) {
    loadInspirations();
  }
};

onMounted(() => {
  if (!props.loadAll) {
    loadInspirations();
    window.addEventListener('scroll', handleScroll);
  }
});

onUnmounted(() => {
  if (!props.loadAll) {
    window.removeEventListener('scroll', handleScroll);
  }
});

const router = useRouter();
const authStore = useAuthStore();

// 处理灵感数据，确保作者信息是最新的
const processedInspirations = computed(() => {
  const list = props.loadAll ? props.inspirations || [] : inspirationList.value;
  console.log('props',props,inspirationList.value);
  console.log('list',list);
  return list?.map(inspiration => {
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

const truncateContent = (content: string) => {
  return content.length > 30 ? content.substring(0, 30) + '...' : content;
};
</script>

<style scoped lang="scss">
.inspiration-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  width: 100%;
  transition: all 0.4s cubic-bezier(0.645, 0.045, 0.355, 1);

  &.sidebar-collapsed {
    grid-template-columns: repeat(4, 1fr);
  }
}

.inspiration-card {
  width: 100%;
  height: 160px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }
}

.card-content {
  height: 100%;
  display: flex;
  flex-direction: column;

  .card-main {
    flex: 1;
    min-height: 0;
    padding-bottom: 16px;

    .title {
      font-size: 18px;
      font-weight: 600;
      color: #1d1d1f;
      margin: 0;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .content {
      margin: 8px 0 0;
      font-size: 14px;
      color: #6e6e73;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }

  .meta-info {
    height: 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
    margin: 0 -16px;
    padding: 4px 16px 15px;

    .author {
      font-size: 14px;
      font-weight: 500;
      color: #1d1d1f;
    }

    .collections {
      display: flex;
      align-items: center;
      gap: 4px;

      .el-icon {
        font-size: 16px;
        color: #e6a23c;
      }

      span {
        font-size: 14px;
        font-weight: 500;
        color: #1d1d1f;
      }
    }
  }
}
</style>
