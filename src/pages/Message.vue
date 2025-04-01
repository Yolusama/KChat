<template>
    <div id="message">
        <el-scrollbar class="head-messages">
            <search-com @search="" @groupCreated="createdGroup"></search-com>
            <div class="head-message" v-for="(message, index) in state.headMessages" :key="index"
                @click.left="getMessages(message)" :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                    'background-color:rgb(0,155,245);' : ''"
                    @click.right="openHeadMsgMenu($event,headMessage)">
                <img :src="imgSrc(message.contactAvatar)" class="avatar">
                <div class="info">
                    <div class="between">
                        <span class="text-overflow nickname" :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                            'color:white' : ''">{{ message.contactName }}</span>
                        <span class="time" :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                            'color:white' : ''">{{ timeStr(new Date(message.time)) }}</span>
                    </div>
                    <div class="between">
                        <span class="text-overflow head-content" :style="currentHeadMessage != null && currentHeadMessage.id == message.id ?
                            'color:white' : ''">{{ isGroupMsg(message.contactId) ? `${message.userName}:
                            ${message.content}` :
                                message.content }}</span>
                    </div>
                </div>
            </div>
        </el-scrollbar>
        <div class="empty" v-if="currentHeadMessage == null">
            <img class="background" src="../assets/messages-empty.jpg" />
        </div>
        <div class="messages" v-else ref="messagesContent">
            <div class="head no-drag">
                <contact-profile :contactId="currentHeadMessage.contactId" palcement="right-end">
                    {{ currentHeadMessage.contactName }}
                </contact-profile>
            </div>
            <el-scrollbar style="height:60%;padding-right: 2%;" class="no-drag" ref="scroll">
                <div class="message" v-for="message in msgPageOpt.data" :key="message.id">
                    <span class="time">{{ new Date(message.time).toLocaleString() }}</span>
                    <div class="content user" v-if="message.userId == state.user.id && !isGroupMsg(message.contactId)">
                        <div class="user-info right">
                            <div class="message-body" v-html="message.content"
                                v-if="message.type == MessageType.common">
                            </div>
                            <el-image class="msg-img" :src="imageMsgDisplay(message)"
                                :preview-src-list="[imageMsgDisplay(message)]" v-if="message.type == MessageType.image"
                                fit="fill">
                                <template #error>
                                    <div class="err-img">
                                        <el-icon :size="24">
                                            <Picture></Picture>
                                        </el-icon>
                                    </div>
                                </template>
                            </el-image>
                            <div v-if="message.type == MessageType.file" class="msg-file no-drag">
                                <el-icon color="rgb(0,75,235)" :size="24" @click="seeFileInFolder(message)">
                                    <Document />
                                </el-icon>
                                <div class="file-info">
                                    <span class="nickname text-overflow">{{ message.fileName }}</span>
                                    <span class="size">{{ getFileSize(message.fileSize) }}&nbsp;
                                        .{{ getFileSuffix(message.fileName) }}</span>
                                </div>
                                <el-icon v-if="message.downloaded" :size="24" @click="seeFileInFolder(message)">
                                    <Folder />
                                </el-icon>
                                <span v-if="expire(message)">已过期</span>
                            </div>
                            <div v-if="message.type == MessageType.video" class="msg-video">
                                <video :src="toPlay(message)" muted="true" class="video" controls
                                    controlslist="nodownload" @loadeddata="scrollBottom"></video>
                                <el-icon v-if="message.downloaded" :size="24" @click="seeFileInFolder(message)">
                                    <Folder />
                                </el-icon>
                            </div>
                            <contact-profile :contactId="state.user.id" placement="left-start">
                                <img :src="imgSrc(state.user.avatar)" class="avatar">
                            </contact-profile>
                        </div>
                    </div>
                    <div class="content contactor"
                        v-if="message.userId != state.user.id && !isGroupMsg(message.contactId)">
                        <div class="user-info left">
                            <contact-profile :contactId="message.userId" placement="right-start">
                                <img :src="imgSrc(message.contactAvatar)" class="avatar">
                            </contact-profile>
                            <el-image class="msg-img" :src="imageMsgDisplay(message)"
                                :preview-src-list="[imageMsgDisplay(message)]" v-if="message.type == MessageType.image"
                                fit="fill">
                                <template #error>
                                    <div class="err-img">
                                        <el-icon :size="24">
                                            <Picture></Picture>
                                        </el-icon>
                                    </div>
                                </template>
                            </el-image>
                            <div v-if="message.type == MessageType.file" class="msg-file">
                                <el-icon color="white" :size="24" @click="seeFileInFolder(message)">
                                    <Document />
                                </el-icon>
                                <div class="file-info">
                                    <span class="nickname text-overflow" style="color:white">{{ message.fileName
                                    }}</span>
                                    <span class="size">{{ getFileSize(message.fileSize) }}&nbsp;
                                        .{{ getFileSuffix(message.fileName) }}</span>
                                </div>
                                <el-icon v-if="!message.downloaded && !expire(message)" :size="24"
                                    @click="downloadFile(message)" color="white">
                                    <Download />
                                </el-icon>
                                <el-icon v-if="message.downloaded" :size="24" color="white"
                                    @click="seeFileInFolder(message)">
                                    <Folder />
                                </el-icon>
                                <span v-if="expire(message.time)" class="size">已过期</span>
                            </div>
                            <div class="message-body" v-html="message.content"
                                v-if="message.type == MessageType.common">
                            </div>
                            <div v-if="message.type == MessageType.video" class="msg-video">
                                <video :src="message.downloaded ? toPlay(message) : fileSrc(message.fileName)" controls
                                    muted="true" class="video" controlslist="nodownload" @loadeddata="scrollBottom">
                                </video>
                                <el-icon v-if="message.downloaded" :size="24" @click="seeFileInFolder(message)"
                                    color="white">
                                    <Folder />
                                </el-icon>
                                <el-icon v-if="!message.downloaded && !expire(message)" :size="24"
                                    @click="downloadFile(message)" color="white">
                                    <Download />
                                </el-icon>
                                <span class="size" v-if="expire(message)">已过期</span>
                            </div>
                        </div>
                    </div>
                    <div class="content group-user"
                        v-if="message.userId == state.user.id && isGroupMsg(message.contactId)">
                        <div class="group-user-self">
                            <span class="nickname text-overflow">{{ state.user.nickname }}</span>
                            <div class="message-body" v-html="message.content"
                                v-if="message.type == MessageType.common"></div>
                            <el-image class="msg-img" :src="imageMsgDisplay(message)"
                                :preview-src-list="[imageMsgDisplay(message)]" v-if="message.type == MessageType.image"
                                fit="fill">
                                <template #error>
                                    <div class="err-img">
                                        <el-icon :size="24">
                                            <Picture></Picture>
                                        </el-icon>
                                    </div>
                                </template>
                            </el-image>
                            <div v-if="message.type == MessageType.video" class="msg-video">
                                <video :src="toPlay(message)" controls muted="true" class="video"
                                    controlslist="nodownload" @loadeddata="scrollBottom"> </video>
                                <el-icon v-if="message.downloaded" :size="24" @click="seeFileInFolder(message)">
                                    <Folder />
                                </el-icon>
                                <span class="size" v-if="expire(message)">已过期</span>
                            </div>
                            <div v-if="message.type == MessageType.file" class="msg-file no-drag">
                                <el-icon color="rgb(0,75,235)" :size="24" @click="seeFileInFolder(message)">
                                    <Document />
                                </el-icon>
                                <div class="file-info">
                                    <span class="nickname text-overflow">{{ message.fileName }}</span>
                                    <span class="size">{{ getFileSize(message.fileSize) }}&nbsp;
                                        .{{ getFileSuffix(message.fileName) }}</span>
                                </div>
                                <el-icon v-if="message.downloaded" :size="24" @click="seeFileInFolder(message)">
                                    <Folder />
                                </el-icon>
                                <span v-if="expire(message.time)">已过期</span>
                            </div>
                        </div>
                        <contact-profile :contactId="message.userId" placement="left-start">
                            <img :src="imgSrc(state.user.avatar)" class="avatar">
                        </contact-profile>
                    </div>
                    <div class="content group-contactor"
                        v-if="message.userId != state.user.id && isGroupMsg(message.contactId)">
                        <contact-profile :contactId="message.userId" placement="right-start">
                            <img :src="imgSrc(state.user.avatar)" class="avatar">
                        </contact-profile>
                        <div class="group-contactor-self">
                            <span class="nickname text-overflow">{{ message.contactName }}</span>
                            <div class="message-body" v-html="message.content"
                                v-if="message.type == MessageType.common">
                            </div>
                            <el-image class="msg-img" :src="imageMsgDisplay(message)"
                                :preview-src-list="[imageMsgDisplay(message)]" v-if="message.type == MessageType.image"
                                fit="fill">
                                <template #error>
                                    <div class="err-img">
                                        <el-icon :size="24">
                                            <Picture></Picture>
                                        </el-icon>
                                    </div>
                                </template>
                            </el-image>
                            <div v-if="message.type == MessageType.video" class="msg-video">
                                <video :src="message.downloaded ? toPlay(message) : fileSrc(message.fileName)" controls
                                    muted="true" class="video" controlslist="nodownload" @loadeddata="scrollBottom">
                                </video>
                                <el-icon v-if="message.downloaded" :size="24" @click="seeFileInFolder(message)"
                                    color="white">
                                    <Folder />
                                </el-icon>
                                <el-icon v-if="!message.downloaded && !expire(message)" :size="24"
                                    @click="downloadFile(message)" color="white">
                                    <Download />
                                </el-icon>
                                <span class="size" v-if="expire(message)">已过期</span>
                            </div>
                            <div v-if="message.type == MessageType.file" class="msg-file">
                                <el-icon color="white" :size="24" @click="seeFileInFolder(message)">
                                    <Document />
                                </el-icon>
                                <div class="file-info">
                                    <span class="nickname text-overflow" style="color:white">{{ message.fileName
                                    }}</span>
                                    <span class="size">{{ getFileSize(message.fileSize) }}&nbsp;
                                        .{{ getFileSuffix(message.fileName) }}</span>
                                </div>
                                <el-icon v-if="!message.downloaded && !expire(message.time)" :size="24"
                                    @click="downloadFile(message)" color="white">
                                    <Download />
                                </el-icon>
                                <el-icon v-if="message.downloaded" :size="24" color="white"
                                    @click="seeFileInFolder(message)">
                                    <Folder />
                                </el-icon>
                                <span v-if="expire(message.time)" class="size">已过期</span>
                            </div>
                        </div>
                    </div>
                </div>
            </el-scrollbar>
            <div class="edit no-drag">
                <div class="send-list">
                    <el-tooltip effect="dark" content="发送图片" placement="top">
                        <label for="pic">
                            <el-icon style="font-size:20px;cursor:pointer">
                                <Picture />
                            </el-icon>
                        </label>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="发送文件" placement="top">
                        <label for="file">
                            <el-icon style="font-size:20px;cursor:pointer"
                                @click="chooseFile({ save: false, open: true, video: false })">
                                <Files />
                            </el-icon>
                        </label>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="发送视频" placement="top">
                        <label for="video">
                            <el-icon style="font-size:20px;cursor:pointer"
                                @click="chooseFile({ save: false, open: true, video: true })">
                                <VideoPlay />
                            </el-icon>
                        </label>
                    </el-tooltip>
                    <input type="file" style="display:none" id="pic" @change="chooseImage" accept="image/*">
                </div>
                <textarea v-model="state.content" class="input" style="" @keydown="keyToSend">
                </textarea>
                <el-tooltip effect="dark" content="按住ctrl+空格快捷发送" placement="top">
                    <el-button type="primary" class="send" @click="send">发送</el-button>
                </el-tooltip>
            </div>
        </div>
        <div class="head-menu no-drag" v-show="headMenu.show" :style="headMenu.style()"
         @mouseleave.stop="headMenu.show=false;">
           <div @click.stop="copyAccount" class="func">复制账号</div>
            <div @click.stop="removeMessages" class="func">移除消息</div>
            <div @click.stop ="" class="func">拉黑TA</div>
            <div @click.stop="" class="func">删除好友</div>
        </div>
        <div class="msg-menu no-drag" v-show="msgMenu.show" :style="msgMenu.style()" @blur="msgMenu.show=false"></div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref, onBeforeUnmount } from 'vue';
import webSocket from '../modules/WebSocket';
import { copy, CurrentHeadMessage, delayToRun, getFileSize, getFileSuffix, MessageType, onlyDate, PageOption, playNotifyAudio, StylePos, timeWithoutSeconds } from '../modules/Common';
import { CreateMessage, FreshHeadMessage, GetCacheFile, GetHeadMessages, GetMessages, RemoveMessages, UpdateFilePath, UploadFile } from '../api/ChatMessage';
import stateStroge from '../modules/StateStorage';
import { fileSrc, imgSrc } from '../modules/Request';
import { ipcRenderer } from 'electron';
import { ElMessage } from 'element-plus';

const state = reactive<any>({
    headMessages: [],
    verifications: [],
    user: {},
    content: "",
    file: {
        name: "",
        size: 0,
        path: null
    }
});

const currentHeadMessage = ref<any>(null);
const messagesContent = ref<any>(null);
const scroll = ref<any>(null);
const today = ref(new Date());
const headMenu = ref<StylePos>(new StylePos());
const msgMenu = ref<StylePos>(new StylePos());

const maxScrollHeight = 2000;

onMounted(() => {
    webSocket.assignMessageCallback(messageHandle);

    const user = stateStroge.get("user");
    state.user = user;
    getData();
});

const msgPageOpt = ref<PageOption>(new PageOption(1, 15, []));

function messageHandle(event: MessageEvent<any>) {
    const msg = JSON.parse(event.data);
    if (currentHeadMessage.value != null && (currentHeadMessage.value.contactId == msg.userId ||
        currentHeadMessage.value.contactId == msg.contactId)) {
        const { contactName, contactAvatar } = currentHeadMessage.value;
        if (!isGroupMsg(msg.contactId)) {
            msg.contactName = contactName;
            msg.contactAvatar = contactAvatar;
            playNotifyAudio();
        }
        else {
            if (msg.userId != state.user.id) {
                playNotifyAudio();
                msg.downloaded = false;
            }
        }
        if (msgPageOpt.value.data.length >= msgPageOpt.value.size) {
            msgPageOpt.value.data.splice(0, 1);
            msgPageOpt.value.data.push(msg);
        }
        else
            msgPageOpt.value.data.push(msg);
        if (msg.type == MessageType.image) {
            const res: any = {};
            GetCacheFile(msg.fileName, res).then(async () => {
                await ipcRenderer.invoke("writeFile", state.user.account, msg.fileName, res.data);
            });
        }
        scrollBottom();
    }
    const toFresh: any = {};
    copy(msg, toFresh);
    if (!isGroupMsg(msg.contactId)) {
        toFresh.userId = msg.contactId;
        toFresh.contactId = msg.userId;
    }
    else
        toFresh.userId = state.user.id;
    freshHeadMessage(toFresh);
    msg.contactName = msg.userName;
}

function sendMessage(headMessage: any, type: Number) {
    const { contactId, contactName, contactAvatar } = headMessage;
    const message: any = {
        userId: state.user.id,
        contactId: contactId,
        time: new Date(),
        content: state.content,
        type: type,
        contactName: contactName,
        contactAvatar: contactAvatar
    };
    if (type != MessageType.common) {
        message.fileName = state.file.name;
        message.fileSize = state.file.size;
        message.filePath = state.file.path;
        if (type == MessageType.image)
            message.content = "图片";
        else if (type == MessageType.video) {
            message.content = "视频";
            message.downloaded = true;
        }
        else {
            message.content = "文件";
            message.downloaded = true;
        }
    }
    if (isGroupMsg(message.contactId)) {
        message.userName = state.user.nickname;
    }
    webSocket.sendMessage(message, () => {
        CreateMessage(message, res => {
            if (!isGroupMsg(message.contactId)) {
                const data = res.data;
                message.id = data;
                if (msgPageOpt.value.data.length >= msgPageOpt.value.size) {
                    msgPageOpt.value.data.splice(0, 1);
                    msgPageOpt.value.data.push(message);
                }
                else
                    msgPageOpt.value.data.push(message);

                freshHeadMessage(message);
                scrollBottom();
            }
            state.content = "";
            if (type != MessageType.common) {
                state.file = {
                    name: "",
                    size: 0,
                    path: null
                };
            }
        });
    });
}

function getData() {
    GetHeadMessages(state.user.id, res => {
        state.headMessages = res.data;

        if (stateStroge.has(CurrentHeadMessage)) {
            const { contactId } = stateStroge.get(CurrentHeadMessage);
            const headMessage = state.headMessages.find((h: any) => h.contactId == contactId);
            getMessages(headMessage);
        }
    });
}

function getMessages(headMessage: any) {
    msgPageOpt.value.current = 1;
    msgPageOpt.value.total = 0;
    currentHeadMessage.value = null;

    GetMessages(msgPageOpt.value.current, msgPageOpt.value.size, state.user.id, headMessage.contactId, res => {
        msgPageOpt.value.data = res.data.data;
        msgPageOpt.value.total = res.data.total;
        delayToRun(() => {
            currentHeadMessage.value = headMessage;
            stateStroge.set(CurrentHeadMessage, { contactId: headMessage.contactId });
        }, 5);
        scrollBottom();
    });
}

function freshHeadMessage(msg: any) {
    const headMessage: any = {};
    headMessage.userId = msg.userId;
    headMessage.contactId = msg.contactId;
    headMessage.contactAvatar = msg.contactAvatar;
    headMessage.contactName = msg.contactName;
    headMessage.content = msg.content;
    headMessage.userName = msg.userName;
    headMessage.time = new Date();
    FreshHeadMessage(headMessage, (res) => {
        const id = res.data;
        let index: number = state.headMessages.findIndex((h: any) => {
            if (isGroupMsg(headMessage.contactId))
                return h.contactId == headMessage.contactId;
            else
                return h.id == id
        });
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
            sendMessage(currentHeadMessage.value, MessageType.common);
    }
}

function send() {
    if (state.content.length == 0) return;
    sendMessage(currentHeadMessage.value, MessageType.common);
}

function createdGroup(headMessage: any) {
    state.headMessages.splice(0, 0, headMessage);
}

function imageMsgDisplay(message: any) {
    if (message.image != undefined)
        return message.image;
    const account = state.user.account;
    const fileName = message.fileName;
    ipcRenderer.invoke("fileExists", account, fileName).then(exists => {
        if (exists) {
            ipcRenderer.invoke("readFile", account, fileName).then(data => {
                const buffer = Buffer.from(data);
                message.image = `data:image/${getFileSuffix(fileName)};base64,${buffer.toString("base64")}`;
            });
        }
        else {
            const data: any = {};
            GetCacheFile(fileName, data).then(() => {
                ipcRenderer.invoke("writeFile", account, fileName, data.data).then(() => {
                    message.image = `data:image/${getFileSuffix(fileName)};base64,${data.data.toString("base64")}`;
                });
            });
        }
    })

    return message.image;
}

function chooseImage(e: any) {
    const file = e.target.files[0];
    UploadFile(file, async (res) => {
        const newFileName = res.data;
        state.file.name = newFileName;
        state.file.size = file.size;

        const data: any = {};
        await GetCacheFile(newFileName, data);
        ipcRenderer.invoke("writeFile", state.user.account, newFileName, data.data).then(() => {
            sendMessage(currentHeadMessage.value, MessageType.image);
        });
    });
}

function chooseFile(mode: any) {
    ipcRenderer.invoke("openDialog", mode).then((path) => {
        ipcRenderer.invoke("readClientFile", path).then(data => {
            const file = new File([data], path, {});
            UploadFile(file, res => {
                state.file.name = res.data;
                state.file.path = path;
                state.file.size = file.size;
                let type = MessageType.file;
                if (mode.video)
                    type = MessageType.video;
                sendMessage(currentHeadMessage.value, type);
            });
        });
    });
}

function expire(message: any) {
    const today = new Date();
    today.setDate(today.getDate() - 7);
    return today.getTime() > message.time;
}

function downloadFile(message: any) {
    ipcRenderer.invoke("openDialog", { save: true, open: false, video: false }).then(async (res) => {
        const filePath = res;
        const data: any = {};
        await GetCacheFile(message.fileName, data);
        ipcRenderer.invoke("writeClientFile", filePath, data.data).then(() => {
            const recordId = message.recordId;
            const model = isGroupMsg(message.contactId) ? { recordId, filePath, contactId: message.contactId } : {
                userId: message.userId, contactId: message.contactId, filePath: filePath, messageId: message.id
            };
            UpdateFilePath(model, () => {
                message.filePath = filePath;
                message.downloaded = true;
            });
        });
    });
}

function seeFileInFolder(message: any) {
    if (!(state.user.id == message.userId || message.downloaded)) return;
    ipcRenderer.invoke("openFileInFolder", message.filePath).then((ok) => {
        if (!ok)
            ElMessage({
                message: "文件已被移除...",
                type: "warning"
            });
    });
}

function toPlay(message: any) {
    if (message.videoUrl == undefined) {
        ipcRenderer.invoke("readClientFile", message.filePath)
            .then(res => {
                const data = res;
                const blob = new Blob([data]);
                message.videoUrl = URL.createObjectURL(blob);
            }).catch(() => {
                ElMessage({
                    message: "视频资源已被删除",
                    type: "warning"
                });
            })
    }
    return message.videoUrl;
}

function timeStr(time: Date) {
    if (onlyDate(time).getTime() == onlyDate(today.value).getTime())
        return timeWithoutSeconds(time);
    if (onlyDate(time).getTime() == new Date(onlyDate(today.value)).setDate(today.value.getDate() - 1))
        return "昨天";
    return time.toLocaleDateString();
}

function scrollBottom() {
    delayToRun(() => {
        scroll.value.setScrollTop(maxScrollHeight);
    }, 50);
}

function openHeadMsgMenu(e:any,headMessage:any){
   headMenu.value.show = true; 
   headMenu.value.x = e.offsetX;
   headMenu.value.y = e.offsetY;
   headMenu.value.headMessage = headMessage;
}

function copyAccount(){
    navigator.clipboard.write(headMenu.value.headMessage.contactAccount);
}

function removeMessages(){
    const contactId = headMenu.value.headMessage.contactId;
    RemoveMessages(state.user.id,contactId,()=>{
        const index = state.headMessages.findIndex((h:any)=>h.contactId==contactId);
        if(index<0){
            ElMessage({
                message:"发生了一个错误！",
                type:"error"
            });
        }
        else
           state.headMessages.splice(index,1);
    });
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

.messages .head:hover {
    background-color: rgba(98%, 98%, 98%, .35);
    cursor: pointer;
}

#message .head-message {
    display: flex;
    cursor: default;
    width: 100%;
    padding: 2px 3px;
    height: 50px;
    align-items: center;
    background-color: white;
    box-sizing: border-box;
}

.head-message .nickname {
    color: black;
    font-size: 13px;
    width: 60%;
    text-align: left;
}

.head-message .info {
    margin-left: 4px;
    width: calc(100% - 45px);
}

.head-message .info .head-content {
    width: 90%;
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
    min-height: 20px;
    padding: 1%;
    width: fit-content;
    margin-top: 1%;
    min-width: 20px;
}

.user .message-body,
.group-user .message-body {
    background: white;
    color: black;
    margin-right: 1%;
}

.contactor .message-body,
.group-contactor .message-body {
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

#message .content .nickname {
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
    width: 40%;
    margin-right: 5%;
}

#message .head-message:hover {
    background-color: rgba(255, 249, 248, .5);
}

#message .group-user,
#message .group-contactor {
    flex-direction: row;
    width: 98%;
}

#message .group-user {
    justify-content: flex-end;
}

#message .group-contactor {
    justify-self: flex-start;
}

#message .group-user-self,
#message .group-contactor-self {
    display: flex;
    flex-direction: column;
    width: 100%;
}

#message .group-user-self {
    align-items: flex-end;
    margin-right: 1%;
}

#message .group-contactor-self {
    align-items: flex-start;
    margin-left: 1%;
}

#message .group-contactor-self .nickname {
    width: 30%;
    text-align: left;
    white-space: nowrap;
}

#message .group-user-self .nickname {
    width: 30%;
    text-align: right;
}

#message .edit .send-list {
    height: 25px;
    display: flex;
    align-items: center;
    width: 100%;
    padding-left: 20px;
}

.send-list label {
    margin-right: 5%;
}

#message .msg-img {
    max-width: 80%;
    max-height: 40vh;
    border-radius: 7px;
    min-width: 30px;
    min-height: 30px;
    display: flex;
}

#message .err-img {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    width: 100%;
    height: 100%;
}

.right .msg-img,
.group-user-self .msg-img {
    margin-right: 1%;
}

#message .msg-file {
    display: flex;
    align-items: center;
    border-radius: 5px;
    width: 250px;
    justify-content: flex-start;
}

.left .msg-img,
.group-contactor-self .msg-img {
    margin-left: 1%;
}

.right .msg-file,
.group-user-self .msg-file {
    margin-right: 1%;
    background-color: white;
}

.left .msg-file,
.group-contactor-self .msg-file {
    margin-left: 1%;
    background-color: rgb(0, 125, 225);
}

#message .message .file-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    width: 80%;
}

#message .file-info .nickname {
    width: 90%;
}

.file-info .size,
.msg-video .size {
    color: gray;
    font-size: 14px;
}

.left .file-info .size,
.group-contactor-self .file-info .size,
.group-contactor-self .msg-video .size {
    color: white;
}

#message .file-info .left .nickname {
    color: white;
}

.msg-file .el-icon,
.msg-video .el-icon {
    cursor: pointer;
}

#message .msg-video {
    display: flex;
    flex-direction: row;
    align-items: center;
    border-radius: 6px;
}

.msg-video .video {
    max-width: 350px;
    max-height: 350px;
}

.left .msg-video,
.group-contactor-self .msg-video {
    background-color: rgb(0, 125, 225);
    margin-left: 1%;
}

.right .msg-video,
.group-user-self .msg-video {
    background-color: white;
    margin-right: 1%;
}

.head-menu,.msg-menu{
  display: flex;
  flex-direction: column;
  align-items: center;
  position: absolute;
  width: 120px;
  z-index:12;
  background-color: rgb(96%,97%,98%);
  font-size: 13px;
  border-radius: 6px;
}

.head-menu .func,.msg-menu .func{
    display: flex;
    height: 25px;
    align-items: center;
    cursor: pointer;
    color: black;
    width: 100%;
    justify-content: center;
}

.head-menu .func:hover,.msg-menu .func:hover{
    background-color: rgb(12,13,15,.35);
}

</style>