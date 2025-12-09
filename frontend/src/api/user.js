import request from "@/utils/request";

/**
 * 用户登录
 * @param {object} data - 包含 username 和 password 的对象
 */
export function login(data) {
  return request({
    url: "/users/login",
    method: "post",
    data, // 等同于 data: data
  });
}

/**
 * 用户注册
 * @param {object} data - 包含 username, password, phone, email 的对象
 */
export function register(data) {
  return request({
    url: "/users/register",
    method: "post",
    data,
  });
}
