<template>
  <div class="profile-info">
    <!-- 头像部分 -->
    <div class="info-section avatar-section">
      <h3>头像</h3>
      <div class="avatar-wrapper">
        <el-avatar :size="100" :src="authStore.userInfo?.avatar" />
        <el-upload
          class="avatar-uploader"
          action="/api/upload"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <el-button type="primary">更换头像</el-button>
        </el-upload>
      </div>
    </div>

    <!-- 用户名部分 -->
    <div class="info-section">
      <h3>用户名</h3>
      <div v-if="!isEditingUsername" class="display-row">
        <span>{{ authStore.userInfo?.username }}</span>
        <el-button type="text" @click="startEditingUsername">
          <el-icon><Edit /></el-icon>
          修改
        </el-button>
      </div>
      <div v-else class="edit-row">
        <el-input
          v-model="editingUsername"
          placeholder="输入新的用户名"
          maxlength="20"
        />
        <div class="actions">
          <el-button @click="cancelEditUsername">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveUsername" 
            :loading="savingUsername"
          >
            保存
          </el-button>
        </div>
      </div>
    </div>

    <!-- 个人简介部分 -->
    <div class="info-section">
      <h3>个人简介</h3>
      <div v-if="!isEditingBio" class="display-row">
        <p class="bio">{{ authStore.userInfo?.bio || '这个人很懒，什么都没写' }}</p>
        <el-button type="text" @click="startEditingBio">
          <el-icon><Edit /></el-icon>
          修改
        </el-button>
      </div>
      <div v-else class="edit-row">
        <el-input
          v-model="editingBio"
          type="textarea"
          :rows="3"
          placeholder="写一写你的个人简介..."
          maxlength="200"
          show-word-limit
        />
        <div class="actions">
          <el-button @click="cancelEditBio">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveBio" 
            :loading="savingBio"
          >
            保存
          </el-button>
        </div>
      </div>
    </div>

    <!-- 密码部分 -->
    <div class="info-section">
      <h3>密码</h3>
      <div class="display-row">
        <span>********</span>
        <el-button type="text" @click="showPasswordDialog = true">
          <el-icon><Edit /></el-icon>
          修改
        </el-button>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="400px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="updatePassword"
            :loading="updatingPassword"
          >
            确认修改
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.profile-info {
  padding: 32px;
  background: #fff;
  border-radius: 12px;
  
  .info-section {
    padding: 24px 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    
    &:last-child {
      border-bottom: none;
    }
    
    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #1d1d1f;
      margin: 0 0 16px;
    }
    
    .display-row {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      
      .bio {
        flex: 1;
        margin: 0;
        font-size: 14px;
        color: #6e6e73;
        line-height: 1.6;
      }
    }
    
    .edit-row {
      .actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        margin-top: 12px;
      }
    }
  }
  
  .avatar-section {
    .avatar-wrapper {
      display: flex;
      align-items: center;
      gap: 24px;
    }
  }
}
</style>

<script lang="ts" setup>
import { ref } from 'vue';
import { useAuthStore } from '@/store/useAuthStore';
import { Edit } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, UploadProps } from 'element-plus';

const authStore = useAuthStore();

// 用户名编辑状态
const isEditingUsername = ref(false);
const editingUsername = ref('');
const savingUsername = ref(false);

// 个人简介编辑状态
const isEditingBio = ref(false);
const editingBio = ref('');
const savingBio = ref(false);

// 密码修改状态
const showPasswordDialog = ref(false);
const updatingPassword = ref(false);
const passwordFormRef = ref<FormInstance>();
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 添加适当的类型定义
interface ValidationRule {
  required?: boolean;
  message?: string;
  trigger?: string;
  validator?: (rule: ValidationRule, value: string, callback: (error?: Error) => void) => void;
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
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

// 头像上传相关方法
const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.url) {
    authStore.updateAvatar(response.url);
  }
};

const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }
  return true;
};

// 用户名编辑相关方法
const startEditingUsername = () => {
  editingUsername.value = authStore.userInfo?.username || '';
  isEditingUsername.value = true;
};

const cancelEditUsername = () => {
  isEditingUsername.value = false;
  editingUsername.value = '';
};

const saveUsername = async () => {
  if (!editingUsername.value.trim()) return;
  
  try {
    savingUsername.value = true;
    await authStore.updateUserInfo({ username: editingUsername.value });
    isEditingUsername.value = false;
    ElMessage.success('用户名修改成功');
  } catch (error) {
    console.error('修改用户名失败:', error);
    ElMessage.error('修改失败，请重试');
  } finally {
    savingUsername.value = false;
  }
};

// 个人简介编辑相关方法
const startEditingBio = () => {
  editingBio.value = authStore.userInfo?.bio || '';
  isEditingBio.value = true;
};

const cancelEditBio = () => {
  isEditingBio.value = false;
  editingBio.value = '';
};

const saveBio = async () => {
  try {
    savingBio.value = true;
    await authStore.updateUserBio(editingBio.value);
    isEditingBio.value = false;
    ElMessage.success('个人简介更新成功');
  } catch (error) {
    console.error('更新个人简介失败:', error);
    ElMessage.error('更新失败，请重试');
  } finally {
    savingBio.value = false;
  }
};

// 密码修改方法
const updatePassword = async () => {
  if (!passwordFormRef.value) return;
  
  try {
    await passwordFormRef.value.validate();
    updatingPassword.value = true;
    
    await authStore.updatePassword(
      passwordForm.value.oldPassword,
      passwordForm.value.newPassword
    );
    
    showPasswordDialog.value = false;
    ElMessage.success('密码修改成功');
    
    // 清空表单
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  } catch (error) {
    console.error('修改密码失败:', error);
    ElMessage.error('修改失败，请检查原密码是否正确');
  } finally {
    updatingPassword.value = false;
  }
};
</script> 