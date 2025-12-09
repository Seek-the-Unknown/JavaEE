import { createRouter, createWebHistory } from "vue-router";
import VenueListView from "../views/VenueListView.vue";
import LoginRegisterView from "../views/LoginRegisterView.vue";
import VenueDetailView from "../views/VenueDetailView.vue";
import MyOrdersView from "../views/MyOrdersView.vue";
import AdminVenueEdit from "../views/AdminVenueEdit.vue";
import UserProfileView from "../views/UserProfileView.vue";
import MyPublishedVenues from "../views/MyPublishedVenues.vue";

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

  // 需登录路由
  {
    path: "/my-orders",
    name: "MyOrders",
    component: MyOrdersView,
    meta: { requiresAuth: true },
  },
  {
    path: "/profile",
    name: "UserProfile",
    component: UserProfileView,
    meta: { requiresAuth: true },
  },
  {
    path: "/my-published",
    name: "MyPublished",
    component: MyPublishedVenues,
    meta: { requiresAuth: true },
  },

  // 发布与编辑 (权限由后端和组件内控制)
  {
    path: "/venue/new",
    name: "VenueNew",
    component: AdminVenueEdit,
    meta: { requiresAuth: true },
  },
  {
    path: "/admin/venue/edit/:id",
    name: "VenueEdit",
    component: AdminVenueEdit,
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const userInfo = JSON.parse(localStorage.getItem("userInfo"));
  if (to.meta.requiresAuth && !userInfo) {
    next("/login");
  } else {
    next();
  }
});

export default router;
