<template>
  <div class="login">
    <h2>登录</h2>
    <el-form class="login-form" :model="loginForm" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="loginForm.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="loginForm.password" type="password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive } from "vue";

export default defineComponent({
  name: "LoginView",
  setup() {
    const loginForm = reactive({
      username: "",
      password: ""
    });

    const handleLogin = () => {
      // 后续添加登录逻辑
    };

    return {
      loginForm,
      handleLogin
    };
  }
});
</script>

<style scoped>
.login {
  padding: 20px;
}

.login-form {
  max-width: 400px;
  margin: 20px auto;
}
</style>
