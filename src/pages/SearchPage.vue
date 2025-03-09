<template>
    <app-header :inLoginCom="false" inSearch></app-header>
    <div id="search">
        <div class="head no-drag">
            <el-input v-model="state.identifier" placeholder="群号/（用户账户/用户邮箱)" clearable @change="clearData"
                @keydown="enterToSearch"></el-input>
            <el-button type="primary" @click="search" style="margin:0 5px">搜索</el-button>
        </div>
        <div class="content no-drag" v-if="state.data != null && state.data.id.includes('U')">
            <div class="info">
                <img :src="imgSrc(state.data.avatar)" class="avatar" />
                <div style="display:flex;flex-direction: column;width:90%">
                    <span class="name"><span class="intro">昵称&nbsp;</span> {{ state.data.nickname }}</span>
                    <span v-if="state.data.isFriend"><span class="intro">备注&nbsp;</span> {{ state.data.remark }}
                        <el-icon>
                            <Edit></Edit>
                        </el-icon>
                    </span>
                    <span class="signature text-overflow"><span class="intro">个性签名&nbsp;</span>{{ state.data.signature
                        }}1</span>
                    <span style="font-size:14px"><span class="intro">账号&nbsp;</span>{{ state.data.account }}</span>
                    <span style="font-size:14px"><span class="intro">电子邮箱&nbsp;</span>{{ state.data.email }}</span>
                    <span><span class="intro">地区&nbsp;</span>{{ state.data.area }}</span>
                </div>
            </div>
            <el-button type="primary" size="small" v-if="!state.data.isFriend" @click="toAddFriend">添加为好友</el-button>
            <el-button size="small" v-if="state.isFriend || state.data.user == state.userId">发送消息</el-button>
        </div>
        <div class="content no-drag" v-if="state.data != null && state.data.id.includes('G')">
            <div>
                <div>
                    <img :src="imgSrc(state.data.avatar)" class="avatar" />
                    <span>{{ state.data.name }}</span>
                </div>
                <span class="signature text-overflow">{{ state.data.currentCount }}/{{ state.data.size }}</span>
            </div>
            <el-button type="warning" size="small" v-if="!state.data.isGroup">加入</el-button>
            <el-button size="small" v-else>发送消息</el-button>
        </div>
        <apply-dialog :isGroup="state.data.id.includes('G')" v-if="state.show" ref="apply" :contactId="state.data.id"
            :name="state.data.id.includes('G') ? state.data.name : state.data.nickname" v-model:labels="state.labels"
            :avatar="state.data.avatar" @close="state.show = false"></apply-dialog>
    </div>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref } from 'vue';
import { imgSrc } from '../modules/Request';
import { GetUserLabels, MakeFriends, SearchUser } from '../api/User';
import stateStroge from '../modules/StateStorage';
import { ElMessage } from 'element-plus';
import { nextTick } from 'process';

const state = reactive<any>({
    identifier: "",
    data: null,
    userId: "",
    labelId: 0,
    show: false,
    labels: []
});

const apply = ref<any>(null);

onMounted(() => {
    const user = stateStroge.get("user");
    state.userId = user.id;
    GetUserLabels(user.id, (res) => {
        state.labels = res.data;
        state.labelId = state.labels[0].id;
    });
});

function search() {
    if (state.identifier.trim().length == 0) return;
    SearchUser(state.userId, state.identifier, res => {
        state.data = res.data;
    });
}

function enterToSearch(e: globalThis.KeyboardEvent) {
    if (e.key == "Enter")
        search();
}

function clearData() {
    if (state.identifier.length == 0)
        state.data = null;
}

function toAddFriend() {
    if (state.data.acceptMode == 2) {
        MakeFriends({
            userId: state.userId,
            contactId: state.constactId,
            labelId: state.data.labelId
        }, () => {
            ElMessage({
                message: "已添加为好友...",
                type: "success"
            });
        });
    }
    else {
        state.show = true;
        nextTick(() => {
            apply.value.open();
        });
    }
}
</script>

<style scoped>
#search {
    height: 100vh;
    width: 100vw;
}

#search .head {
    display: flex;
    margin-top: 25px;
    margin-bottom: 7px;
    padding: 0 2px;
    height: 40px;
    align-items: center;
}

#search .content {
    display: flex;
    align-items: center;
    width: 66%;
    margin: 10px auto 0;
    justify-content: center;
    flex-flow: column nowrap;
    padding: 2%;
    height: 72vh;
    text-align: left;
}

#search .content .avatar {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    margin-right: 2%;
}

#search .content .info {
    display: flex;
    align-items: center;
}

#search .content .signature {
    height: 30px;
    color: gray;
    font-size: 14px;
}

#search .content .info span {
    height: 30px;
    line-height: 30px;
    width: 80%;
    text-wrap: nowrap;
}

#search .name {
    font-size: 14px;
}
</style>