<template>
  <div class="search-com">
    <el-input size="small" :prefix-icon="Search" placeholder="搜索" @input="onInput" v-model="content"></el-input>
    <el-dropdown class="add">
      <el-icon :size="15">
        <Plus />
      </el-icon>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="openSearch"><el-icon :size="15">
              <Connection />
            </el-icon>加好友/群聊</el-dropdown-item>
          <el-dropdown-item @click="openGroupEditor"><el-icon :size="15">
              <ChatDotSquare />
            </el-icon>创建群聊</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <el-dialog v-model="state.groupEditShow" @close="clearGroupModel" class="no-drag">
      <el-form label-position="right" label-width="auto" style="margin-top:15px">
        <el-form-item label="群名称">
          <el-input v-model="state.group.name"></el-input>
        </el-form-item>
        <el-form-item label="选择群头像">
          <el-image :src="state.groupImgShow" :preview-src-list="[state.groupImgShow]"
          :preview-teleported="true" style="height:70px;width:70px;border-radius: 50%;"></el-image>
          <label for="upload" class="upload">
             <el-icon><Plus></Plus></el-icon>
            <input type="file" id="upload" style="display:none" @change="selectFile" accept="image/*">
          </label>
        </el-form-item>
        <el-form-item label="群描述信息">
           <el-input v-model="state.group.description" resize="none" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="群规模">
          <el-radio-group v-model="state.group.size" size="small">
            <el-radio-button v-for="(size, index) in GroupSizes" :key="index" :label="size" :value="size">
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="接受模式">
          <el-radio-group v-model="state.group.acceptMode">
            <el-radio label="无需申请直接加入" :value="2"></el-radio>
            <el-radio label="需要群主同意才可加入" :value="1"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button type="primary" plain @click="createGroup">创建</el-button>
        <el-button type="info" plain @click="state.groupEditShow = false">取消</el-button>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import { Search } from "@element-plus/icons-vue";
import { ipcRenderer } from 'electron';
import { DefaultGroupAvatar, GroupSizes, previewOpenFile } from '../modules/Common';
import { imgSrc } from '../modules/Request';
import stateStroge from '../modules/StateStorage';
import { CreateGroup, UploadGroupAvatar } from '../api/UserGroup';
import { ElNotification } from 'element-plus';
import { CreateHeadMessage } from '../api/ChatMessage';
import webSocket from '../modules/WebSocket';

const state = reactive<any>({
  group: {
    name: "",
    size: 20,
    acceptMode: 2,
    ownerId: "",
    avatar: DefaultGroupAvatar,
    description:""
  },
  groupEditShow: false,
  groupImgShow: "",
  selectedFile: null
});
const content = ref<string>();
const emits = defineEmits(["search","groupCreated"]);

onMounted(() => {
  const user = stateStroge.get("user");
  state.group.ownerId = user.id;
});

function onInput() {
  const query = content.value;
  emits("search", query);
}

function openSearch() {
  ipcRenderer.send("openSearch");
}

function openGroupEditor() {
  state.groupEditShow = true;
  state.groupImgShow = imgSrc(DefaultGroupAvatar);
}

function selectFile(e: any) {
  const target = e.target;
  const file = target.files[0];

  previewOpenFile(file, res => {
    state.selectedFile = file;
    state.groupImgShow = res;
  });
}

function clearGroupModel() {
  state.group.name = "";
  state.group.size = 20;
  state.group.acceptMode = 2;
  state.group.avatar = DefaultGroupAvatar;
  state.group.description = "";
  state.groupImgShow = "";
  state.selectedFile = null;
}

function createGroup() {
  CreateGroup(state.group, res => {
    if (state.selectedFile != null) {
      UploadGroupAvatar(res.data, state.group.avatar, state.selectedFile, () => {
        state.groupEditShow = false;
        created(res.data);
      });
    }
    else
      created(res.data);
  });

  function created(groupId: any) {
    const headMessage: any = {
      userId: state.group.ownerId,
      contactId: groupId,
      content: "",
      time: new Date(),
      contactName:state.group.name,
      contactAvatar:state.group.avatar
    };
    CreateHeadMessage(headMessage, (res) => {
      headMessage.id = res.data;
      ElNotification({
        message: "群组已创建！",
        type: "success"
      });
      emits("groupCreated",headMessage);
      state.groupEditShow = false;
      webSocket.reconnect();
    });
  }
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

.search-com .add {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 4px;
  background-color: white;
  border-radius: 5px;
  height: 25px;
  width: 25px;
}

.search-com .upload{
  margin-left: 3%;
  height: 70px;
  width: 70px;
  font-size: 38px;
  border: 2px dashed rgb(0,125,235);
  display: flex;
  align-items: center;
  justify-content: center;
  color:darkgray;
}
</style>