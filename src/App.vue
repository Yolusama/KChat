<script setup lang="ts">
import { onBeforeMount, onMounted} from 'vue';
import { GetAsync } from './modules/Request';
import { oneSecond } from './modules/Common';

type DelayOption = {
  timer?: NodeJS.Timeout,
  expire?:number
}

const option:DelayOption = {expire:oneSecond*60*3};

onMounted(()=>{
   option.timer = setInterval(async()=>{
     await GetAsync("/Api/Common/Heartbeat",{});
   },option.expire);
});

onBeforeMount(()=>{
   clearInterval(option.timer);
});
</script>

<template>
  <RouterView></RouterView>
</template>

<style>
#app {
  width: 100vw;
  overflow: hidden;
  padding: 0;
  max-width: unset;
}

body{
  -webkit-app-region: drag;
}

.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}

.no-drag{
  -webkit-app-region:no-drag;
}

.text-overflow{
  overflow: hidden;
  text-wrap: none;
  text-overflow: ellipsis;
}
</style>
