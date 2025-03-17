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
            <img class="background" src="../assets/messages-empty.jpg" />
        </div>
        <div class="messages" v-else>
            <div class="head">
            </div>
            <div class="message" v-for="message in msgPageOpt.data" :key="message.id" ref="messagesContent">
                <div class="content"  v-if="message.userId == state.user.id">
                    <div class="user-info right">
                        <img :src="imgSrc(state.user.avatar)" class="avatar">
                        <span class="text-overflow nickname">{{ state.user.nickname }}</span>
                        <span>{{ new Date(message.time).toLocaleString() }}</span>
                    </div>
                    <div class="message-body" v-html="message.content">
                    </div>
                </div>
                <div class="content left"  v-else>
                    <div class="user-info left">
                        <img :src="imgSrc(message.avatar)" class="avatar">
                        <span class="text-overflow nickname">{{ message.nickname }}</span>
                        <span>{{ new Date(message.time).toLocaleString() }}</span>
                    </div>
                    <div class="message-body" v-html="message.content">
                    </div>
                </div>
                <div class="edit no-drag">
                    <textarea v-model="state.content" class="input" style="">
                    </textarea>
                    <el-tooltip effect="dark" content="按住ctrl+空格快速送" placement="top">
                        <el-button type="primary" class="send">发送</el-button>
                    </el-tooltip>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref, onBeforeUnmount } from 'vue';
import webSocket from '../modules/WebSocket';
import { copy, MessageType, PageOption, type ChatMessage} from '../modules/Common';
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
const meesagesContent = ref<any>(null);

onMounted(() => {
    webSocket.assignMessageCallback(messageHandle);

    const user = stateStroge.get("user");
    state.user = user;
    getData();
});

const msgPageOpt = ref<PageOption>(new PageOption(1, 15, []));

function messageHandle(event: MessageEvent<any>) {
    const msg = JSON.parse(event.data);
    if (currentHeadMessage.value!=null && currentHeadMessage.value.contactId == msg.contactId) {
        const { contactName, contactAvatar } = currentHeadMessage.value;
        msg.contactName = contactName;
        msg.contactAvatar = contactAvatar;
        if (msgPageOpt.value.current < msgPageOpt.value.size) {
            msgPageOpt.value.data.splice(0, 1);
            msgPageOpt.value.data.push(msg);
        }
    }
    const toFresh:any = {};
    copy(msg,toFresh);
    toFresh.userId = msg.contactId;
    toFresh.contactId = msg.userId;
    freshHeadMessage(toFresh);
}

function sendMessage(headMessage: any) {
    const { contactId, contactName, contactAvatar } = headMessage;
    const message: ChatMessage = {
        userId: state.user.id,
        contactId: contactId,
        time: new Date(),
        content: state.content,
        type: MessageType.common
    };
    webSocket.sendMessage(message, () => {
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
            getData();
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

    GetMessages(msgPageOpt.value.current, msgPageOpt.value.size, state.user.id, headMessage.contactId, res => {
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
        headMessage.contactAvatar = msg.contactAvatar;
        headMessage.contactName = msg.contentName;
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
    
});
</script>

<style scoped>
#message {
    position: relative;
    /*calc函数使用需要空格隔开否则易被识别成字符串 */
    width: calc(100% - 50px);
    height: 100vh;
    display: flex;
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
    background-color: white;
}

#message .avatar{
    width: 35px;
    height: 35px;
    border-radius: 50%;
}

#message .content{
    position: relative;
    display: flex;
    flex-flow:column nowrap;
}

#message .user-info{
    display: flex;
    align-items: center;
    height: 32px;
}

#message .left{
    justify-content: flex-start;
}

#message .right{
    justify-content: flex-end;
}

#message .message-body{
    position: absolute;
    text-wrap: wrap;
    max-width: 60vw;
    font-size: 14px;
    text-align: left;
    border-radius: 7px;
    top:3px;
}

.left .message-body{
    background: white;
    color:black;
    left:5px
}

.right .message-body{
    background-color: rgb(0,125,225);
    color: white;
    right: 6px;
}

#message .empty{
    height: 100%;
    position: relative;
    width:66vw;
    background-color: rgb(248,248,248);
}

.empty .background{
    position: absolute;
    left:50%;
    top:  50%;
    transform: translate(-50%,-50%);
    width: 240px;
    height: 240px;
}

#message .messages{
    position: relative;
    height: 100vh;
    width:68vw;
    background-color: rgb(248,248,248);
    padding-right: 2%;
}

#message .edit{
    position: absolute;
    bottom: 1px;
    width: 100%;
}

.edit .send{
    position: absolute;
    right: 26px;
    bottom: 5px;
    z-index: 2;
}

#message .edit .input{
    border: none;
    outline: none;
    resize: none;
    display: block;
    width: 98%;
    height: 25vh;
    /*background-color: rgb(248,248,248);*/
    font-size: 14px;
    font-family: "SimSun"  ;
}
</style>