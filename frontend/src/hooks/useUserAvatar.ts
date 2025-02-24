import { onMounted, onUnmounted, ref } from 'vue';

export function useUserAvatar() {
  const forceUpdate = ref(0);

  const handleUserInfoUpdate = () => {
    forceUpdate.value++; // 强制组件重新渲染
  };

  onMounted(() => {
    window.addEventListener('user-info-updated', handleUserInfoUpdate);
  });

  onUnmounted(() => {
    window.removeEventListener('user-info-updated', handleUserInfoUpdate);
  });

  return {
    forceUpdate
  };
} 