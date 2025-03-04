<template>
    <div id="message">
       <div class="head-messages">
          <div class="head-message" v-for="(message) in state.headMessages" :key="message.id">
          </div>
       </div>
       <div class="messages">
          <div class="message" v-for="message in state.messages" :key="message.id">
               <div class="content" v-html="message.content"></div>
          </div>
       </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';


const state = reactive<any>({
    headMessages:[],
    messages:[]
});


</script>

<style scoped>
#message{
    position: relative; 
    /*calc函数使用需要空格隔开否则易被识别成字符串 */
    width: calc(100% - 50px);
    height: 100%;
}
</style>