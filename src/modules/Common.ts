import { ElMessageBox, type MessageBoxData, ElLoading } from "element-plus";
import { type NavigationGuardNext, type RouteLocationNormalizedGeneric } from "vue-router";
import { Route } from "./Route";


export function confirmDialog(title: string, content: string, confirmButtonText: string, cancelButtonText: string,
    successCallback: (res: MessageBoxData) => void, failCallback: () => void = () => { return; }) {
    ElMessageBox.confirm(
        content,
        title,
        {
            confirmButtonText: confirmButtonText,
            cancelButtonText: cancelButtonText
        }
    ).then(successCallback).catch(failCallback);
}

export const defalutLodingColor = "rgba(235,235,236,.75)";
export const defalutLodingColor1 = "rgb(23,25,24,.5)";

export function LoadingOperate(
    fullscreen: boolean,
    title: string,
    backgroundColor: string,
    func: () => void,
    expire: number,
    selector = "body"
) {
    const instance = ElLoading.service({
        fullscreen: fullscreen,
        target: selector,
        background: backgroundColor,
        text: title,
    });
    const timer = setTimeout(() => {
        instance.close();
        func();
        clearTimeout(timer);
    }, expire);
}

export function BeforeRouteLeave(to: RouteLocationNormalizedGeneric, from: RouteLocationNormalizedGeneric,
    next: NavigationGuardNext, notPrevented: boolean) {
    if (notPrevented) {
        next();
    }
    if (to.redirectedFrom != undefined) {
        Route.switch("#" + from.fullPath);
    }
    next(from);
}

export function copy(src: any, to: any) {
    for (const pro in src)
        to[pro] = src[pro];
}

export function delayToRun(func: () => void, expire: number) {
    const timer = setTimeout(() => {
        func();
        clearInterval(timer);
    }, expire);
}

export function onlyDate(date = new Date()) {
    const res = new Date(date);
    res.setHours(0);
    res.setMinutes(0);
    res.setSeconds(0);
    res.setMilliseconds(0);
    return res;
}

export function getTimeStr(date: Date) {
    const year = date.getFullYear();
    function withZeroStr(num: number): string {
        return num >= 10 ? num.toString() : "0" + num;
    }
    return `${year}-${withZeroStr(date.getMonth() + 1)}-${withZeroStr(date.getDate())}` +
        ` ${withZeroStr(date.getHours())}:${withZeroStr(date.getMinutes())}`;
}

export function swapArrayItem(array: any[], index1: number, index2: number) {
    const temp = array[index1];
    array[index1] = array[index2];
    array[index2] = temp;
}

export const oneSecond = 1000;

export type HeadMessage = {
    userId: string,
    contactId: string,
    content: string,
    time: Date
}

export interface ChatMessage {
    userId: string,
    contactId: string,
    content: string,
    time: Date,
    type: Number,
    fileName?: string,
    fileSize?: number
}

export const MessageType = {
    common: 1,
    image: 2,
    video:3,
    file: 4
}

export class PageOption {
    public current: number;
    public size: number;
    public total: number;
    public data: Array<any> | Record<string, any>;
    constructor(current: number, size: number, data: Array<any> | Record<string, any>) {
        this.current = current;
        this.size = size;
        this.total = 0;
        this.data = data;
    }
    count() {
        return Math.ceil(this.total / this.size);
    }
}

export enum UserApplyStatus {
    Verifying = 1, Accepted = 2, Ignored, Refused
}

export enum GroupContactStatus {
    KickOut = 2, Dismissed = 3
}

export const GroupSizes = [20, 50, 100, 200, 500, 1000, 1500, 2000];
export const DefaultGroupAvatar = "default-group.jpg";

export function previewOpenFile(file: any, func: (reuslt: any) => void) {
    if (file) {
        var reader = new FileReader();

        reader.onload = function (e: any) {
            func(e.target.result);
        };
        reader.readAsDataURL(file);
    }
}

export function timeWithoutSeconds(date: Date) {
    function withZeroStr(num: number): string {
        return num >= 10 ? num.toString() : "0" + num;
    }

    return `${withZeroStr(date.getHours())}:${withZeroStr(date.getMinutes())}`;
}

export function getFileSuffix(fileName:string){
    const index = fileName.lastIndexOf('.');
    if(index<0)
        return "";
    else
       return fileName.substring(index+1);
}

export function playNotifyAudio(){
    const audio = new Audio("src/assets/recev-msg.ogg");
    audio.muted = false;
    audio.volume = 1;
    audio.play().catch(err=>console.log(err));
}

export const KB = 1024;
export const MB = KB*KB;
export const GB = MB*MB;

export function getFileSize(fileSize:number){
    if(fileSize<KB)
        return `${fileSize}B`;
    else if(fileSize>=KB&&fileSize<MB)
        return `${(fileSize/KB).toFixed(1)}KB`;
    else if(fileSize>=MB&&fileSize<=GB)
        return `${(fileSize/MB).toFixed(1)}MB`;
    else
       return `${(fileSize/GB).toFixed(1)}GB`;
}