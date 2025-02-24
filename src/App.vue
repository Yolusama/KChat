<script setup lang="ts">
import { onBeforeMount, onMounted } from 'vue';
import { GetAsync } from './modules/Request';
import AppHeader from './components/AppHeader.vue';

const option = {
  timer: 0,
  exipre:3*60*1000
};

onMounted(()=>{
   option.timer = setInterval(async()=>{
     await GetAsync("/Api/Common/Heartbeat",{});
   },option.exipre);
});

onBeforeMount(()=>{
   clearInterval(option.timer);
});
</script>

<template>
  <AppHeader></AppHeader>
  <RouterView></RouterView>
</template>

<style>
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
</style>
