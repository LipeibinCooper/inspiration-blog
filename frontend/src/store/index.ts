import { defineStore } from "pinia";

// 定义 state 的类型
interface CountState {
  count: number;
}

// 定义并导出 store
export const useCountStore = defineStore("count", {
  // 定义 state
  state: (): CountState => ({
    count: 0
  }),

  // 定义 getters
  getters: {
    getCount: (state: CountState): number => state.count
  },

  // 定义 actions
  actions: {
    increment() {
      this.count++;
    }
  }
});
