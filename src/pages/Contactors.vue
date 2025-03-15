<template>
   <div id="contactors">
      <div class="head no-drag">
         <search-com></search-com>
         <div class="notifications">
            <div class="item" @click="loadUserApplies"> <span>好友通知</span> <el-icon>
                  <ArrowRight />
               </el-icon></div>
            <div class="item" @click="loadGroupNotices"><span>群通知 </span><el-icon>
                  <ArrowRight />
               </el-icon></div>
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
                     <span class="text-overflow name">{{ state.userId == apply.userId ? apply.contactName :
                        apply.userName
                     }}</span>
                     <span v-if="apply.info != null" class="apply-info text-overflow">{{ apply.info }}</span>
                  </div>
               </div>
               <span v-if="apply.status != 1 || state.userId != apply.contactId" class="status-text">{{ getApplyStatusText(apply.status)
               }}</span>
               <el-dropdown v-if="apply.status == 1 && state.userId == apply.contactId" split-button size="small">
                  <el-text type="success" @click="aggreUserApply(apply)">同意</el-text>
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
            <div class="item" v-for="apply in state.data" :key="apply.groupId">
               <div class="info">
                  <el-image :src="imgSrc(apply.groupAvatar)"></el-image>
                  <div style="display:flex;flex-direction:column">
                     <span class="text-overflow name">{{ apply.groupName }}</span>
                     <span v-if="apply.info != null" class="text-overflow apply-info">{{ apply.info }}</span>
                  </div>
               </div>
               <span v-if="apply.contactStatus == 1 && apply.status != 1"  class="status-text">{{ getApplyStatusText(apply.status) }} </span>
               <el-dropdown v-if="apply.contactStatus == 1 && apply.status == 1" split-button>
                  <template #dropdown>
                     <el-text type="success" @click="aggreUserApply(apply)">同意</el-text>
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
   </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive } from 'vue';
import { GetGroupApplies, GetUserApplies, SetApplyStatus } from '../api/UserApply';
import stateStroge from '../modules/StateStorage';
import { imgSrc } from '../modules/Request';
import { copy, GroupContactStatus, UserApplyStatus } from '../modules/Common';
import  webSocket  from '../modules/WebSocket';
import { IsUserOnline } from '../api/User';
import { CreateHeadMessage, CreateMessage, CreateOfflineMessage } from '../api/ChatMessage';

const state = reactive<any>({
   showUserApply: false,
   showGroupNotice: false,
   showUserInfo: false,
   data: [],
   userId: "",
   friends: {},
   groups: []
});

onMounted(() => {
   const user = stateStroge.get("user");
   state.userId = user.id;
});

function loadUserApplies() {
   state.showUserApply = true;
   state.showGroupNotice = false;
   state.showUserInfo = false;

   GetUserApplies(state.userId, res => {
      state.data = res.data;
   });
}

function loadGroupNotices() {
   state.showUserApply = false;
   state.showGroupNotice = true;
   state.showUserInfo = false;

   GetGroupApplies(state.userId, res => {
      state.data = res.data;
   });
}

function modelFromApply(apply: any) {
   const model: Record<string, any> = {};
   copy(apply, model);
   return model;
}

function aggreUserApply(apply: any) {
   const model = modelFromApply(apply);
   model.status = UserApplyStatus.Accepted;
   const idTo = apply.userId == state.userId? apply.contactId : apply.userId;
   SetApplyStatus(model, () => {
      apply.status = model.status;
      const message = {
         userId: state.userId,
         contactId: idTo,
         content: apply.info,
         time: new Date(),
         type: 1,
         contactAvatar: apply.contactAvatar,
         contactName : apply.contactName
      };
      IsUserOnline(idTo, res => {
         if (res.data)
            CreateMessage(message, () => {
               CreateHeadMessage({
                  userId: state.userId,
                  contactId: idTo,
                  time: message.time,
                  content: apply.info,
                  contactAvatar: apply.contactAvatar,
                  contactName : apply.contactName
               }, () => {
                  webSocket.sendMessage(message, () => { })
               });
            });
         else
            CreateOfflineMessage(message);
      });
   });
}

function refuseUserApply(apply: any) {
   const model = modelFromApply(apply);
   model.status = UserApplyStatus.Refused;
   SetApplyStatus(model, () => {
      apply.status = model.status;
   });
}

function ignoreUserApply(apply: any) {
   const model = modelFromApply(apply);
   model.status = UserApplyStatus.Ignored;
   SetApplyStatus(model, () => {
      apply.status = model.status;
   });
}

function getApplyStatusText(status: any) {
   switch (status) {
      case UserApplyStatus.Verifying: return "等待验证中...";
      case UserApplyStatus.Accepted: return "已同意";
      case UserApplyStatus.Refused: return "已拒绝";
      case UserApplyStatus.Ignored: return "已忽略";
   }
}

function groupStatusText(status: any) {
   switch (status) {
      case GroupContactStatus.KickOut: return "你已被踢出群聊";
      case GroupContactStatus.Dismissed: return "群已解散";
   }
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
   width: 100%;
}

.user-apply .item .apply-info {
   font-size: 13px;
   color: gray;
   width: 100%;
}

.user-apply .item .info .el-image {
   width: 40px;
   height: 40px;
   border-radius: 50%;
   margin-right: 1%;
}

.user-apply .item .status-text{
   color: gray;
   font-size: 13px;
}
</style>