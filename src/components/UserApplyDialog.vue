<template>
  <el-dialog v-model="state.show" :title="isGroup ? '申请加入群聊' : '申请添加为好友'" @close="closed">
    <div class="apply">
      <div class="info">
        <el-image :src="imgSrc(avatar)" :preview-src-list="[imgSrc(avatar)]" fit="cover" :preview-teleported="true"
          class="avatar"></el-image>
        <span>{{ name }}</span>
      </div>
      <el-form label-position="right" label-width="auto">
        <el-form-item label="验证信息">
          <el-input type="textarea" :rows="3" v-model="state.info" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="state.remark"></el-input>
        </el-form-item>
        <el-form-item label="好友分组" v-if="!isGroup">
          <el-select v-model="state.labelId">
            <el-option v-for="item in labels" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
          <div @click="addLabel">
            <el-icon>
              <Plus />
            </el-icon>添加分组
          </div>
        </el-form-item>
        <el-button type="primary" @click="send">发送</el-button>
        <el-button @click="state.show = false">取消</el-button>
      </el-form>
    </div>
  </el-dialog>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
import { CreateLabel } from "../api/User";
import stateStroge from "../modules/StateStorage";
import { imgSrc } from "../modules/Request";
import { MakeApply, MakeGroupApply } from "../api/UserApply";
import { ElMessageBox, ElNotification } from "element-plus";

const state = reactive<any>({
  show: false,
  labels: [],
  info: "",
  remark: "",
  avatar: "",
  labelId: 0,
  userId: "",
});

const emits = defineEmits(["close", "update:labels"]);

const pros = defineProps({
  isGroup: Boolean,
  name: String,
  avatar: String,
  contactId: String,
  labels: Array,
  groupOwnerId: String
});

const isGroup = ref<boolean>(pros.isGroup);
const name = ref(pros.name);
const avatar = ref<any>(pros.avatar);
const contactId = ref<any>(pros.contactId);
const labels = ref<any>(pros.labels);
const groupOwnerId = ref<any>(pros.groupOwnerId);

onMounted(() => {
  const user = stateStroge.get("user");
  state.userId = user.id;
  state.labelId = labels.value[0].id;
});

function send() {
  if (!isGroup.value) {
    MakeApply(
      {
        userId: state.userId,
        contactId: contactId.value,
        info: state.info,
      },
      (res) => {
        ElNotification({
          message: res.message,
          type: "success",
        });
        state.show = false;
      }
    );
  }
  else {
    MakeGroupApply(groupOwnerId.value, {
      userId: state.userId,
      contactId: contactId.value,
      info: state.info,
    }, res => {
      ElNotification({
        message: res.message,
        type: "success",
      });
      state.show = false;
    });
  }
}

function addLabel() {
  ElMessageBox.prompt("新建分组", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  })
    .then((value) => {
      CreateLabel(state.userId, value, (res) => {
        const index = labels.value.findIndex((l: any) => l.id == res.data.id);
        if (index < 0) {
          labels.value.push(res.data);
          emits("update:labels", labels.value);
        }
      });
    })
    .catch(() => {
      return;
    });
}


function open() {
  state.show = true;
}

function closed() {
  emits("close");
}

defineExpose({
  open,
});
</script>

<style scoped>
.apply {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 500px;
  overflow-y: auto;
}

.apply .info {
  color: gray;
  font-size: 13px;
  display: flex;
  align-items: center;
  margin-bottom: 7px;
}

.apply .avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 5px;
}
</style>
