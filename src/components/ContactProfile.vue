<template>
   <el-popover trigger="click" :placement="placement" width="250" @show="getContactor">
      <template #reference>
         <slot></slot>
      </template>
      <div class="profile" v-if="contactor != undefined">
         <div style="display:flex;align-items: center;">
            <el-image :src="imgSrc(contactor.avatar)" class="avatar" :preview-src-list="[imgSrc(contactor.avatar)]" />
            <div class="info" v-if="!isGroup(contactor.id)">
               <div class="nickname">
                  <span class="text-overflow">{{ contactor.nickname }}</span>
                  <img src="../assets/male.png" class="icon-image" v-if="contactor.gender == 0">
                  <img src="../assets/female.png" class="icon-image" v-else>
               </div>
               <div class="id">
                  uid&nbsp; {{ contactor.id }}
               </div>
               <div class="account">
                  账号&nbsp; {{ contactor.account }}
               </div>
            </div>
            <div class="info" v-else>
               <div class="name">
                  {{ contactor.name }}({{ contactor.currentCount }}人)
               </div>
               <div class="id">
                  群id &nbsp; {{ contactor.id }}
               </div>
            </div>
         </div>
         <div v-if="contactor.remark != null" class="between">
            <span>备注</span>
            <span>{{ contactor.remark }}</span>
         </div>
         <div v-if="contactor.signature != null && contactor.signature.length > 0" class="between">
            <span>个性签名</span>
            <span> {{ contactor.signature }}</span>
         </div>
         <div v-if="contactor.area != null" class="between">
            <span>地区</span>
            <span>
               {{ contactor.area }}
            </span>
         </div>
         <div class="btns">
            <el-button size="small" v-if="!isGroup(contactId)&&!contactor.isFriend" @click="">加好友</el-button>
            <el-button size="small" v-if="isGroup(contactId)&&!contactor.userJoined" @click="">申请加群</el-button>
            <el-button type="primary" plain size="small" @click="toSendMessage"
               style="">发消息</el-button>
         </div>
      </div>
   </el-popover>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { imgSrc } from '../modules/Request';
import { GetUserInfo } from '../api/User';
import stateStroge from '../modules/StateStorage';
import { SearchGroup } from '../api/UserGroup';
import { ToSendMessage } from '../api/ChatMessage';
import { CurrentHeadMessage } from '../modules/Common';
import { Route } from '../modules/Route';


const props = defineProps({
   contactId: String,
   placement: {
      type: String,
      default: "right-start"
   }
});

const contactId = ref<any>(props.contactId);
const placement = ref<any>(props.placement);
const contactor = ref<any>();

function getContactor() {
   const user = stateStroge.get("user");
   if (!isGroup(contactId.value)) {
      if (user.id == contactId.value) {
         contactor.value = user;
         contactor.value.isFriend = true;
         return;
      }
      GetUserInfo(user.id, contactId.value, res => {
         contactor.value = res.data;
      });
   }
   else {
      SearchGroup(user.id, contactId.value, res => contactor.value = res.data);
   }
}

function isGroup(contactId: string) {
   return contactId.includes('G');
}

function toSendMessage() {
   const user = stateStroge.get("user");
   ToSendMessage(user.id, contactId.value, () => {
      stateStroge.set(CurrentHeadMessage, { contactId: contactId.value });
      const messagePath = "/Home/Message";
      Route.switch("#" + messagePath);
   });
}
</script>

<style>
.profile {
   position: relative;
   width: 100%;
   height: fit-content;
   display: flex;
   flex-direction: column;
   align-items: center;
}

.profile .id {
   font-size: 14px;
   color: red;
}

.profile .account {
   color: gray;
   font-size: 13px;
}

.profile .avatar {
   width: 60px;
   height: 60px;
   border-radius: 50%;
   margin-right: 3px;
}

.profile .icon-image {
   width: 15px;
   height: 15px;
   margin-left: 2px;
}

.profile .btns{
   display: flex;
   align-items: center;
   justify-content: center;
   height: 40px;
   margin-top: 10px;
}
</style>