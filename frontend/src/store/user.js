import { reactive, computed } from "vue";
import { useRouter } from "vue-router";

// 使用Vue 3的reactive API创建一个响应式的状态对象
const userState = reactive({
  userInfo: JSON.parse(localStorage.getItem("userInfo")) || null,
});

export function useUserStore() {
  const router = useRouter();

  // 计算属性，判断用户是否登录
  const isLoggedIn = computed(() => !!userState.userInfo);

  const isAdmin = computed(
    () => !!userState.userInfo && userState.userInfo.isAdmin === 1
  );
  // 登录方法
  const login = (userData) => {
    userState.userInfo = userData;
    localStorage.setItem("userInfo", JSON.stringify(userData));
  };

  // 退出登录方法
  const logout = () => {
    userState.userInfo = null;
    localStorage.removeItem("userInfo");
    // 退回到登录页
    if (router) {
      router.push("/login");
    }
  };

  return {
    userState,
    isLoggedIn,
    isAdmin,
    login,
    logout,
  };
}
