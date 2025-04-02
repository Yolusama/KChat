import type { Result } from "../modules/Request";
import { api, authorization } from "./Api";

const groupApiPattern = "/Api/Group";

const groupApiUrl = (url:string)=>{
    return groupApiPattern + url;
}

export function GetUserGroups(userId:string,successCallback:(res:Result)=>void){
    api.get(groupApiUrl(`/GetGroups/${userId}`),authorization(),successCallback);
}

export function CreateGroup(group:any,successCallback:(res:Result)=>void){
    api.put(groupApiUrl("/CreateGroup"),authorization(),group,successCallback);
}

export function UploadGroupAvatar(groupId:string,avatar:string,file:any,successCallback:(res:Result)=>void){
    const data = new FormData();
    data.append("avatar",avatar);
    data.append("file",file);
    api.post(groupApiUrl(`/UploadAvatar/${groupId}`),authorization(true),data,successCallback);
}

export function SearchGroup(userId:string,identifier:string,successCallback:(res:Result)=>void){
    api.get(groupApiUrl(`/SearchGroup/${userId}?identifier=${identifier}`),authorization(),successCallback);
}

export function ChangeDescription(groupId:string,description:string,successCallback:(res:Result)=>void){
    api.patch(groupApiUrl(`/ChangeDescription/${groupId}?description=${description}`),authorization(),{},successCallback);
}

export function QuitGroup(userId:string,groupId:string,successCallback:(res:Result)=>void){
    api.delete(groupApiUrl(`/QuitGroup/${userId}/${groupId}`),authorization(),successCallback);
}
