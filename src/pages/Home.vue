<template>
  <app-header :inLoginCom="false"></app-header>
  <div id="home">
    <el-menu default-active="/Home/Message" mode="vertical" :router="true" active-text-color="#409eff"
      background-color="#f8f8f8" class="home-menu">
      <el-menu-item v-if="user != null">
        <img class="avatar no-drag" :src="imgSrc(user.avatar)" />
      </el-menu-item>
      <el-menu-item index="/Home/Message" @click="unreadOpt.message = false">
        <el-badge is-dot :hidden="!unreadOpt.message" :offset="[10, 20]">
          <el-icon class="no-drag">
            <ChatLineSquare />
          </el-icon>
        </el-badge>
      </el-menu-item>
      <el-menu-item index="/Home/Contactors" @click="unreadOpt.apply = false">
        <el-badge is-dot :hidden="!unreadOpt.apply" :offset="[10, 20]">
          <el-icon class="no-drag">
            <User></User>
          </el-icon>
        </el-badge>
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
import stateStroge from '../modules/StateStorage';
import { imgSrc } from '../modules/Request';
import sse from '../modules/SSE';
import { Route } from '../modules/Route';
import webSocket from '../modules/WebSocket';

const user = ref<any>(null);
const unreadOpt = ref<any>({
  apply: false,
  message: false
});

onMounted(() => {
  ipcRenderer.send("setSize", 800, 720, true);
  ipcRenderer.send("setMinBound", 400, 600);

  const stored = stateStroge.get("user");
  user.value = stored;
  ipcRenderer.send("userLogan", user.value.id, user.value.token);
  sse.assign(user.value.id);
  sse.assignMessageCallback(messageCallback);
  webSocket.assign(user.value.id,user.value.token);
  Route.dive("#/Home/Message");

  /*ipcRenderer.invoke("testRead","src/modules/WebSocket.ts").then(data=>{
      ipcRenderer.invoke("testWrite","src/test/ws.ts",data);
  });*/
});


function messageCallback(event: MessageEvent<any>) {
  const data = JSON.parse(event.data);
  unreadOpt.value[data.key] = data["value"];
}



onBeforeUnmount(() => {
  sse.close();
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

#home .home-menu .el-menu-item {
  display: flex;
  justify-content: center;
}

#home .avatar {
  height: 40px;
  width: 40px;
  border-radius: 50%;
}

#home .avatar:hover {
  cursor: pointer;
}
</style>