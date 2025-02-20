<template>
  <div class="profile-info">
    <div class="info-section">
      <h3 class="section-title">个人信息</h3>
      <el-form 
        :model="userForm" 
        label-width="80px"
        class="info-form">
        <div class="avatar-container">
          <el-avatar 
            :size="120" 
            :src="userForm.avatar || userInfo?.avatar"
            @error="() => true"
            class="user-avatar">
            {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <el-upload
            class="avatar-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            :on-change="handleAvatarChange">
            <el-button type="primary" class="change-avatar-btn" round>
              <el-icon><Camera /></el-icon>
              更换头像
            </el-button>
          </el-upload>
        </div>

        <el-form-item label="用户名">
          <el-input 
            v-model="userForm.username"
            placeholder="请输入用户名"
            maxlength="20"
            show-word-limit />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input 
            v-model="userForm.email" 
            disabled
            class="disabled-input" />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleUpdateInfo"
            :loading="updating"
            round>
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="password-section">
      <h3 class="section-title">修改密码</h3>
      <el-form 
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="80px"
        class="password-form">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入原密码" />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码" />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码" />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleUpdatePassword"
            :loading="updatingPassword"
            round>
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useAuthStore } from "@/store/useAuthStore";
import type { PasswordUpdateForm } from '@/types/inspiration';
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { Camera } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const userInfo = computed(() => authStore.userInfo);
const passwordFormRef = ref<FormInstance>();

const updating = ref(false);
const updatingPassword = ref(false);

// 用户信息表单
const userForm = ref({
  username: userInfo.value?.username || '',
  email: userInfo.value?.email || '',
  avatar: ''
});

// 密码表单
const passwordForm = ref<PasswordUpdateForm>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 定义验证规则的类型
interface ValidationRule {
  required?: boolean;
  message?: string;
  trigger?: 'blur' | 'change';
  min?: number;
  validator?: (rule: ValidationRule, value: string, callback: (error?: Error) => void) => void;
}

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: ValidationRule, value: string, callback: (error?: Error) => void) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

interface FileUploadEvent {
  raw: File;
}

// 处理头像上传
const handleAvatarChange = (file: FileUploadEvent) => {
  const reader = new FileReader();
  reader.onload = async (e) => {
    if (e.target?.result) {
      const avatarUrl = e.target.result as string;
      userForm.value.avatar = avatarUrl;
      
      try {
        await authStore.updateUserInfo({
          avatar: avatarUrl
        });
        ElMessage.success('头像更新成功');
      } catch (error) {
        ElMessage.error('头像更新失败');
      }
    }
  };
  reader.readAsDataURL(file.raw);
};

// 更新用户信息
const handleUpdateInfo = async () => {
  updating.value = true;
  try {
    await authStore.updateUserInfo({
      username: userForm.value.username,
      avatar: userForm.value.avatar || userInfo.value?.avatar
    });
    ElMessage.success('个人信息更新成功');
  } catch (error) {
    ElMessage.error('更新失败');
  } finally {
    updating.value = false;
  }
};

// 更新密码
const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return;
  
  updatingPassword.value = true;
  try {
    await passwordFormRef.value.validate();
    await authStore.updatePassword(
      passwordForm.value.oldPassword,
      passwordForm.value.newPassword
    );
    
    ElMessage.success('密码修改成功');
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('密码修改失败');
    }
  } finally {
    updatingPassword.value = false;
  }
};
</script>

<style scoped lang="scss">
.profile-info {
  max-width: 800px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #303133;
}

.info-section,
.password-section {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  
  .user-avatar {
    border: 4px solid #fff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    font-size: 48px;
    background: #409eff;
    
    &:hover {
      transform: scale(1.02);
      transition: transform 0.3s ease;
    }
  }
}

.change-avatar-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  
  .el-icon {
    font-size: 16px;
  }
}

.disabled-input {
  :deep(.el-input__wrapper) {
    background-color: #f5f7fa;
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  
  &:hover {
    box-shadow: 0 0 0 1px #c0c4cc inset;
  }
  
  &.is-focus {
    box-shadow: 0 0 0 1px #409eff inset;
  }
}

.el-button {
  min-width: 100px;
}
</style> 