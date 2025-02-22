import { defineStore } from "pinia";
import { ref } from "vue";
import type { Inspiration } from "@/types/inspiration";

interface BrowseRecord {
  inspiration: Inspiration;
  timestamp: string;
}

export const useBrowseHistoryStore = defineStore("browseHistory", () => {
  const browseHistory = ref<BrowseRecord[]>([]);

  // 添加浏览记录
  const addHistory = (inspiration: Inspiration) => {
    // 移除可能存在的重复记录
    browseHistory.value = browseHistory.value.filter(
      record => record.inspiration.id !== inspiration.id
    );
    
    // 添加新记录到最前面
    browseHistory.value.unshift({
      inspiration,
      timestamp: new Date().toISOString()
    });
    
    // 只保留最近的50条记录
    if (browseHistory.value.length > 50) {
      browseHistory.value = browseHistory.value.slice(0, 50);
    }
  };

  // 清空浏览记录
  const clearHistory = () => {
    browseHistory.value = [];
  };

  return {
    browseHistory,
    addHistory,
    clearHistory
  };
}, {
  persist: true
}); 