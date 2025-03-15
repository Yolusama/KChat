import { ElNotification } from "element-plus";
import { webSocketUrl } from "./Request";
import stateStroge from "./StateStorage";
import { delayToRun } from "./Common";

const defaultErrorCallback = () => {
    ElNotification({
        message: "WebSocket连接或处理消息时出现问题!",
        type: "error"
    });
}

const defaultCloseCallback = () => {
    console.log("WebSocket关闭中...");
}

const defaultOpenCallback = () => {
    console.log("WebSocket已打开...");
}
const user = stateStroge.get("user");

class KWebSocket {
    messagingFuncs: Record<string, (event: MessageEvent<any>) => void> = {};
    private socket: WebSocket;
    private closeCallback: (event: CloseEvent) => void = defaultCloseCallback;

    constructor() {
        this.socket = this.assign();
    } 

    assign() {
        const socket = new WebSocket(`${webSocketUrl}?userId=${user.id}&token=${user.token}`);
        socket.onopen = defaultOpenCallback;
        socket.onmessage = (event) => {
            for (let name in this.messagingFuncs)
                this.messagingFuncs[name](event);
        };
        socket.onclose = (event) => {
            this.closeCallback(event);
            this.messagingFuncs = {};
            socket.onopen = null;
            socket.onmessage = null;
            socket.onerror = null;
            socket.onclose = null;
        };
        socket.onerror = defaultErrorCallback;
        return socket;
    }

    assignOpenCallback(callback: (event: Event) => void) {
        this.socket.onopen = callback;
    }

    assignCloseCallback(callback: (event: CloseEvent) => void) {
        this.closeCallback = callback;
    }

    assignErrorCallback(callback: (event: Event) => void) {
        this.socket.onerror = callback;
    }

    sendMessage(message: any, callback: () => void) {
        try {
            this.socket.send(JSON.stringify(message));
            delayToRun(callback, 5);
        }
        catch (e) {
            console.log(e);
        }
    }

    addMsgFunc(func: (event: MessageEvent<any>) => void) {
        this.messagingFuncs[func.name] = func;
    }

    close() {
        if (this.socket.readyState == this.socket.OPEN || this.socket.readyState == this.socket.CONNECTING)
            webSocket.close();
    }

    removeMsgFunc(name:string) {
        const data: Record<string, any> = {};
        for (let funcName in this.messagingFuncs)
            if (name != funcName)
                data[funcName] = this.messagingFuncs[funcName];
        this.messagingFuncs = data;
    }
}

const webSocket = new KWebSocket();
export default webSocket;