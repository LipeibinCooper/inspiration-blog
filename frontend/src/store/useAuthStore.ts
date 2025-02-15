import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { User, LoginForm, RegisterForm } from "@/types/inspiration";

export const useAuthStore = defineStore("auth", () => {
  const userInfo = ref<User | null>(null);
  const token = ref<string | null>(null);

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

  const login = async (loginForm: LoginForm) => {
    // 查找注册用户
    const user = registeredUsers.value.find(
      u => u.username === loginForm.username && u.password === loginForm.password
    );

    if (user) {
      userInfo.value = {
        id: user.id,
        username: user.username,
        email: user.email
      };
      token.value = "mock_token_" + user.id;
      localStorage.setItem("token", token.value);
      localStorage.setItem("user", JSON.stringify(userInfo.value));
    } else {
      throw new Error("用户名或密码错误");
    }
  };

  const logout = () => {
    userInfo.value = null;
    token.value = null;
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

  return {
    userInfo,
    token,
    login,
    logout,
    isLoggedIn,
    initAuth,
    register
  };
}); 