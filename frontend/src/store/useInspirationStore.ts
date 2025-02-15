import { defineStore } from "pinia";
import { ref } from "vue";
import {
  getInspirations as getInspirationsApi,
  createInspiration as createInspirationApi
} from "@/api/inspiration";
import type { Inspiration, CreateInspirationDto } from "@/types/inspiration";

export const useInspirationStore = defineStore("inspiration", () => {
  const myInspirations = ref<Inspiration[]>([]);
  const recommendedInspirations = ref<Inspiration[]>([]);

  // 获取灵感列表
  const fetchInspirations = async () => {
    try {
      const response = await getInspirationsApi();
      const inspirations = response.data.map((item) => ({
        id: item.id,
        title: item.title,
        content: item.content,
        createdAt: item.createdAt,
        updatedAt: item.createdAt,
        userId: 1
      }));

      myInspirations.value = inspirations.slice(0, 5);
      recommendedInspirations.value = inspirations.slice(5);
    } catch (error) {
      console.error("获取灵感失败", error);
    }
  };

  // 创建新灵感
  const createInspiration = async (data: CreateInspirationDto) => {
    try {
      const response = await createInspirationApi(data);
      const newInspiration: Inspiration = {
        id: response.data.id,
        title: response.data.title,
        content: response.data.content,
        createdAt: response.data.createdAt,
        updatedAt: response.data.createdAt,
        userId: 1
      };

      myInspirations.value.unshift(newInspiration);
    } catch (error) {
      console.error("创建灵感失败", error);
    }
  };

  return {
    myInspirations,
    recommendedInspirations,
    fetchInspirations,
    createInspiration
  };
});
