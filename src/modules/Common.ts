import { ElMessageBox, type MessageBoxData, ElLoading } from "element-plus";
import { type NavigationGuardNext,type RouteLocationNormalizedGeneric } from "vue-router";
import { Route } from "./Route";


export function confirmDialog(title: string, content: string, confirmButtonText: string, cancelButtonText: string,
    successCallback: (res: MessageBoxData) => void, failCallback:()=>void = () => { return; }) {
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

export function swapArrayItem(array:any[], index1:number, index2:number) {
    const temp = array[index1];
    array[index1] = array[index2];
    array[index2] = temp;
}

export const oneSecond = 1000;