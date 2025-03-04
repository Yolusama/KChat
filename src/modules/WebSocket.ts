import { ElNotification } from "element-plus";
import { webSocketUrl } from "./Request";

const defaultErrorCallback = () => {
    ElNotification({
        message: "WebSocket连接或处理消息时出现问题!",
        type: "error"
    });
}

const defaultCloseCallback = () => {
    console.log("WebSocket关闭中...");
}

const defalutOpenCallback = () => {
    console.log("websocket opens...");
}
const socket = new WebSocket(webSocketUrl);

export function assignWebSocket(openCallback = defalutOpenCallback, closeCallback = defaultCloseCallback,
    errorCallback = defaultErrorCallback) {
    socket.onopen = openCallback;
    socket.onmessage = null;
    socket.onclose = () => {
        closeCallback();
        socket.onopen = null;
        socket.onmessage = null;
        socket.onerror = null;
        socket.onclose = null;
    };
    socket.onerror = errorCallback;
}

export function assignMessageCallback(messageCallback: (message: MessageEvent<any>) => void) {
    if (socket.onmessage == null)
        socket.onmessage = messageCallback;
}

export function sendMessage(message: any) {
    socket.send(JSON.stringify(message));
}

export function closeWebSocket() {
    if (socket.readyState == socket.CONNECTING || socket.readyState == socket.OPEN)
        socket.close();
}