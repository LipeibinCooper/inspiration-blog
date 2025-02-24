import axios, {
  AxiosInstance,
  InternalAxiosRequestConfig,
  AxiosResponse
} from "axios";
import { ElMessage } from "element-plus";

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: "http://localhost:3000/api",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json"
  }
});

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data;
  },
  (error) => {
    ElMessage.error(error.response?.data?.message || "请求失败");
    return Promise.reject(error);
  }
);

export default request;
