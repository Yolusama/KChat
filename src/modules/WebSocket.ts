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

const defalutOpenCallback = () => {
    console.log("WebSocket已打开...");
}
const user = stateStroge.get("user");

let _socket:WebSocket;
export function assignWebSocket(openCallback = defalutOpenCallback, closeCallback = defaultCloseCallback,
    errorCallback = defaultErrorCallback) {
    const socket = new WebSocket(`${webSocketUrl}?userId=${user.id}&token=${user.token}`);
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
    _socket = socket;
    return socket;
}

export function assignMessageCallback(messageCallback: ((message: MessageEvent<any>) => void) | null) {
    _socket.onmessage = messageCallback;
}

export function sendMessage(message: any, callback: () => void) {
    try {
        _socket.send(JSON.stringify(message));
        delayToRun(callback, 50);
    }
    catch (e) {
        console.log(e);
    }
}