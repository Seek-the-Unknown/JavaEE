import request from "@/utils/request";

export function login(data) {
  return request({ url: "/users/login", method: "post", data });
}
export function register(data) {
  return request({ url: "/users/register", method: "post", data });
}
// [新增]
export function getUserInfo() {
  return request({ url: "/users/me", method: "get" });
}
