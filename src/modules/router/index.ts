import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path:"/",
    redirect:"/Home"
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes:routes
});

export default router;
