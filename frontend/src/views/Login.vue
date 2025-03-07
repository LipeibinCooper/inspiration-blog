<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>欢迎使用灵感笔记</h2>

      <!-- 切换登录/注册的标签页 -->
      <el-tabs v-model="activeTab">
        <el-tab-pane label="登录" name="login">
          <el-form
            ref="formRef"
            :model="loginForm"
            :rules="rules"
            label-width="80px"
            class="login-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="loginForm.username" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="loginForm.password" type="password" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLogin" :loading="loading">
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-position="top"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
              />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                class="submit-button"
                :loading="loading"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { useAuthStore } from "@/store/useAuthStore";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";

// 添加错误类型定义
interface LoginError {
  message: string;
}

export default defineComponent({
  name: "LoginView",
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const route = useRoute();
    const formRef = ref<FormInstance>();
    const registerFormRef = ref<FormInstance>();
    const loading = ref(false);
    const activeTab = ref('login');

    // 登录表单
    const loginForm = ref({
      username: "linjie",
      password: "123456"
    });

    // 注册表单
    const registerForm = ref({
      username: "",
      password: "",
      confirmPassword: ""
    });

    // 登录验证规则
    const rules: FormRules = {
      username: [
        { required: true, message: "请输入用户名", trigger: "blur" }
      ],
      password: [
        { required: true, message: "请输入密码", trigger: "blur" }
      ]
    };

    // 注册验证规则
    const registerRules: FormRules = {
      username: [
        { required: true, message: "请输入用户名", trigger: "blur" },
        { min: 3, max: 20, message: "用户名长度在 3 到 20 个字符", trigger: "blur" }
      ],
      password: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 6, message: "密码不能少于 6 个字符", trigger: "blur" }
      ],
      confirmPassword: [
        { required: true, message: "请确认密码", trigger: "blur" },
        {
          validator: (
            _: unknown,
            value: string,
            callback: (error?: Error | string) => void
          ) => {
            if (value !== registerForm.value.password) {
              callback(new Error("两次输入的密码不一致"));
            } else {
              callback();
            }
          },
          trigger: "blur"
        }
      ]
    };

    // 登录处理
    const handleLogin = async () => {
      if (!formRef.value) return;

      await formRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            await authStore.login(loginForm.value);
            ElMessage.success('登录成功');
            const redirect = route.query.redirect as string;
            console.log(redirect);
            // 跳转到首页
            router.push('/home');
          } catch (error: unknown) {
            const err = error as LoginError;
            ElMessage.error(err.message || "登录失败");
          } finally {
            loading.value = false;
          }
        }
      });
    };

    // 注册处理
    const handleRegister = async () => {
      if (!registerFormRef.value) return;

      await registerFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            await authStore.register({
              username: registerForm.value.username,
              password: registerForm.value.password,
              confirmPassword: registerForm.value.confirmPassword
            });
            ElMessage.success("注册成功，请登录");
            activeTab.value = 'login';
            registerForm.value = {
              username: "",
              password: "",
              confirmPassword: ""
            };
          } catch (error: unknown) {
            const err = error as LoginError;
            ElMessage.error(err.message || "注册失败");
          } finally {
            loading.value = false;
          }
        }
      });
    };

    return {
      activeTab,
      loginForm,
      registerForm,
      rules,
      registerRules,
      formRef,
      registerFormRef,
      loading,
      handleLogin,
      handleRegister
    };
  }
});
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  background-color: #f8f9fa;
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.submit-button {
  width: 100%;
  margin-top: 20px;
}

:deep(.el-tabs__nav) {
  width: 100%;
}

:deep(.el-tabs__item) {
  width: 50%;
  text-align: center;
}
</style>
