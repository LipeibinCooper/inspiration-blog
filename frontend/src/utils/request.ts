import axios, {
  AxiosInstance,
  InternalAxiosRequestConfig,
  AxiosResponse
} from "axios";
import { ElMessage } from "element-plus";

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json"
  }
});

// 开发环境下打印请求信息
if (process.env.NODE_ENV === 'development') {
  request.interceptors.request.use(config => {
    console.log('Request:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data,
      params: config.params  // 添加查询参数的输出
    });
    return config;
  });
}

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // console.log('Request Interceptor:', config);
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request Interceptor Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    if (process.env.NODE_ENV === 'development') {
      console.log('d');
      // console.log('Response:', {
      //   status: response.status,
      //   statusText: response.statusText,  // 添加状态文本
      //   data: response.data,
      //   headers: response.headers,
      //   config: {  // 添加请求配置信息
      //     url: response.config.url,
      //     method: response.config.method,
      //     baseURL: response.config.baseURL
      //   }
      // });
    }
    const data = response.data;
    if (data.code == 0) {
      console.error('API Error:', {
        code: data.code,
        message: data.message,
        url: response.config.url,
        requestData: response.config.data,
        responseData: data
      });
      ElMessage.error(data.message || '请求失败');
      return Promise.reject(new Error(data.message || '请求失败'));
    }
    console.log('res', response)
    return response;
  },
  (error) => {
    console.error('Request Error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,  // 添加状态文本
      message: error.message,
      url: error.config?.url,
      method: error.config?.method,  // 添加请求方法
      data: error.response?.data,
      requestData: error.config?.data,  // 添加请求数据
      stack: error.stack  // 添加错误堆栈信息
    });
    const message = error.response?.data?.message || error.message || '请求失败';
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default request;
