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
    private socket: WebSocket;
    private closeCallback: (event: CloseEvent) => void = defaultCloseCallback;

    constructor() {
        this.socket = this.assign();
    }

    assign() {
        const socket = new WebSocket(`${webSocketUrl}?userId=${user.id}&token=${user.token}`);
        socket.onopen = defaultOpenCallback;
        socket.onclose = (event) => {
            this.closeCallback(event);
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

    assignMessageCallback(callback:(event:MessageEvent<any>)=>void){
        this.socket.onmessage = callback;
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
    
    close() {
        if (this.socket.readyState == this.socket.OPEN || this.socket.readyState == this.socket.CONNECTING)
            webSocket.close();
    }
}

const webSocket = new KWebSocket();
export default webSocket;