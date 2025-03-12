import type { Result } from "../modules/Request";
import { api, authorization } from "./Api";

const groupApiPattern = "/Api/Group";

const groupApiUrl = (url:string)=>{
    return groupApiPattern + url;
}

export function GetUserGroups(userId:string,successCallback:(res:Result)=>void){
    api.get(groupApiUrl(`/GetGroups/${userId}`),authorization(),successCallback);
}