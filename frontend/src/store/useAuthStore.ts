import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { User, LoginForm, RegisterForm } from "@/types/inspiration";
import { setToken, removeToken, setUser, removeUser, getUser } from "@/utils/auth";

// 注册用户信息接口
interface RegisteredUser {
  id: number;
  username: string;
  password: string;
  email: string;
  avatar?: string;
}

export const useAuthStore = defineStore("auth", () => {
  const userInfo = ref<User | null>(getUser());
  const token = ref<string | null>(null);

  // 存储注册用户的信息
  const registeredUsers = ref<RegisteredUser[]>([
    {
      username: "admin",
      password: "888888",
      email: "admin@example.com",
      id: 1,
      avatar: undefined
    },
    {
      username: "test",
      password: "888888",
      email: "test@example.com",
      id: 2,
      avatar: undefined
    }
  ]);

  // 检查登录状态
  const isLoggedIn = computed(() => !!userInfo.value);

  // 根据 ID 获取用户信息
  const getUserById = (id: number) => {
    // 如果是当前用户，直接返回 userInfo
    if (userInfo.value?.id === id) {
      return userInfo.value;
    }
    return registeredUsers.value.find(user => user.id === id) || null;
  };

  // 更新用户信息
  const updateUserInfo = (info: Partial<User>) => {
    if (!userInfo.value) return;

    // 更新当前用户信息
    const updatedUserInfo = { ...userInfo.value, ...info };
    userInfo.value = updatedUserInfo;

    // 更新注册用户列表中的信息
    const userIndex = registeredUsers.value.findIndex(u => u.id === userInfo.value.id);
    if (userIndex !== -1) {
      const updatedUser = {
        ...registeredUsers.value[userIndex],
        ...info,
        password: registeredUsers.value[userIndex].password // 保持密码不变
      };
      registeredUsers.value[userIndex] = updatedUser;

      // 强制更新数组引用
      registeredUsers.value = [...registeredUsers.value];
    }

    // 保存到 localStorage
    setUser(updatedUserInfo);

    // 触发更新事件
    window.dispatchEvent(new CustomEvent('user-info-updated', {
      detail: {
        userId: updatedUserInfo.id,
        updates: info
      }
    }));
  };

  const login = async (form: LoginForm) => {
    // 验证用户名和密码
    const user = registeredUsers.value.find(
      u => u.username === form.username && u.password === form.password
    );

    if (!user) {
      throw new Error("用户名或密码错误");
    }

    // 登录成功，设置用户信息
    userInfo.value = {
      id: user.id,
      username: user.username,
      email: user.email,
      avatar: user.avatar
    };

    // 生成并保存 token
    token.value = `mock_token_${user.id}`;
    setToken(token.value);
    setUser(userInfo.value);
  };

  const logout = () => {
    userInfo.value = null;
    token.value = null;
    removeToken();
    removeUser();
  };

  // 注册新用户
  const register = async (registerForm: RegisterForm) => {
    // 检查用户名是否已存在
    if (registeredUsers.value.some(u => u.username === registerForm.username)) {
      throw new Error("用户名已存在");
    }

    // 创建新用户
    const newUser: RegisteredUser = {
      id: registeredUsers.value.length + 1,
      username: registerForm.username,
      password: registerForm.password,
      email: `${registerForm.username}@example.com`,
      avatar: undefined
    };

    // 添加到注册用户列表
    registeredUsers.value.push(newUser);
  };

  // 更新密码
  const updatePassword = async (oldPassword: string, newPassword: string) => {
    if (!userInfo.value) return;

    // 验证原密码
    const userIndex = registeredUsers.value.findIndex(u => u.id === userInfo.value?.id);
    if (userIndex === -1 || registeredUsers.value[userIndex].password !== oldPassword) {
      throw new Error("原密码错误");
    }

    // 更新密码
    registeredUsers.value[userIndex].password = newPassword;
    return Promise.resolve();
  };

  // 更新头像
  const updateAvatar = async (avatarUrl: string) => {
    if (userInfo.value) {
      await updateUserInfo({ avatar: avatarUrl });
    }
  };

  return {
    userInfo,
    token,
    isLoggedIn,
    login,
    logout,
    register,
    getUserById,
    updateUserInfo,
    updatePassword,
    updateAvatar,
    registeredUsers
  };
}, {
  persist: {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    paths: ['userInfo', 'registeredUsers']
  }
}); 