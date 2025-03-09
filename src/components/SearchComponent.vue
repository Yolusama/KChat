<template>
  <div class="search-com">
    <el-input size="small" :prefix-icon="Search" placeholder="搜索" @input="onInput" v-model="content"></el-input>
    <el-dropdown class="add">
        <el-icon :size="15">
          <Plus />
        </el-icon>
        <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="openSearch"><el-icon :size="15"><Connection /></el-icon>加好友/群聊</el-dropdown-item>
          <el-dropdown-item><el-icon :size="15"><ChatDotSquare /></el-icon>创建群聊</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { Search } from "@element-plus/icons-vue";
import { ipcRenderer } from 'electron';

const content = ref<string>();
const emits = defineEmits(["search"]);

function onInput() {
  const query = content.value;
  emits("search", query);
}

function openSearch(){
  ipcRenderer.send("openSearch");
}

</script>

<style scoped>
.search-com {
  position: relative;
  padding: 4px;
  -webkit-app-region: no-drag;
  display: flex;
  align-items: center;
}

.search-com .add{
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 4px;
  background-color: white;
  border-radius: 5px;
  height: 25px;
  width: 25px;
}
</style>