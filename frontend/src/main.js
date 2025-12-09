import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

// 使用 @ 别名来确保路径解析正确
import "@/assets/main.css";

createApp(App).use(router).mount("#app");
