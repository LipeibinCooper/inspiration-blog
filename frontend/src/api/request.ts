import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';

// 扩展 AxiosRequestConfig 接口以包含 retryCount
interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  retryCount?: number;
}

// 定义响应数据接口
interface ApiResponse {
  data: any;
  message: string;
  code: number;
}

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
});

// 请求拦截器
request.interceptors.request.use(
  (config: CustomAxiosRequestConfig) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data;
  },
  async (error: AxiosError<ApiResponse>) => {
    if (error.response?.status === 401) {
      router.push('/login');
    } else if (error.code === 'ERR_NETWORK') {
      ElMessage.error('无法连接到服务器，请确保后端服务已启动');
      
      // 简单的重试逻辑
      const config = error.config as CustomAxiosRequestConfig;
      if (config && config.retryCount === undefined) {
        config.retryCount = 0;
      }

      if (config && config.retryCount < 3) {
        config.retryCount++;
        await new Promise(resolve => setTimeout(resolve, config.retryCount * 1000));
        return request(config);
      }
    } else {
      ElMessage.error(error.response?.data?.message || '请求失败');
    }
    return Promise.reject(error);
  }
);

export default request; 