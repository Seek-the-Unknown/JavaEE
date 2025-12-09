import axios from "axios";

const service = axios.create({
  baseURL: "http://localhost:8888/api",
  timeout: 10000,
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // ======== 确保这段逻辑存在且正确 ========
    // 每次请求都尝试从 localStorage 获取用户信息
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    // 如果用户信息存在，就在请求头中添加 userId
    if (userInfo && userInfo.id) {
      config.headers["userId"] = userInfo.id;
    }
    return config;
  },
  (error) => {
    console.log(error);
    return Promise.reject(error);
  }
);

// 响应拦截器 (保持不变)
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200) {
      console.error("API Logic Error: ", res.message);
      return Promise.reject(new Error(res.message || "Error"));
    } else {
      return res.data;
    }
  },
  (error) => {
    console.log("err" + error);
    let message = "发生未知错误";

    if (error.response) {
      if (error.response.data && error.response.data.message) {
        message = error.response.data.message;
      } else {
        switch (error.response.status) {
          case 400:
            message = "请求参数错误";
            break;
          case 401:
            message = "未授权，请登录";
            break;
          case 403:
            message = "权限不足，拒绝访问";
            break;
          case 404:
            message = "请求地址不存在";
            break;
          case 500:
            message = "服务器内部错误";
            break;
          default:
            message = `连接错误 ${error.response.status}`;
        }
      }
    } else if (error.message.includes("timeout")) {
      message = "请求超时，请检查网络连接";
    }

    return Promise.reject(new Error(message));
  }
);

export default service;
