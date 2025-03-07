import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [];

routes.push({
  path: "/",
  redirect: "/Login"
});
routes.push({
  path: "/Login",
  name: "Login",
  component: () => import("../../pages/Login.vue")
});
const home:RouteRecordRaw = {
  path: "/Home",
  name: "Home",
  component: () => import("../../pages/Home.vue"),
  children:[]
};
const message:RouteRecordRaw = {
  path:"/Home/Message",
  name:"Message",
  component:()=>import("../../pages/Message.vue")
};
const contactors:RouteRecordRaw = {
  path:"/Home/Contactors",
  name:"Contactors",
  component:()=>import("../../pages/Contactors.vue")
};
home.children.push(message,contactors);
routes.push(home);
routes.push({
  path:"/Search",
  name:"Search",
  component:()=>import("../../pages/SearchPage.vue")
});

const router = createRouter({
  history: createWebHashHistory(),
  routes: routes
});

export default router;
