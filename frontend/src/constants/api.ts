// API 路径常量
export const API_ROUTES = {
  // 认证相关
  AUTH: {
    LOGIN: "/auth/login",
    REGISTER: "/auth/register",
    PROFILE: "/auth/profile"
  },

  // 灵感相关
  INSPIRATION: {
    LIST: "/inspirations",
    DETAIL: (id: number) => `/inspirations/${id}`,
    CREATE: "/inspirations",
    UPDATE: (id: number) => `/inspirations/${id}`,
    DELETE: (id: number) => `/inspirations/${id}`
  }
};

// HTTP 状态码
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_ERROR: 500
};
