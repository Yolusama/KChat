<template>
  <app-header :inLoginCom="false"></app-header>
  <div id="home">
    <el-menu :default-active="route.path" mode="vertical" :router="true" active-text-color="#409eff"
      background-color="#f8f8f8" class="home-menu">
      <el-menu-item v-if="user != null">
        <img class="avatar no-drag" :src="imgSrc(user.avatar)" />
      </el-menu-item>
      <el-menu-item index="/Home/Message">
        <el-icon class="no-drag">
          <ChatLineSquare />
        </el-icon>
      </el-menu-item>
      <el-menu-item index="/Home/Contactors">
        <el-icon class="no-drag">
          <User></User>
        </el-icon>
      </el-menu-item> 
      <el-menu-item>
        <el-icon class="no-drag">
          <Setting />
        </el-icon>
      </el-menu-item>
    </el-menu>
    <router-view></router-view>
  </div>
</template>

<script lang="ts" setup>
import { ipcRenderer } from 'electron';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import stateStroge from '../modules/StateStorage';
import { imgSrc } from '../modules/Request';
import { assignWebSocket, closeWebSocket } from '../modules/WebSocket';

const user = ref<any>(null);

const route = useRoute();

onMounted(() => {
  ipcRenderer.send("setSize", 800, 720, true);
  ipcRenderer.send("setMinBound", 400, 600);

  const stored = stateStroge.get("user");
  user.value = stored;
  assignWebSocket();
}); 

onBeforeUnmount(()=>{
  closeWebSocket();
});

</script>

<style scoped>
#home {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
}

#home .home-menu {
  height: 100%;
  width: 75px;
}

#home .avatar {
  height: 40px;
  width: 40px;
  border-radius: 50%;
}

#home .avatar:hover{
  cursor: pointer;
}
</style>