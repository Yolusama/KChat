<template>
   <div class="search-com">
    <el-input size="small" :prefix-icon="Search" placeholder="搜索" @input="onInput" v-model="content"></el-input>
   </div>
</template>

<script lang="ts" setup>
import { ref} from 'vue';
import { Search } from "@element-plus/icons-vue";

const content = ref<string>();
const emits = defineEmits(["search"]);

function onInput(){
    const query = content.value;
    emits("search",query);
}

</script>

<style scoped>
  .search-com{
    position: relative;
    padding: 4px;
    -webkit-app-region:no-drag;
  }
</style>