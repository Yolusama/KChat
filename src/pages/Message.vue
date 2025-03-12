<template>
    <div id="message">
        <div class="head-messages">
            <search-com @search=""></search-com>
            <div class="head-message" v-for="(message) in state.headMessages" :key="message.id"
                @click="getMessages(message)">
                <img :src="imgSrc(message.contactAvatar)" class="avatar">
                <div>
                    <div class="info">
                        <span class="text-overflow nickname">{{ message.contactName }}</span>
                        <span class="text-overflow content">{{ message.content }}</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="empty" v-if="currentHeadMessage == null">
            <el-image style="width: 100px; height: 100px" src="../assets/messages-empty.jpg" fit="fit"></el-image>
        </div>
        <div class="messages" v-else-if="!currentHeadMessage.isVerification">
            <div class="head">
            </div>
            <div class="message" v-for="message in msgPageOpt.data" :key="message.id">
                <div class="content">
                    <div class="user-info" v-if="message.userId == state.user.id">
                        <img :src="imgSrc(state.user.avatar)" class="avatar">
                        <span class="text-overflow nickname">{{ state.user.nickname }}</span>
                        <span>{{ new Date(message.time).toLocaleString() }}</span>
                    </div>
                    <div class="user-info" v-else>
                        <img :src="imgSrc(message.avatar)" class="avatar">
                        <span class="text-overflow nickname">{{ message.nickname }}</span>
                        <span>{{ new Date(message.time).toLocaleString() }}</span>
                    </div>
                    <div class="message-body" v-html="message.content">
                    </div>
                </div>
                <div class="edit">
                    <el-input v-model="state.content" :rows="4">
                    </el-input>
                </div>
            </div>
        </div>
        <div v-else-if="currentHeadMessage.isVerification" class="verifications">

        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref, onBeforeUnmount } from 'vue';
import { assignMessageCallback, sendMessage } from '../modules/WebSocket';
import { copy, MessageType, PageOption, type ChatMessage, type HeadMessage } from '../modules/Common';
import { CreateMessage, FreshHeadMessage, GetHeadMessages, GetMessages } from '../api/ChatMessage';
import stateStroge from '../modules/StateStorage';
import { imgSrc } from '../modules/Request';

const state = reactive<any>({
    headMessages: [],
    verifications: [],
    user: {},
    content: ""
});

const currentHeadMessage = ref<any>(null);

onMounted(() => {
    assignMessageCallback(messageHandle); 

    const user = stateStroge.get("user");
    state.user = user;
    getData();
});

const msgPageOpt = ref<PageOption>(new PageOption(1, 5, []));

function messageHandle(event: MessageEvent<any>) {
    const msg = event.data;
    if (currentHeadMessage.value!=null && currentHeadMessage.value.contactId == msg.contactId) {
        const { contactName, contactAvatar } = currentHeadMessage.value;
        msg.contactName = contactName;
        msg.contactAvatar = contactAvatar;
        if (msgPageOpt.value.current < msgPageOpt.value.size) {
            msgPageOpt.value.data.splice(0, 1);
            msgPageOpt.value.data.push(msg);
        }
    }
    const toUpdate:any = {};
    copy(msg,toUpdate);
    const temp = toUpdate.userId;
    toUpdate.userId = toUpdate.contactId;
    toUpdate.contactId = temp;
    freshHeadMessage(toUpdate);
}

function messageSend(headMessage: any) {
    const { contactId, contactName, contactAvatar } = headMessage;
    const message: ChatMessage = {
        userId: state.user.id,
        contactId: contactId,
        time: new Date(),
        content: state.content,
        type: MessageType.common
    };
    sendMessage(message, () => {
        CreateMessage(message, res => {
            const data = res.data;
            const toAdd: any = {};
            copy(message, toAdd);
            toAdd.id = data;
            toAdd.contactIdName = contactName;
            toAdd.contactAvatar = contactAvatar;
            if (msgPageOpt.value.current < msgPageOpt.value.size) {
                msgPageOpt.value.data.splice(0, 1);
                msgPageOpt.value.data.push(toAdd);
            }
            state.content = "";
            freshHeadMessage(toAdd);
        });
    });
}

function getData() {
    GetHeadMessages(state.user.id, res => {
        state.headMessages = res.data;
    });
}

function getMessages(headMessage: any) {
    msgPageOpt.value.current = 1;
    msgPageOpt.value.total = 0;

    GetMessages(msgPageOpt.value.current, msgPageOpt.value.size, state.user.id, headMessage.contractId, res => {
        msgPageOpt.value.data = res.data.data;
        msgPageOpt.value.total = res.data.total;
    });

    currentHeadMessage.value = headMessage;
}

function freshHeadMessage(msg:any){
    const headMessage:any = {};
    if(currentHeadMessage.value!=null){
         copy(currentHeadMessage.value,headMessage);
         headMessage.content = msg.content;
         headMessage.time = new Date();
    }
    else{
        headMessage.userId = msg.userId;
        headMessage.contactId = msg.contactId;
        headMessage.content = msg.content;
        headMessage.time = new Date();
    }
    FreshHeadMessage(headMessage,(res)=>{
        const id = res.data;
        const index:number = state.headMessages.findIndex((h: any)=>h.id==id);
        if(index<0){
            headMessage.id = id;
            state.headMessages.splice(0,0,headMessage);
        }
        else
            state.headMessages[index] = headMessage;
    });
}

onBeforeUnmount(()=>{
   assignMessageCallback(null);
});

</script>

<style scoped>
#message {
    position: relative;
    /*calc函数使用需要空格隔开否则易被识别成字符串 */
    width: calc(100% - 50px);
    height: 100vh;
}

#message .head-messages {
    position: relative;
    min-width: 10vw;
    width: 25vw;
    max-width: 35vw;
    background-color: aliceblue;
    height: 100vh;
    -webkit-app-region: no-drag;
}

#message .messages .head {

    
    height: 30px;
    width: 100%;
}

#message .head-message {
    display: flex;
    width: 100%;
    padding: 2px 3px;
    height: 50px;
    align-items: center;
}
</style>