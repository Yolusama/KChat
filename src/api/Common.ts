import type { Result } from "../modules/Request";
import { api, authorization } from "./Api";

const commonApiPattern = "/Api/Common";

function commonApiUrl(url:string){
    return commonApiPattern + url;
}

export function ChangeRemark(userId:string,contactId:string,remark:string,successCallback:(res:Result)=>void){
    api.post(commonApiUrl(`/ChangeRemark/${userId}/${contactId}?remark=${remark}`),authorization(),{},successCallback);
}