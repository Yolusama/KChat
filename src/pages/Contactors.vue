<template>
   <div id="contactors">
      <div class="head no-drag">
         <search-com></search-com>
         <div class="notifications">
            <el-badge is-dot :hidden="!state.notifyOpt.u" style="width:100%" :offset="[-20, 5]">
               <div class="item" @click="loadUserApplies"> <span>好友通知</span> <el-icon>
                     <ArrowRight />
                  </el-icon></div>
            </el-badge>
            <el-badge is-dot :hidden="!state.notifyOpt.g" style="width:100%">
               <div class="item" @click="loadGroupNotices"><span>群通知 </span><el-icon>
                     <ArrowRight />
                  </el-icon></div>
            </el-badge>
         </div>
      </div>
      <div class="content">
         <div class="user-apply" v-if="state.showUserApply">
            <div class="item no-drag" v-for="(apply, index) in state.data" :key="index">
               <div class="info">
                  <el-image :src="imgSrc(apply.contactAvatar)" :preview-src-list="[imgSrc(apply.contactAvatar)]"
                     :preview-teleported="true" v-if="state.userId == apply.userId"></el-image>
                  <el-image :src="imgSrc(apply.userAvatar)" :preview-src-list="[imgSrc(apply.userAvatar)]"
                     :preview-teleported="true" v-else></el-image>
                  <div style="display:flex;flex-direction:column">
                     <div style="display:flex">
                        <span class="text-overflow name">{{ state.userId == apply.userId ? apply.contactName :
                           apply.userName
                        }}</span>
                        <span class="time">{{ new Date(apply.time).toLocaleString() }}</span>
                     </div>
                     <span v-if="apply.info != null" class="apply-info text-overflow">{{ apply.info }}</span>
                  </div>
               </div>
               <span v-if="apply.status != 1 || state.userId != apply.contactId" class="status-text">{{
                  getApplyStatusText(apply)
               }}</span>
               <el-dropdown v-if="apply.status == 1 && state.userId == apply.contactId" split-button size="small">
                  <el-text type="success" @click="state.labelShow = true; state.selectedApply = apply;">同意</el-text>
                  <template #dropdown>
                     <el-dropdown-menu>
                        <el-dropdown-item> <el-button type="danger" size="small"
                              @click="refuseUserApply(apply)">拒绝</el-button>
                        </el-dropdown-item>
                        <el-dropdown-item><el-button type="warning" size="small"
                              @click="ignoreUserApply(apply)">忽略</el-button>
                        </el-dropdown-item>
                     </el-dropdown-menu>
                  </template>
               </el-dropdown>
            </div>
         </div>
         <div class="user-apply" v-if="state.showGroupNotice">
            <div class="item" v-for="apply in state.data" :key="apply.groupId" style="height:fit-content">
               <div class="info">
                  <el-image :src="imgSrc(apply.contactAvatar)"></el-image>
                  <div style="display:flex;flex-direction: column;">
                     <div style="display:flex">
                        <span class="text-overflow name">{{ apply.contactName }}</span>
                        <span class="time">{{ new Date(apply.time).toLocaleString() }}</span>
                     </div>
                     <span v-if="apply.info != null" class="text-overflow apply-info">
                        {{ apply.info }}
                     </span>
                     <div class="user-contact"> <span class="apply-in">申请加入群</span>
                        <el-image style="width: 25px; height: 25px;border-radius:50% "
                           :src="imgSrc(apply.groupAvatar)"></el-image>
                        <span class="name no-drag">{{ apply.groupName }}</span>
                     </div>
                  </div>
               </div>
               <span v-if="apply.contactStatus == 1 && (apply.status != 1 || apply.groupOwnerId != state.userId)"
                  class="status-text">{{
                     getApplyStatusText(apply)
                  }} </span>
               <el-dropdown v-if="apply.contactStatus == 1 && apply.status == 1 && apply.groupOwnerId == state.userId"
                  split-button class="no-drag" size="small">
                  <el-text type="success" @click="aggreUserApply(apply)">同意</el-text>
                  <template #dropdown>
                     <el-dropdown-menu split-button size="small">
                        <el-dropdown-item> <el-button type="danger" size="small"
                              @click="refuseUserApply(apply)">拒绝</el-button>
                        </el-dropdown-item>
                        <el-dropdown-item><el-button type="warning" size="small"
                              @click="ignoreUserApply(apply)">忽略</el-button>
                        </el-dropdown-item>
                     </el-dropdown-menu>
                  </template>
               </el-dropdown>

               <span v-if="apply.contactStatus != 1">{{ groupStatusText(apply.contactStatus) }}</span>
            </div>
         </div>
      </div>
      <el-dialog v-model="state.labelShow" @close="state.labelId = 0; state.selectedApply = null;">
         <div style="width:30vw">
            <el-select v-model="state.labelId">
               <el-option v-for="item in state.labels" :key="item.id" :label="item.name" :value="item.id">
               </el-option>
            </el-select>
            <div @click="addLabel">
               <el-icon>
                  <Plus />
               </el-icon>添加分组
            </div>
            <el-button type="primary" @click="aggreUserApply(state.selectedApply)">确定</el-button>
            <el-button type="info" @click="state.labelShow = false">取消</el-button>
         </div>
      </el-dialog>
   </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive } from 'vue';
import { GetGroupApplies, GetUserApplies, RemoveContactorCache, SetApplyStatus } from '../api/UserApply';
import stateStroge from '../modules/StateStorage';
import { imgSrc } from '../modules/Request';
import { copy, delayToRun, GroupContactStatus, UserApplyStatus, type HeadMessage } from '../modules/Common';
import webSocket from '../modules/WebSocket';
import { CreateLabel, GetUserLabels, IsUserOnline } from '../api/User';
import { CreateHeadMessage, CreateMessage, CreateOfflineMessage, FreshHeadMessage } from '../api/ChatMessage';
import { ElMessageBox } from 'element-plus';

const state = reactive<any>({
   showUserApply: false,
   showGroupNotice: false,
   showUserInfo: false,
   data: [],
   userId: "",
   friends: {},
   groups: [],
   notifyOpt: {
      u: false,
      g: false
   },
   labelId: 0,
   labelShow: false,
   labels: [],
   selectedApply: null
});

onMounted(() => {
   const user = stateStroge.get("user");
   state.userId = user.id;

   webSocket.assignMessageCallback(messageHandle);
   GetUserLabels(user.id, res => {
      state.labels = res.data;
      state.labelId = state.labels[0].id;
   });
});

function loadUserApplies() {
   state.showUserApply = true;
   state.showGroupNotice = false;
   state.showUserInfo = false;
   state.notifyOpt.u = false;

   GetUserApplies(state.userId, res => {
      state.data = res.data;
   });
}

function messageHandle(event: MessageEvent<any>) {
   const data = JSON.parse(event.data);
   console.log(data);
   if (data.isVerification == undefined || !data.isVerification) return;
   const index = data.contactId.indexOf('G');
   if (index >= 0)
      state.notifyOpt.g = true;
   else
      state.notifyOpt.u = true;
   if (state.showUserApply || state.showGroupNotice) {
      const apply = state.data.find((a: any) => a.applyId == data.applyId);
      apply.status = data.applyStatus;

      if (apply.status == UserApplyStatus.Accepted)
         freshHeadMessage(data);
   }
}

function freshHeadMessage(data: any) {
   const headMsg: HeadMessage = {
      userId: data.contactId,
      contactId: data.userId,
      content: data.content,
      time: data.time
   };
   FreshHeadMessage(headMsg, () => { });
}

function loadGroupNotices() {
   state.showUserApply = false;
   state.showGroupNotice = true;
   state.showUserInfo = false;
   state.notifyOpt.g = false;

   GetGroupApplies(state.userId, res => {
      state.data = res.data;
   });
}

function modelFromApply(apply: any) {
   const model: Record<string, any> = {};
   if (state.showUserApply) {
      copy(apply, model);
      model.contactLabelId = state.labelId;
   }
   if (state.showGroupNotice) {
      model.userId = apply.contactId;
      model.contactId = apply.groupId;
      model.groupOwnerId = apply.groupOwnerId;
      model.info = apply.info;
   }
   return model;
}

function aggreUserApply(apply: any) {
   const model = modelFromApply(apply);
   model.status = UserApplyStatus.Accepted;
   let idTo: any;
   if (state.showUserApply)
      idTo = apply.userId == state.userId ? apply.contactId : apply.userId;
   if (state.showGroupNotice)
      idTo = apply.groupId;
   SetApplyStatus(model, () => {
      apply.status = model.status;
      let message: Record<string, any>;
      if (state.showUserApply) {
         message = {
            userId: state.userId,
            contactId: idTo,
            content: apply.info,
            time: new Date(),
            type: 1,
            contactAvatar: apply.contactAvatar,
            contactName: apply.contactName
         };
      }
      if (state.showGroupNotice)
         {
            idTo = apply.groupOwnerId;
            message = {
               userId: apply.contactId,
               contactId: apply.groupId,
               content:apply.info,
               time:new Date(),
               contactAvatar: apply.groupAvatar,
               contactName: apply.groupName
            };
         }
      IsUserOnline(idTo, res => {
         if (res.data)
            CreateMessage(message, () => {
               CreateHeadMessage({
                  userId: message.userId,
                  contactId: message.contactId,
                  time: message.time,
                  content: apply.info,
                  contactAvatar: message.contactAvatar,
                  contactName: message.contactName
               }, () => {
                  if (state.showGroupNotice) {
                     delayToRun(() => RemoveContactorCache(idTo, true, () => sendApplyMessage(apply)), 5);
                  }
                  if (state.showUserApply)
                     RemoveContactorCache(idTo, true, () => sendApplyMessage(apply))
               });
            });
         else
            CreateOfflineMessage(message);
      });
      if (state.selectedApply != null)
         state.labelShow = false;
   });
}

function refuseUserApply(apply: any) {
   const model = modelFromApply(apply);
   model.status = UserApplyStatus.Refused;
   SetApplyStatus(model, () => {
      apply.status = model.status;
      sendApplyMessage(apply);
   });
}

function ignoreUserApply(apply: any) {
   const model = modelFromApply(apply);
   model.status = UserApplyStatus.Ignored;
   SetApplyStatus(model, () => {
      apply.status = model.status;
   });
}

function getApplyStatusText(apply: any) {
   const status = apply.status;
   const selfRequest = apply.userId == state.userId;
   switch (status) {
      case UserApplyStatus.Verifying: return "等待验证中...";
      case UserApplyStatus.Accepted: return selfRequest ? "已通过" : "已同意";
      case UserApplyStatus.Refused: return selfRequest ? "对方拒绝了你的好友请求" : "已拒绝";
      case UserApplyStatus.Ignored: return selfRequest ? "等待对方验证..." : "已忽略";
   }
}

function groupStatusText(status: any) {
   switch (status) {
      case GroupContactStatus.KickOut: return "你已被踢出群聊";
      case GroupContactStatus.Dismissed: return "群已解散";
   }
}

function sendApplyMessage(apply: any) {
   const data = {
      applyId: apply.applyId,
      applyStatus: apply.status,
      isVerification: true,
      contactId: apply.userId,
      contactName: apply.contactName,
      contactAvatar: apply.contactAvatar,
      userId: state.userId,
      content: apply.info,
      time: new Date()
   };
   webSocket.sendMessage(data, () => { });
}

function addLabel() {
   ElMessageBox.prompt("新建分组", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
   })
      .then((value) => {
         CreateLabel(state.userId, value, (res) => {
            const index = state.labels.findIndex((l: any) => l.id == res.data.id);
            if (index < 0)
               state.labels.push(res.data);
         });
      })
      .catch(() => {
         return;
      });
}
</script>

<style scoped>
#contactors {
   position: relative;
   height: 100vh;
   width: calc(100% - 50px);
   display: flex;
}

#contactors .notifications {
   list-style: none;
   display: flex;
   flex-flow: column nowrap;
   justify-content: center;
   align-items: center;
   margin: 12px auto 0;
   width: 100%;
}

#contactors .notifications .item {
   display: flex;
   justify-content: space-between;
   align-items: center;
   height: 30px;
   font-size: 13px;
   width: 100%;
   padding: 0 2%;
   box-sizing: border-box;
}

#contactors .notifications .item:hover {
   cursor: pointer;
   background-color: white;
}

#contactors .head {
   width: 220px;
   background-color: aliceblue;
   height: 100%;
}

#contactors .user-apply {
   display: flex;
   flex-flow: column nowrap;
   align-items: center;
   justify-content: center;
   padding-top: 5vh;
   width: 63vw;
}

#contactors .user-apply .item {
   display: flex;
   align-items: center;
   position: relative;
   justify-content: space-between;
   padding: 1%;
   height: 50px;
   width: 98%;
   box-sizing: border-box;
   background-color: rgb(97%, 98%, 98%);
   border-radius: 7px;
}

.user-apply .item .info {
   display: flex;
   width: 40%;
}

.user-apply .item .el-text {
   font-size: 14px;
}

.user-apply .item .name {
   font-size: 14px;
   width: 60px;
}

.user-apply .item .apply-info {
   font-size: 13px;
   color: gray;
   width: 100%;
   text-align: left;
}

.user-apply .item .info .el-image {
   width: 40px;
   height: 40px;
   border-radius: 50%;
   margin-right: 1%;
}

.user-apply .item .status-text {
   color: gray;
   font-size: 13px;
   position: absolute;
   right: 5%;
}

.user-apply .item .time {
   font-size: 13px;
   color: gray;
   width: 88px;
   text-wrap: nowrap;
   margin-left: 10%;
}

.user-apply .user-contact {
   display: flex;
   align-items: center;
}

.user-apply .user-contact .apply-in {
   display: block;
   font-size: 13px;
   margin-right: 2%;
   text-wrap: nowrap;
}

.user-apply .item .user-contact .name {
   text-align: left;
   margin-left: 1%;
   color: rgb(0, 125, 235);
   cursor: pointer;
}
</style>