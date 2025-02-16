import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { User, LoginForm, RegisterForm } from "@/types/inspiration";

interface ApiUser {
  id: number;
  username: string;
  email: string;
}

export const useAuthStore = defineStore("auth", () => {
  const userInfo = ref<User | null>(null);
  const token = ref<string>("");

  // 存储注册用户的信息
  const registeredUsers = ref([
    {
      username: "admin",
      password: "888",
      email: "admin@example.com",
      id: 1
    },
    {
      username: "test",
      password: "888",
      email: "test@example.com",
      id: 2
    }
  ]);

  const login = async (form: LoginForm) => {
    // 模拟登录
    const user: ApiUser = {
      id: form.username === "admin" ? 1 : 2,
      username: form.username,
      email: `${form.username}@example.com`
    };

    userInfo.value = {
      id: user.id,
      username: user.username
    };
    token.value = "mock_token_" + user.id;
    localStorage.setItem("token", token.value);
  };

  const logout = () => {
    userInfo.value = null;
    token.value = "";
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  };

  // 检查登录状态
  const isLoggedIn = computed(() => !!userInfo.value);

  // 初始化状态
  const initAuth = () => {
    const savedToken = localStorage.getItem("token");
    const savedUser = localStorage.getItem("user");
    if (savedToken && savedUser) {
      token.value = savedToken;
      userInfo.value = JSON.parse(savedUser);
    }
  };

  const register = async (registerForm: RegisterForm) => {
    // 检查用户名是否已存在
    if (registeredUsers.value.some(u => u.username === registerForm.username)) {
      throw new Error("用户名已存在");
    }

    // 创建新用户
    const newUser = {
      id: registeredUsers.value.length + 1,
      username: registerForm.username,
      password: registerForm.password,
      email: `${registerForm.username}@example.com` // 生成默认邮箱
    };

    // 添加到注册用户列表
    registeredUsers.value.push(newUser);
    return Promise.resolve();
  };

  // 根据 ID 获取用户信息
  const getUserById = (id: number) => {
    return registeredUsers.value.find(user => user.id === id) || null;
  };

  return {
    userInfo,
    token,
    login,
    logout,
    isLoggedIn,
    initAuth,
    register,
    getUserById
  };
}); 