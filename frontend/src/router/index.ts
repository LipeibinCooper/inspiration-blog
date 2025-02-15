import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";

// 路由配置
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: () => import("../views/HomeView.vue")
  },
  {
    path: "/login",
    name: "login",
    component: () => import("../views/Login.vue")
  },
  {
    path: "/inspiration/:id",
    name: "inspiration-detail",
    component: () => import("../views/InspirationDetail.vue")
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
