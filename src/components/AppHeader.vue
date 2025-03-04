<template>
   <div id="app-header">
       <el-icon @click="minimize" :size="16">
         <Minus/>
       </el-icon>
       <el-icon @click="maximize" v-if="!isMaximized&&!inLoginCom" :size="16"><FullScreen /></el-icon>
       <img src="../assets/fullscreen.png" class="image" @click="maximize" v-if="isMaximized&&!inLoginCom">
       <el-icon @click="close" :size="16"><Close /></el-icon>
   </div>
</template>

<script setup lang="ts">
import { ipcRenderer } from "electron"
import { onMounted,ref } from "vue";

const isMaximized = ref<boolean>(false);

onMounted(()=>{
  
});

defineProps({
   inLoginCom:Boolean
});

function minimize(){
  ipcRenderer.send("minimize");
}

function maximize(){
   isMaximized.value = !isMaximized.value;
   if(isMaximized.value)
      ipcRenderer.send("maximize");
   else
      ipcRenderer.send("restoreSize");
}

function close(){
   ipcRenderer.send("close");
}
</script>

<style scoped>
#app-header{
   position: absolute;
   height: 30px;
   display: flex;
   align-items: center;
   justify-content: space-between;
   top:0;
   right: 0.5vw;
   -webkit-app-region:no-drag;
   width: fit-content;
   z-index: 255;
}

#app-header .image{
   width: 16px;
   height: 16px;
   margin-left: 10px;
}

#app-header .el-icon{
   margin-left: 10px;
}

#app-header .el-icon:hover{
   cursor: pointer;
   background-color: rgb(235,236,235,.75);
}

</style>