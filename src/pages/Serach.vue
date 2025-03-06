<template>
    <div id="search">
        <div class="head">
            <el-input v-model="state.identifier" placeholder="群号/（用户账户/用户邮箱）"></el-input>
            <el-button type="primary" @click="search" @keydown="enterToSerach"> </el-button>
        </div>
        <div class="content" v-if="state.data != null && state.data.id.includes('U')">
            <div>
                <div>
                    <img :src="imgSrc(state.data.avatar)" class="avatar" />
                    <span>{{ state.data.nickname }}</span>
                </div>
                <span>{{ state.data.signature }}</span>
            </div>
            <el-button type="primary" size="small" v-if="state.data.labelId == null">添加</el-button>
            <el-button size="small">发送消息</el-button>
        </div>
        <div class="content" v-if="state.data != null && state.data.id.includes('G')">
            <div>
                <div>
                    <img :src="imgSrc(state.data.avatar)" class="avatar" />
                    <span>{{ state.data.name }}</span>
                </div>
                <span>{{ state.data.currentCount }}/{{ state.data.size }}</span>
            </div>
            <el-button type="warning" size="small" v-if="!state.data.isGroup">加入</el-button>
            <el-button size="small">发送消息</el-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive,onMounted } from 'vue';
import { imgSrc } from '../modules/Request';
import { ipcRenderer } from 'electron';

const state = reactive<any>({
    identifier: "",
    data: null
});

onMounted(()=>{
    ipcRenderer.send("setMinBound",850,900);
    ipcRenderer.send("setSize",0,0);
});

</script>

<style scoped>
#search{
    position: relative;
    height: 100vh;
    width: 100vw;
}
</style>