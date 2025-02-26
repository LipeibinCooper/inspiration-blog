import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css"; // 引入 Element Plus 样式
import "./styles/global.scss";

const app = createApp(App);
const pinia = createPinia();

// 添加持久化插件
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);
app.use(ElementPlus);

app.mount("#app");
