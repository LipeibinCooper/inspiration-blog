import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import { useAuthStore } from "@/store/useAuthStore";
import UserProfile from '@/views/UserProfile.vue'

// 路由配置
const routes: Array<RouteRecordRaw> = [
  {
    path: "/home",
    redirect: "/"
  },
  {
    path: "/",
    name: "home",
    component: () => import("../views/HomeView.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/login",
    name: "login",
    component: () => import("../views/Login.vue")
  },
  {
    path: "/inspiration/:id",
    name: "inspiration-detail",
    component: () => import("../views/InspirationDetail.vue"),
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: "/profile/:id?",
    name: "profile",
    component: () => import("../views/ProfileView.vue"),
    redirect: { name: 'my-inspirations' },
    children: [
      {
        path: "info",
        name: "profile-info",
        component: () => import("../views/profile/ProfileInfo.vue")
      },
      {
        path: "inspirations",
        name: "my-inspirations",
        component: () => import("../views/profile/MyInspirations.vue")
      },
      {
        path: "history",
        name: "browse-history",
        component: () => import("../views/profile/BrowseHistory.vue")
      },
      {
        path: "likes",
        name: "my-likes",
        component: () => import("../views/profile/MyLikes.vue")
      },
      {
        path: "collections",
        name: "my-collections",
        component: () => import("../views/profile/MyCollections.vue")
      },
      {
        path: "comments",
        name: "my-comments",
        component: () => import("../views/profile/MyComments.vue")
      }
    ],
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: UserProfile,
    meta: {
      requiresAuth: true
    }
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!authStore.token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      });
    } else {
      if (!authStore.userInfo) {
        try {
          await authStore.getCurrentUser();
        } catch (error) {
          next('/login');
          return;
        }
      }
      next();
    }
  } else {
    next();
  }
});

export default router;
