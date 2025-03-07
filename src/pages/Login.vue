<template>
    <app-header :inLoginCom="true"></app-header>
    <div id="login">
        <el-form v-if="state.isLogin" label-position="right" label-width="auto">
            <el-form-item label="账户/电子邮箱">
                <el-input v-model="state.user.account"></el-input>
            </el-form-item>
            <el-form-item label="密码">
                <el-input v-model="state.user.password" show-password @input="pwdInput"></el-input>
            </el-form-item>
            <el-button type="primary" @click="login">
                登录
            </el-button>
            <el-button type="info" @click="switchReg">
                注册
            </el-button>
        </el-form>
        <el-form v-if="!state.isLogin" :rules="regRules" label-position="right" label-width="auto">
            <el-form-item label="电子邮箱" prop="email">
                <el-input v-model="state.user.email" :prefix-icon="Message" maxlength="25"></el-input>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
                <el-input v-model="state.user.nickname" maxlength="25"></el-input>
            </el-form-item>
            <el-form-item label="性别">
                <el-radio-group v-model="state.user.gender">
                    <el-radio label="男" :value="0"></el-radio>
                    <el-radio label="女" :value="1"></el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="设置密码" prop="password">
                <el-input v-model="state.user.password" show-password clearable :prefix-icon="Lock" minlength="6"
                    maxlength="20" @input="pwdInput"></el-input>
            </el-form-item>
            <el-form-item label="验证码" prop="checkCode">
                <el-input v-model="state.checkCode" maxlength="6">
                    <template #append>
                        <el-button :disabled="state.hasGotCode" @click="getCheckCode">{{ state.checkCodeText
                        }}</el-button>
                    </template>
                </el-input>
            </el-form-item>
            <el-button type="primary" @click="register">确定</el-button>
            <el-button type="info" @click="switchLogin">返回</el-button>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { ipcRenderer } from 'electron';
import { onMounted, reactive, ref } from 'vue';
import { GetCheckCode, Login, Register, VerifyToken } from '../api/User';
import stateStroge from '../modules/StateStorage';
import { Route } from '../modules/Route';
import { defalutLodingColor1, LoadingOperate, oneSecond } from '../modules/Common';
import { ElMessage, type FormRules } from 'element-plus';
import { Message, Lock } from "@element-plus/icons-vue";

const state = reactive({
    user: {
        account: "",
        password: "",
        email: "",
        nickname: "",
        gender: 0
    },
    checkCode: "",
    isLogin: true,
    checkCodeText: "获取验证码",
    hasGotCode: false,
    regFormValid: {
        e: false,
        p: false,
        n: false,
        c: false,
        valided: function () {
            return this.e && this.n && this.p && this.c;
        }
    }
});
const regRules = ref<FormRules>({
    nickname: [
        { required: true, validator: nicknameValidate, trigger: "blur" }
    ],
    email: [
        {
            required: true,
            validator: emailValidate,
            trigger: "blur"
        }
    ],
    checkCode: [
        { required: true, validator: checkCodeValidate, trigger: "blur" },
    ],
    password: [{
        required: true, validator: passwordValidate, trigger: "blur"
    }]
});

onMounted(function () {
    if (stateStroge.has("user")) {
        const user = stateStroge.get("user");
        VerifyToken(user.id, user.token, res => {
            if (!res.data) {
                ElMessage({
                    message: res.message,
                    type: "info"
                });
                stateStroge.clear();
                ipcRenderer.send("setSize", 0, 0, false);
            }
            else {
                ipcRenderer.send("setSize", 0, 0, false);
                Route.switch("#/Home");
            }
        }, () => {
            stateStroge.clear();
            ipcRenderer.send("setSize", 0, 0, false);
        });
    }
    else
        ipcRenderer.send("setSize", 0, 0, false);
});

function login() {
    Login(state.user.account, state.user.password, res => {
        const data = res.data;
        stateStroge.set("user", data);
        LoadingOperate(false, "登录中...", defalutLodingColor1, () => Route.switch("#/Home"), 2000);
    });
}

function getCheckCode() {
    GetCheckCode(6, state.user.email, () => {
        state.hasGotCode = true;
        let i = 60;
        const timer = setInterval(() => {
            if (i == 0) {
                state.checkCodeText = "获取验证码";
                clearInterval(timer);
                state.hasGotCode = false;
                i = 60;
            }
            else
                state.checkCodeText = `${i--}s`;
        }, oneSecond)
    });
}

async function register() {
    if (!state.regFormValid.valided()) return;
    Register({
        nickname: state.user.nickname,
        email: state.user.email,
        password: state.user.password,
        gender: state.user.gender
    }, state.checkCode, (res) => {
        state.isLogin = true;
        clearReg();
        state.user.account = res.data;
        ElMessage({
            message: `你的账号：${res.data}`,
            type: "success"
        });
    });
}

function clearReg() {
    state.checkCode = "";
    state.hasGotCode = false;
    state.checkCodeText = "获取验证码";
    state.user.gender = 0;
}

function emailValidate(rule: any, value: any, callback: any) {
    const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!reg.test(state.user.email)) {
        callback(new Error("邮箱格式错误！"));
        state.regFormValid.e = false;
    }
    else
        state.regFormValid.e = true;
}

function nicknameValidate(rule: any, value: any, callback: any) {
    if (state.user.nickname.length == 0) {
        callback(new Error("用户昵称不能为空！"));
        state.regFormValid.n = false;
    }
    else
        state.regFormValid.n = true;
}

function checkCodeValidate(rule: any, value: any, callback: any) {
    if (state.checkCode.length == 0) {
        callback(new Error("请输入验证码！"));
        state.regFormValid.c = false;
    }
    else
        state.regFormValid.c = true;

}

function passwordValidate(rule: any, value: any, callback: any) {
    const reg = /\s/;
    if (state.user.password.length < 6 || state.user.password.length > 20) {
        callback(new Error("密码长度必须在6-20之间！"));
        state.regFormValid.p = false;
    }
    else
        state.regFormValid.p = true;
}

function pwdInput() {
    const value = state.user.password;
    const reg = /\s/;
    state.user.password = value.replace(reg, "");
}

function switchLogin() {
    state.isLogin = true;
    clearReg();
}

function switchReg() {
    state.user.password = "";
    state.isLogin = false;
}

</script>

<style scoped>
#login {
    position: relative;
    -webkit-app-region: no-drag;
    width: 90%;
    margin: 0 auto;
}

#login .el-input {
    width: 90%;
}
</style>