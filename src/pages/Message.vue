<template>
    <div id="message">
        <el-scrollbar class="head-messages">
            <search-com @search="" @groupCreated="createdGroup"></search-com>
            <div class="head-message" v-for="(message, index) in state.headMessages" :key="index"
                @click="getMessages(message)" :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                    'background-color:rgb(0,155,245);' : ''">
                <img :src="imgSrc(message.contactAvatar)" class="avatar">
                <div class="info">
                    <div class="between">
                        <span class="text-overflow nickname" :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                            'color:white' : ''">{{ message.contactName }}</span>
                        <span class="time" :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                            'color:white' : ''">{{ timeWithoutSeconds(new Date(message.time)) }}</span>
                    </div>
                    <div class="between">
                        <span class="text-overflow content" 
                        :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                            'color:white' : ''">{{ message.content }}</span>
                    </div>
                </div>
            </div>
        </el-scrollbar>
        <div class="empty" v-if="currentHeadMessage == null">
            <img class="background" src="../assets/messages-empty.jpg" />
        </div>
        <div class="messages" v-else>
            <div class="head">
                {{ currentHeadMessage.contactName }}
            </div>
            <el-scrollbar style="height:60%">
                <div class="message" v-for="message in msgPageOpt.data" :key="message.id" ref="messagesContent">
                    <div class="content user" v-if="message.userId == state.user.id && !isGroupMsg(message.contactId)">
                        <span class="time">{{ new Date(message.time).toLocaleString() }}</span>
                        <div class="user-info right">
                            <div class="message-body" v-html="message.content">
                            </div>
                            <img :src="imgSrc(state.user.avatar)" class="avatar">
                        </div>
                    </div>
                    <div class="content contactor"
                        v-if="message.userId != state.user.id && !isGroupMsg(message.contactId)">
                        <span class="time">{{ new Date(message.time).toLocaleString() }}</span>
                        <div class="user-info left">
                            <img :src="imgSrc(message.contactAvatar)" class="avatar">
                            <div class="message-body" v-html="message.content">
                            </div>
                        </div>
                    </div>
                </div>
            </el-scrollbar>
            <div class="edit no-drag">
                <textarea v-model="state.content" class="input" style="" @keydown="keyToSend">
                    </textarea>
                <el-tooltip effect="dark" content="按住ctrl+空格快速送" placement="top">
                    <el-button type="primary" class="send" @click="send">发送</el-button>
                </el-tooltip>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref, onBeforeUnmount } from 'vue';
import webSocket from '../modules/WebSocket';
import { copy, MessageType, PageOption, timeWithoutSeconds } from '../modules/Common';
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
const messagesContent = ref<any>(null);

onMounted(() => {
    webSocket.assignMessageCallback(messageHandle);

    const user = stateStroge.get("user");
    state.user = user;
    getData();
});

const msgPageOpt = ref<PageOption>(new PageOption(1, 15, []));

function messageHandle(event: MessageEvent<any>) {
    const msg = JSON.parse(event.data);
    if (currentHeadMessage.value != null && currentHeadMessage.value.contactId == msg.userId) {
        const { contactName, contactAvatar } = currentHeadMessage.value;
        msg.contactName = contactName;
        msg.contactAvatar = contactAvatar;
        if (msgPageOpt.value.data.length >= msgPageOpt.value.size) {
            msgPageOpt.value.data.splice(0, 1);
            msgPageOpt.value.data.push(msg);
        }
        else
            msgPageOpt.value.data.push(msg);
    }
    const toFresh: any = {};
    copy(msg, toFresh);
    toFresh.userId = msg.contactId;
    toFresh.contactId = msg.userId;
    freshHeadMessage(toFresh);
}

function sendMessage(headMessage: any) {
    const { contactId, contactName, contactAvatar } = headMessage;
    const message: any = {
        userId: state.user.id,
        contactId: contactId,
        time: new Date(),
        content: state.content,
        type: MessageType.common,
        contactName: contactName,
        contactAvatar: contactAvatar
    };
    webSocket.sendMessage(message, () => {
        CreateMessage(message, res => {
            const data = res.data;
            message.id = data;
            if (msgPageOpt.value.data.length >= msgPageOpt.value.size) {
                msgPageOpt.value.data.splice(0, 1);
                msgPageOpt.value.data.push(message);
            }
            else
                msgPageOpt.value.data.push(message);
            state.content = "";
            freshHeadMessage(message);
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

function freshHeadMessage(msg: any) {
    const headMessage: any = {};
    headMessage.userId = msg.userId;
    headMessage.contactId = msg.contactId;
    headMessage.contactAvatar = msg.contactAvatar;
    headMessage.contactName = msg.contactName;
    headMessage.content = msg.content;
    headMessage.time = new Date();
    FreshHeadMessage(headMessage, (res) => {
        const id = res.data;
        const index: number = state.headMessages.findIndex((h: any) => h.id == id);
        if (index < 0) {
            headMessage.id = id;
            state.headMessages.splice(0, 0, headMessage);
        }
        else {
            headMessage.id = id;
            state.headMessages[index] = headMessage;
        }
    });
}

function isGroupMsg(contactId: any): boolean {
    return contactId.includes('G');
}

function keyToSend(event: KeyboardEvent) {
    const key = event.key;

    if (event.ctrlKey) {
        if (key == "Enter")
            sendMessage(currentHeadMessage.value);
    }
}

function send() {
    if (state.content.length == 0) return;
    sendMessage(currentHeadMessage.value);
}

function createdGroup(headMessage: any) {
    state.headMessages.splice(0, 0, headMessage);
}

onBeforeUnmount(() => {

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
    font-size: 18px;
    font-weight: 600;
    text-align: left;
    padding-left: 1%;
}

#message .head-message {
    display: flex;
    width: 100%;
    padding: 2px 3px;
    height: 50px;
    align-items: center;
    background-color: white;
    box-sizing: border-box;
}

.head-message .nickname {
    color: black;
    font-size: 15px;
}

.head-message .info {
    margin-left: 5px;
    width: 100%;
}

.head-message .info .content {
    width: 96%;
    font-size: 13px;
    color: gray;
    text-align: left;
    height: 20px;
}

#message .avatar {
    width: 35px;
    height: 35px;
    border-radius: 50%;
}

#message .head-message .avatar {
    width: 45px;
    height: 45px;
}

#message .content {
    position: relative;
    display: flex;
    flex-flow: column nowrap;
    padding-left: 1%;
}

#message .user {
    align-items: flex-end;
}

#message .contactor {
    align-items: flex-start;
}

#message .user-info {
    display: flex;
    align-items: center;
    height: 32px;
    width: 100%;
}

#message .left {
    justify-content: flex-start;
}

#message .right {
    justify-content: flex-end;
}

#message .content .message-body {
    text-wrap: wrap;
    max-width: 60vw;
    font-size: 14px;
    text-align: left;
    border-radius: 7px;
    height: 20px;
    padding: 1%;
    width: fit-content;
    margin-top: 1%;
}

.user .message-body {
    background: white;
    color: black;
    margin-right: 1%;
}

.contactor .message-body {
    background-color: rgb(0, 125, 225);
    color: white;
    margin-left: 1%;
}

#message .empty {
    height: 100%;
    position: relative;
    width: 66vw;
    background-color: rgb(248, 248, 248);
}

.empty .background {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 240px;
    height: 240px;
}

#message .messages {
    position: relative;
    height: 100vh;
    width: 66vw;
    background-color: rgb(248, 248, 248);
    padding-right: 2%;
}

#message .edit {
    position: absolute;
    bottom: 1px;
    width: 100%;
    background-color: inherit;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
}

.edit .send {
    margin-right: 2%;
    margin-bottom: 1%;
}

#message .edit .input {
    border: none;
    outline: none;
    resize: none;
    display: block;
    width: 99%;
    height: 25vh;
    background-color: rgb(248, 248, 248);
    font-size: 14px;
    font-family: "SimSun", "STSong", "Microsoft YaHei ";
}

#message .user-info .nickname {
    font-size: 13px;
    color: gray;
    width: 10%;
}

#message .messages .time {
    font-size: 11px;
    color: gray;
    width: 100%;
    text-align: center;
}

#message .head-message .time {
    font-size: 12px;
    color: gray;
    margin-right: 5%;
}

#message .head-message:hover {
    background-color: rgba(255, 249, 248, .5);
}
</style>