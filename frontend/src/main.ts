import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css"; // 引入 Element Plus 样式

const app = createApp(App);

// 创建 Pinia 实例
const pinia = createPinia();

app.use(router).use(pinia).use(ElementPlus).mount("#app");
