import { createRouter, createWebHistory } from "vue-router";
import VenueListView from "../views/VenueListView.vue";
import LoginRegisterView from "../views/LoginRegisterView.vue";
import VenueDetailView from "../views/VenueDetailView.vue";
import MyOrdersView from "../views/MyOrdersView.vue";
// ======== 修复：导入 AdminVenueEdit 组件 ========
import AdminVenueEdit from "../views/AdminVenueEdit.vue";

const routes = [
  { path: "/", redirect: "/venues" },
  { path: "/venues", name: "VenueList", component: VenueListView },
  {
    path: "/venue/:id",
    name: "VenueDetail",
    component: VenueDetailView,
    props: true,
  },
  { path: "/login", name: "LoginRegister", component: LoginRegisterView },
  {
    path: "/my-orders",
    name: "MyOrders",
    component: MyOrdersView,
    meta: { requiresAuth: true }, // 添加元信息，表示此路由需要登录
  },
  {
    path: "/about",
    name: "about",
    component: () => import("../views/AboutView.vue"),
  },
  // ======== 新增后台路由 ========
  {
    path: "/admin/venue/new",
    name: "AdminVenueNew",
    component: AdminVenueEdit,
    meta: { requiresAuth: true, requiresAdmin: true }, // 需要登录且是管理员
  },
  {
    path: "/admin/venue/edit/:id",
    name: "AdminVenueEdit",
    component: AdminVenueEdit,
    meta: { requiresAuth: true, requiresAdmin: true }, // 需要登录且是管理员
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 全局前置守卫 (Navigation Guard)
router.beforeEach((to, from, next) => {
  const userInfo = JSON.parse(localStorage.getItem("userInfo"));
  const loggedIn = !!userInfo;
  const isAdmin = loggedIn && userInfo.isAdmin === 1;

  if (to.matched.some((record) => record.meta.requiresAdmin)) {
    // 如果目标路由需要管理员权限
    if (isAdmin) {
      next(); // 是管理员，放行
    } else if (loggedIn) {
      alert("权限不足！");
      next("/venues"); // 已登录但不是管理员，跳回首页
    } else {
      next("/login"); // 未登录，去登录
    }
  } else if (
    to.matched.some((record) => record.meta.requiresAuth) &&
    !loggedIn
  ) {
    // 如果目标路由需要登录，但未登录
    next("/login");
  } else {
    next();
  }
});

export default router;
