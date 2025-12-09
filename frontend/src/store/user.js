import { reactive, computed } from "vue";
import { getUserInfo } from "@/api/user";

const userState = reactive({
  userInfo: JSON.parse(localStorage.getItem("userInfo")) || null,
});

const isLoggedIn = computed(() => !!userState.userInfo);
const isAdmin = computed(() => userState.userInfo?.isAdmin === 1);

const login = (userInfo) => {
  userState.userInfo = userInfo;
  localStorage.setItem("userInfo", JSON.stringify(userInfo));
};

const logout = () => {
  userState.userInfo = null;
  localStorage.removeItem("userInfo");
};

// [新增] 刷新用户信息
const fetchUserInfo = async () => {
  if (!isLoggedIn.value) return;
  try {
    const res = await getUserInfo();
    // 合并最新数据（主要是余额）
    login({ ...userState.userInfo, ...res });
  } catch (error) {
    console.error("刷新用户信息失败", error);
  }
};

export function useUserStore() {
  return {
    userState,
    isLoggedIn,
    isAdmin,
    login,
    logout,
    fetchUserInfo, // [必须导出]
  };
}
