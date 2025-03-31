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
         <el-radio-group v-model="state.view">
            <el-radio-button :value="0" label="好友"></el-radio-button>
            <el-radio-button :value="1" label="群组"></el-radio-button>
         </el-radio-group>
         <el-scroll-bar style="height:78vh;width:100%;">
            <el-collapse class="friends" v-show="state.view == 0">
               <el-collapse-item v-for="(data, label) in state.friends" :key="JSON.parse(label.toString()).id">
                  <template #title>
                     <div class="between">
                        <span style="font-size: 14px;"> {{ JSON.parse(label.toString()).name }}</span>
                        <span style="color:gray">{{ data.length }}</span>
                     </div>
                  </template>
                  <div class="friend" v-for="(friend, index) in data" :key="index" @click="seeContactInfo(friend)">
                     <el-avatar :src="imgSrc(friend.avatar)"></el-avatar>
                     <div style="display:flex;flex-direction: column;margin-left:4px;padding:0 3px">
                        <span class="name text-overflow">{{ friend.remark != null ? friend.nickname + '(' +
                           friend.remark + ')' :
                           friend.nickname }}</span>
                        <span class="signature text-overflow"> {{ friend.signature }} </span>
                     </div>
                  </div>
               </el-collapse-item>
            </el-collapse>
            <el-collapse class="groups" v-show="state.view == 1">
               <el-collapse-item :name="name" v-for="(data, name) in state.groups" :key="name">
                  <template #title>
                     <div class="between">
                        <span style="font-size:14px">{{ name }}</span>
                        <span style="color:gray">{{ data.length }}</span>
                     </div>
                  </template>
                  <div class="group" v-for="(group, index) in data" :key="index" @click="seeContactInfo(group)">
                     <el-avatar :src="imgSrc(group.avatar)"></el-avatar>
                     <span class="name text-oveflow">{{ group.remark != null ? group.name + '(' +
                           group.remark + ')' :
                           group.name }}</span>
                  </div>
               </el-collapse-item>
            </el-collapse>
         </el-scroll-bar>
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
         <div v-if="state.showContactInfo" class="contact no-drag">
            <div class="info">
               <el-image style="width: 100px; height: 100px;border-radius: 50%;" :src="imgSrc(state.data.avatar)"
                  :preview-src-list="[imgSrc(state.data.avatar)]"></el-image>
               <div class="detail">
                  <h3 style="text-align: left;">{{ isGroup(state.data.id) ? state.data.name : state.data.nickname }}
                  </h3>
                  <div v-if="isGroup(state.data.id)">
                     <span>群id&nbsp;{{ state.data.id }}</span>
                  </div>
                  <div v-if="isGroup(state.data.id)"><el-icon><User/></el-icon>&nbsp;
                  {{ state.data.currentCount }}/{{ state.data.size }}</div>
                  <div v-if="!isGroup(state.data.id)">账号&nbsp;{{ state.data.account }}</div>
                  <div v-if="!isGroup(state.data.id)">电子邮箱&nbsp;{{ state.data.email }}</div>
                  <div v-if="!isGroup(state.data.id)">
                     性别 {{ state.data.gender == 0 ? "男" : "女" }}
                  </div>
               </div>
            </div>
            <div class="between">
               <div class="info-head">
                  <el-icon :size="18">
                     <Edit></Edit>
                  </el-icon>
                  <span style="font-size:13px">&nbsp; 备注</span>
               </div>
               <span @click="remarkEdit" v-if="!state.remarkEdit" class="common">
                  {{ state.data.remark == null ? "添加备注" : state.data.remark }}
               </span>
               <el-input v-model="state.contactRemark" @blur="changeRemark" ref="remarkInput"
               autofocus
                v-else size="small" style="width:60%"></el-input>
            </div>
            <div class="between" v-if="!isGroup(state.data.id)">
               <div class="info-head">
                  <el-icon :size="18">
                     <UserFilled />
                  </el-icon>
                  <span style="font-size:13px">&nbsp; 好友标签</span>
               </div>
               <el-select v-model="state.data.labelId" style="width:40%" size="small">
                  <el-option v-for="item in state.labels" :key="item.id" :label="item.name" :value="item.id">
                  </el-option>
               </el-select>
            </div>
            <div class="between" v-if="!isGroup(state.data.id)">
               <div class="info-head">
                  <el-icon :size="18">
                     <EditPen />
                  </el-icon>
                  <span style="font-size:13px">&nbsp;个性签名</span>
               </div>
               <span class="common">
                  {{ state.data.signature }}
               </span>
            </div>
            <div v-else style="width:70%">
               <span @click="descriptionEdit" class="common" style="margin-bottom:5px">
                 群描述信息 
                     </span>
               <el-input v-model="state.contactDescription" type="textarea" resize="none" :rows="5"
                style="margin-bottom: 5px;" @blur="changeDescription"></el-input>
            </div>
            <div class="funcs no-drag">
               <div class="func" v-if="!isGroup(state.data.id)">
                  <el-icon :size="20">
                     <Delete />
                  </el-icon>
                  <span style="font-size:13px">移除好友</span>
               </div>
               <div class="func" v-else>
                  <el-icon :size="20">
                     <Delete />
                  </el-icon>
                  <span style="font-size:13px">解散群聊</span>
               </div>
               <div class="func" v-if="isGroup(state.data.id)&&state.userId!=state.data.ownerId">
                  <el-icon :size="20">
                     <Foleder/>
                  </el-icon>
                  <span style="font-size:13px">退出群聊</span>
               </div>
               <div class="func">
                  <el-icon :size="20">
                     <ChatDotSquare />
                  </el-icon>
                  <span style="font-size:13px">发消息</span>
               </div>
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
import { onMounted, reactive, ref } from 'vue';
import { GetGroupApplies, GetUserApplies, RemoveContactorCache, SetApplyStatus } from '../api/UserApply';
import stateStroge from '../modules/StateStorage';
import { imgSrc } from '../modules/Request';
import { copy, delayToRun, GroupContactStatus, UserApplyStatus, type HeadMessage } from '../modules/Common';
import webSocket from '../modules/WebSocket';
import { CreateLabel, GetFriends, GetUserLabels, IsUserOnline } from '../api/User';
import { CreateHeadMessage, CreateMessage, CreateOfflineMessage, FreshHeadMessage } from '../api/ChatMessage';
import { ElMessageBox } from 'element-plus';
import { ChangeDescription, GetUserGroups } from '../api/UserGroup';
import { nextTick } from 'process';
import { ChangeRemark } from '../api/Common';

const state = reactive<any>({
   showUserApply: false,
   showGroupNotice: false,
   showContactInfo: false,
   data: [],
   userId: "",
   friends: {},
   groups: {},
   notifyOpt: {
      u: false,
      g: false
   },
   labelId: 0,
   labelShow: false,
   labels: [],
   selectedApply: null,
   view: 0,
   contactRemark: "",
   contactDescription:""
});

const remarkInput = ref<any>(null);

onMounted(() => {
   const user = stateStroge.get("user");
   state.userId = user.id;

   webSocket.assignMessageCallback(messageHandle);
   GetUserLabels(user.id, res => {
      state.labels = res.data;
      state.labelId = state.labels[0].id;
   });

   GetFriends(state.userId, res => {
      state.friends = res.data;
   });

   GetUserGroups(state.userId, res => {
      state.groups = res.data;
   });
});

function loadUserApplies() {
   state.showUserApply = true;
   state.showGroupNotice = false;
   state.showContactInfo = false;
   state.notifyOpt.u = false;

   GetUserApplies(state.userId, res => {
      state.data = res.data;
   });
}

function messageHandle(event: MessageEvent<any>) {
   const data = JSON.parse(event.data);
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
   state.showContactInfo = false;
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

         state.friends[state.labelId].push({
            id: apply.userId,
            nickname: apply.contactName,
            avatar: apply.contactAvatar,
            signature: apply.signature
         });
      }
      if (state.showGroupNotice) {
         idTo = apply.groupOwnerId;
         message = {
            userId: apply.contactId,
            contactId: apply.groupId,
            content: apply.info,
            time: new Date(),
            contactAvatar: apply.groupAvatar,
            contactName: apply.groupName
         };
         if (apply.groupOwnerId == state.userId)
            state.groups["我的群"].push({
               id: apply.contactId,
               name: apply.groupName,
               avatar: apply.groupAvatar
            });
         else
            state.groups["加入的群"].push({
               id: apply.contactId,
               name: apply.groupName,
               avatar: apply.groupAvatar
            });
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

function isGroup(contactId: string) {
   return contactId.includes('G');
}

function seeContactInfo(contactor: any) {
   state.showUserApply = false;
   state.showGroupNotice = false;
   state.showContactInfo = true;

   state.data = contactor;
   if(isGroup(state.data.id))
      state.contactDescription = state.data.description;
}

function remarkEdit() {
  state.remarkEdit = true;
  nextTick(()=>{
     remarkInput.value.focus();
   });
}

function descriptionEdit() {
   if (state.userId == state.data.ownerId) state.descriptionEdit = true;
}

function changeRemark(){
   if(state.data.remark == state.contactRemark){
      state.remarkEdit = false;
      return;
   }
   ChangeRemark(state.userId,state.data.id,state.contactRemark,()=>{
      state.data.remark = state.contactRemark;
      state.contactRemark = "";
      state.remarkEdit = false;
   });
}

function changeDescription(){
    ChangeDescription(state.data.id,state.contactDescription,()=>{
       state.data.description = state.contactDescription;
       state.contactDescription = "";
       state.descriptionEdit = false;
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

#contactors .friends,
#contactors .groups {
   width: 98%;
   margin: 0 auto;
   margin-top: 12px;
}

.friends .friend {
   display: flex;
   align-items: center;
   height: 50px;
   padding-left: 1%;
   cursor: default;
}

.friends .friend:hover,
.groups .group:hover {
   background-color: rgb(248, 248, 248);
}

.groups .group {
   display: flex;
   align-items: center;
   justify-content: flex-start;
   padding-left: 1%;
   height: 50px;
   cursor: default;
}

.friend .signature {
   display: block;
   height: 20px;
}

#contactors .friend .name {
   font-size: 16px;
}

#contactors .group .name {
   font-size: 16px;
   margin-left: 5px;
   max-width: 85%;
}

#contactors .contact {
   display: flex;
   flex-flow: column nowrap;
   align-items: center;
   justify-content: center;
   padding-top: 5vh;
   width: 63vw;
   height: 65vh;
}

.contact .info {
   display: flex;
   align-items: center;
   justify-content: center;
   width: 100%;
}

.contact .info .detail {
   display: flex;
   font-size: 13px;
   flex-flow: column nowrap;
   justify-content: flex-start;
   align-items: flex-start;
   color: gray;
   margin-left: 1%;
}

.contact .between {
   align-items: center;
   width: 80%;
}

.contact .funcs {
   display: flex;
   justify-content: center;
   align-items: center;
}

.funcs .func {
   display: flex;
   flex-direction: column;
   align-items: center;
   margin-left: 7px;
}

.func:hover {
   color: rgb(0, 145, 235);
   cursor: pointer;
}

.contact .common {
   font-size: 13px;
}

.contact .info-head {
   display: flex;
   align-items: center;
   height: 30px;
}
</style>