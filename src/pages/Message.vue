<template>
    <div id="message">
       <div class="head-messages">
          <search-com @search=""></search-com>
          <div class="head-message" v-for="(message) in state.headMessages" :key="message.id" @click="getMessages(message)">
          </div>
       </div>
       <div class="messages">
          <div class="message" v-for="message in msgPageOpt.data" :key="message.id">
               <div class="content" v-html="message.content"></div>
               <div class="edit">
                 <el-input v-model="state.content" :rows="4">
                 </el-input>
               </div>
          </div>
       </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive,onMounted,ref } from 'vue';
import { assignMessageCallback } from '../modules/WebSocket';
import { copy, MessageType, PageOption, type ChatMessage, type HeadMessage } from '../modules/Common';
import { CreateMessage, GetHeadMessages, GetMessages } from '../api/ChatMessage';
import stateStroge from '../modules/StateStorage';

const state = reactive<any>({
    headMessages:[],
    user:{},
    content:""
});

onMounted(()=>{
    assignMessageCallback(messageHandle);

    const user = stateStroge.get("user");
    state.user = user;
    getData();
});

const msgPageOpt = ref<PageOption>(new PageOption(1,5,[]));

function messageHandle(event:MessageEvent<any>){
  
}

function messageSend(headMessage:any){
    const {contactId,contactIdName,contactAvatar} = headMessage;
    const message:ChatMessage = {
        userId: state.user.id,
        contactId: contactId,
        time: new Date(),
        content: state.content,
        type: MessageType.common
    };
     CreateMessage(message,res=>{
        const data = res.data;
        const toAdd:any = {};
        copy(message,toAdd);
        toAdd.id = data;
        toAdd.contactId = contactId;
        toAdd.contactIdName = contactIdName;
        toAdd.contactAvatar = contactAvatar;
        if(msgPageOpt.value.current>=msgPageOpt.value.size){
            msgPageOpt.value.data.splice(0,1);
            msgPageOpt.value.data.push(toAdd);
        }
        state.content = "";
     });
}

function getData(){
    GetHeadMessages(state.user.id,res=>{
        state.headMessages = res.data;
    });
}

function getMessages(headMessage:any){
    msgPageOpt.value.current = 1;
    msgPageOpt.value.total = 0;
    
    GetMessages(msgPageOpt.value.current,msgPageOpt.value.size,state.user.id,headMessage.contractId,res=>{
        msgPageOpt.value.data = res.data.data;
        msgPageOpt.value.total = res.data.total;
    });
}


</script>

<style scoped>
#message{
    position: relative; 
    /*calc函数使用需要空格隔开否则易被识别成字符串 */
    width: calc(100% - 50px);
    height: 100%;
}
</style>