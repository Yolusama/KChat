import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './modules/router'
import ElementPlus from 'element-plus'
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import AppHeader from './components/AppHeader.vue';
import SearchComponent from './components/SearchComponent.vue';
import UserApplyDialog from './components/UserApplyDialog.vue'

const app= createApp(App); 

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.component("app-header",AppHeader);
app.component("search-com",SearchComponent);
app.component("apply-dialog",UserApplyDialog);

app.use(router);
app.use(ElementPlus);
app.mount('#app')
