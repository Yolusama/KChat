import type { Result } from "../modules/Request";
import { api, authorization } from "./Api";


const applyApiPerren = "/Api/UserApply";

function applyApiUrl(url:string){
    return applyApiPerren+url;
}

export function MakeApply(apply:Record<string,any>,successCallback:(res:Result)=>void){
    api.put(applyApiUrl("/MakeApply"),authorization(),apply,successCallback);
}

export function GetUserApplies(userId:string,successCallback:(res:Result)=>void){
    api.get(applyApiUrl(`/GetUserApplies/${userId}`),authorization(),successCallback);
}

export function GetGroupApplies(userId:string,successCallback:(res:Result)=>void){
    api.get(applyApiUrl(`/GetGroupApplies/${userId}`),authorization(),successCallback);
}

export function SetApplyStatus(apply:Record<string,any>,successCallback:(res:Result)=>void){
    api.patch(applyApiUrl("/SetApplyStatus"),authorization(),apply,successCallback);
}