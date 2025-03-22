import { ElNotification } from "element-plus";
import { webSocketUrl } from "./Request";
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

class KWebSocket {
    private socket: any = null;
    private closeCallback: (event: CloseEvent) => void = defaultCloseCallback;
    private user: any = null;

    constructor() {

    }

    assign(userId: string, token: string) {
        if (this.user == null) {
            this.user = {
                id: userId,
                token: token
            };
        }
        const socket = new WebSocket(`${webSocketUrl}?userId=${userId}&token=${token}`);
        socket.onopen = defaultOpenCallback;
        socket.onclose = (event) => {
            this.closeCallback(event);
            socket.onopen = null;
            socket.onmessage = null;
            socket.onerror = null;
            socket.onclose = null;
        };
        socket.onerror = defaultErrorCallback;
        this.socket = socket;
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

    assignMessageCallback(callback: (event: MessageEvent<any>) => void) {
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
            this.socket.close();
    }

    reconnect() {
        this.close();
        delayToRun(() => this.assign(this.user.id, this.user.token), 15);
    }
}

const webSocket = new KWebSocket();
export default webSocket;